package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.DiscountEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.etcsa.utils.Dollar;

public class TestZeroClaimsDiscountTemplate extends PolicyBaseTest {

	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

	protected void pas9088_testZeroClaimsDiscountQuote() {

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class, true);

		validateDiscountAndPremiumWithClosedClaim();
		validateDiscountAndPremiumWithOpenClaim();

	}

	protected void pas9088_testZeroClaimsDiscountRenewal() {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		policy.renew().perform();
		premiumsAndCoveragesQuoteTab.calculatePremium();

		validateDiscountAndPremiumWithClosedClaim();
		validateDiscountAndPremiumWithOpenClaim();

	}

	private void validateDiscountAndPremiumWithClosedClaim() {
		// Validate the Zero Claims Discount is present and capture Premium
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isTrue();
		Dollar premium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());

		// Add a claim, calculate premium
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(getClaimTD());

		// Validate discount is removed and premium increases
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isFalse();
		new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue()).verify.moreThan(premium);

	}

	private void validateDiscountAndPremiumWithOpenClaim() {
		// Capture current premium
		Dollar premium = new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue());

		// Navigate back to Property Info tab and change claim to 'Open'
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.CLAIM_STATUS).setValue("Open");

		// Validate discount is added and premium decreases
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.isDiscountApplied(DiscountEnum.HomeSSDiscounts.ZERO_PRIOR_CLAIMS.getName())).isTrue();
		new Dollar(PremiumsAndCoveragesQuoteTab.tableTotalPremiumSummary.getRow(1).getCell(2).getValue()).verify.lessThan(premium);

	}

	private TestData getClaimTD() {
		String claimHistoryKeyPath = TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel());
		return getPolicyTD()
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.ADD_A_CLAIM.getLabel()), "Yes")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.DATE_OF_LOSS.getLabel()), "$<today-1y:MM/dd/yyyy>")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.CAUSE_OF_LOSS.getLabel()), "index=1")
				.adjust(TestData.makeKeyPath(claimHistoryKeyPath, HomeSSMetaData.PropertyInfoTab.ClaimHistory.AMOUNT_OF_LOSS.getLabel()), "2000")
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
