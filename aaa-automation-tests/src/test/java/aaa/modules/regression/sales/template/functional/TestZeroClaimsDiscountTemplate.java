package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.DiscountEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static toolkit.verification.CustomAssertions.assertThat;

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
		payTotalAmtDue("test");

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

		// PAS-6730 OPEN CLAIMS ARE USED. Change claim to 'Open' and validate discount/premium is NOT added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Open");
		premium = validateDiscountAddedAndPremiumDoesNotChange(premium);

		// Change claim to back to 'Closed' and validate discount/premium is not changed
		premium = changeClaimToClosedAndValidate(premium);

		// PAS-6730 Subrogated CLAIMS ARE USED. Change claim to 'Subrogated' and validate discount/premium is NOT added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Subrogation");
		premium = validateDiscountAddedAndPremiumDoesNotChange(premium);

		// Change claim to back to 'Closed' and validate discount/premium is not changed
		premium = changeClaimToClosedAndValidate(premium);

		// PAS-6730 limit changed to 5 years. Change claim Date to 6 years ago and validate discount/premium is added/decreased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		String claimDateSixYrs = effectiveDate.minusYears(6).format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(claimDateSixYrs);
		premium = validateDiscountAddedAndPremiumIsDecreased(premium);

		// PAS-6730 limit changed to 5 years. Change date back to less than 6 years ago and validate discount/premium is removed/increased
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS).setValue(dateOfLoss);
		premium = validateDiscountRemovedAndPremiumIncreases(premium);

	}

	private Dollar validateDiscountRemovedAndPremiumIncreases(Dollar premium) {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isFalse();
		Dollar newPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());
		newPremium.verify.moreThan(premium);
		return newPremium;
	}

	private Dollar validateDiscountAddedAndPremiumDoesNotChange(Dollar premium) {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isFalse();
		Dollar newPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());
		newPremium.verify.equals(premium);
		return newPremium;
	}

	private Dollar validateDiscountAddedAndPremiumIsDecreased(Dollar premium) {
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isTrue();
		Dollar newPremium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());
		newPremium.verify.lessThan(premium);
		return newPremium;
	}

	private Dollar changeClaimToClosedAndValidate(Dollar premium) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Closed");
		return validateDiscountAddedAndPremiumDoesNotChange(premium);
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