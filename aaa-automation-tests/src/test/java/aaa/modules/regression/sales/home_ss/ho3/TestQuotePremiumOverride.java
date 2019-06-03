package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * <p> Created by lkazarnovskiy on 8/11/2017.
 * <p> TC Steps:
 * <p> 1. Create new or open existent Customer;
 * <p> 2. Start HSS quote creation;
 * <p> 3. Fill all mandatory fields;
 * <p> 4. Calculate premium.
 * <p> 4. On Premium and coverage tab change "Coverage F" and check that calculated premium resets to zero.
 * <p> 5. Calculate premium.
 * <p> 6. Invoke Override Premium Dialog.
 * <p> 7. Override premium by -101%, check error message appears. Cancel Override.
 * <p> 8. Override premium by Percentage (20%), check calculated values. Cancel Override.
 * <p> 9. Override premium by Flat Amount ($400), check calculated values. Cancel Override.
 * <p> 10.Override premium by Percentage (20%), check calculated values. Confirm Override.
 * <p> 11.Check override success message.
 * <p> 12.Issue Policy;
 * <p> 13.Check Policy status is Active.
 * <p> 14.Check Policy premium summary.
 * <p> // Stories
 * <p> 13736:US NB - Calculate Premium - Premium Override
 * <p> 14891:US Calculate Premium - reset premium after change
 */
public class TestQuotePremiumOverride extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA, States.NJ })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuotePremiumOverride(@Optional("") String state) {

		TestData td = getPolicyTD();
		TestData tdCoverageF = DataProviderFactory.dataOf("Coverage F - Medical Payments to Others", "index=2");
		TestData tdOverridePremiumP = getTestSpecificTD("TestData_Percentage");
		TestData tdOverridePremiumF = getTestSpecificTD("TestData_Flat");

		PremiumsAndCoveragesQuoteTab pcTab = new PremiumsAndCoveragesQuoteTab();

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);

//		On Premium and coverage tab change "Coverage F" and check that calculated premium resets to zero.
		pcTab.fillTab(DataProviderFactory.dataOf(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), tdCoverageF), false);
		PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.zero();

//		Override premium by -101%, check error message appears.
		pcTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.btnOverridePremium.click();
		pcTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.OVERRRIDE_PREMIUM_DIALOG).fill(tdOverridePremiumP.getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName()), false);
		assertThat(PremiumsAndCoveragesQuoteTab.lblErrorMessage).isPresent();
		assertThat(PremiumsAndCoveragesQuoteTab.lblErrorMessage).hasValue("The premium cannot be decreased by more than 100%.");

		TestData adjustedOverridePTestData = tdOverridePremiumP.adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab", "Override Premium", "Percentage"), "20").resolveLinks().getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName());
		pcTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.OVERRRIDE_PREMIUM_DIALOG).fill(adjustedOverridePTestData,false);
		PremiumsAndCoveragesQuoteTab.calculatedOverrideFlatAmount().verify.equals(PremiumsAndCoveragesQuoteTab.getOverridenPremiumFlatAmount());

//		9. Override premium by Flat Amount ($400), check calculated values.
		pcTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.OVERRRIDE_PREMIUM_DIALOG).fill(tdOverridePremiumF.getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName()), false);
		//CustomAssert.assertTrue(PremiumsAndCoveragesQuoteTab.calculatedOverridePercentageAmount().equals(PremiumsAndCoveragesQuoteTab.getOverridenPremiumPercentageAmount()));
		assertThat(PremiumsAndCoveragesQuoteTab.getOverridenPremiumPercentageAmount()).isEqualTo(PremiumsAndCoveragesQuoteTab.calculatedOverridePercentageAmount());
		PremiumsAndCoveragesQuoteTab.dialogOverridePremium.reject();

//		10. Override premium by Percentage (20%), check calculated values. Confirm Override.
		PremiumsAndCoveragesQuoteTab.btnOverridePremium.click();
		TestData adjustedOverrideFTestData = tdOverridePremiumP.adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab", "Override Premium", "Percentage"), "20").getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName());
		pcTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.OVERRRIDE_PREMIUM_DIALOG).fill(adjustedOverrideFTestData,false);
		PremiumsAndCoveragesQuoteTab.calculatedOverrideFlatAmount().verify.equals(PremiumsAndCoveragesQuoteTab.getOverridenPremiumFlatAmount());

		PremiumsAndCoveragesQuoteTab.dialogOverridePremium.confirm();
		PremiumsAndCoveragesQuoteTab.dialogOverrideConfirmation.confirm();
		assertThat(PremiumsAndCoveragesQuoteTab.lblOverridenPremium).valueContains("Original term premium has been overridden.");
		log.info("Override message is displayed on Premium&Coverages tab");
		pcTab.submitTab();

		policy.getDefaultView().fillFromTo(td, MortgageesTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}
}
