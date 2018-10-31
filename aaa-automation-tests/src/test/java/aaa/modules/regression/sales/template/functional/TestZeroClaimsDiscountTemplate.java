package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.DiscountEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.etcsa.utils.Dollar;

public class TestZeroClaimsDiscountTemplate extends PolicyBaseTest {

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
	private LocalDateTime effectiveDate;
	private String dateOfLoss;

	protected void pas9088_testZeroClaimsDiscountQuote() {

		createQuoteAndFillUpTo(PremiumsAndCoveragesQuoteTab.class);
		effectiveDate = new PremiumsAndCoveragesQuoteTab().getEffectiveDate();

		validateDiscountAndPremiumChange();

	}

	protected void pas9088_testZeroClaimsDiscountRenewal() {

		openAppAndCreatePolicy();
		policy.renew().perform();
		effectiveDate =new GeneralTab().getEffectiveDate();
		premiumsAndCoveragesQuoteTab.calculatePremium();

		validateDiscountAndPremiumChange();

	}

	private void validateDiscountAndPremiumChange() {
		// Validate the Zero Claims Discount is present and capture Premium
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isTrue();
		Dollar premium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());

		// Add a claim, calculate premium
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(getClaimTD());

		// Validate discount is removed and premium increases
		premium = validateDiscountRemovedAndPremiumIncreases(premium);

		// Change claim to 'Open' and validate discount/premium is added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Open");
		premium = validateDiscountAddedAndPremiumDecreases(premium);

		// Change claim to back to 'Closed' and validate discount/premium is removed/increased
		premium = changeClaimToClosedAndValidate(premium);

		// Change claim to 'Subrogated' and validate discount/premium is added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Subrogation");
		premium = validateDiscountAddedAndPremiumDecreases(premium);

		// Change claim to back to 'Closed' and validate discount/premium is removed/increased
		premium = changeClaimToClosedAndValidate(premium);

		// Change claim Date to 4 years ago and validate discount/premium is added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		String claimDateFourYrs = effectiveDate.minusYears(4).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(claimDateFourYrs);
		premium = validateDiscountAddedAndPremiumDecreases(premium);

		// Change date back to less than 4 years ago and validate discount/premium is removed/increased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(dateOfLoss);
		premium = validateDiscountRemovedAndPremiumIncreases(premium);

		// Change claim amount to less than $1000 and validate discount/premium is added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS).setValue("1000");
		validateDiscountAddedAndPremiumDecreases(premium);

	}

	private Dollar validateDiscountRemovedAndPremiumIncreases(Dollar premium) {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isFalse();
		Dollar newPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());
		newPremium.verify.moreThan(premium);
		return newPremium;
	}

	private Dollar validateDiscountAddedAndPremiumDecreases(Dollar premium) {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isTrue();
		Dollar newPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());
		newPremium.verify.lessThan(premium);
		return newPremium;
	}

	private Dollar changeClaimToClosedAndValidate(Dollar premium) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Closed");
		return validateDiscountRemovedAndPremiumIncreases(premium);
	}

	private TestData getClaimTD() {
		String claimHistoryKeyPath = TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel());
		dateOfLoss = effectiveDate.minusYears(4).plusDays(1).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		return getPolicyTD()
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.BTN_ADD.getLabel()), "Click")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()), dateOfLoss)
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel()), "index=1")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel()), "1001")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS.getLabel()), "Closed")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.AAA_CLAIM.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.CATASTROPHE_LOSS.getLabel()), "No")
				.mask(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel()))
				.mask(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel()))
				.mask(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel()))
				.mask(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.INTERIOR.getLabel()))
				.mask(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.HOME_RENOVATION.getLabel()))
				.mask(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.RENTAL_INFORMATION.getLabel()));
	}

}
