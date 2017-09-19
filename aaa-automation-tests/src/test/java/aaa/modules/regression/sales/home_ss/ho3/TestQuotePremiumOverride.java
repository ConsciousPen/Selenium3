package aaa.modules.regression.sales.home_ss.ho3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * Created by lkazarnovskiy on 8/11/2017.
 * TC Steps:
 * 1. Create new or open existent Customer;
 * 2. Start HSS quote creation;
 * 3. Fill all mandatory fields;
 * 4. Calculate premium.
 * 4. On Premium and coverage tab change "Coverage F" and check that calculated premium resets to zero.
 * 5. Calculate premium.
 * 6. Invoke Override Premium Dialog.
 * 7. Override premium by -101%, check error message appears. Cancel Override.
 * 8. Override premium by Percentage (20%), check calculated values. Cancel Override.
 * 9. Override premium by Flat Amount ($400), check calculated values. Cancel Override.
 * 10.Override premium by Percentage (20%), check calculated values. Confirm Override.
 * 11.Check override success message.
 * 12.Issue Policy;
 * 13.Check Policy status is Active.
 * 14.Check Policy premium summary.
 *  // Stories
 * 13736:US NB - Calculate Premium - Premium Override
 * 14891:US Calculate Premium - reset premium after change
 *
 */
public class TestQuotePremiumOverride extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
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
		pcTab.fillTab(DataProviderFactory.dataOf(new PremiumsAndCoveragesQuoteTab().getMetaKey(), tdCoverageF), false);
		PremiumsAndCoveragesQuoteTab.getPolicyTermPremium().verify.zero();

//		Override premium by -101%, check error message appears.
		pcTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.btnOverridePremium.click();
		pcTab.fillTab(tdOverridePremiumP,false);
		PremiumsAndCoveragesQuoteTab.lblErrorMessage.verify.present();
		PremiumsAndCoveragesQuoteTab.lblErrorMessage.verify.value("The premium cannot be decreased by more than 100%.");

		pcTab.fillTab(tdOverridePremiumP.adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab","Override Premium","Percentage"),"20"),false);
		PremiumsAndCoveragesQuoteTab.calculatedOverrideFlatAmount().verify.equals(PremiumsAndCoveragesQuoteTab.getOverridenPremiumFlatAmount());

//		9. Override premium by Flat Amount ($400), check calculated values.
		pcTab.fillTab(tdOverridePremiumF, false);
		//CustomAssert.assertTrue(PremiumsAndCoveragesQuoteTab.calculatedOverridePercentageAmount().equals(PremiumsAndCoveragesQuoteTab.getOverridenPremiumPercentageAmount()));
		CustomAssert.assertEquals(PremiumsAndCoveragesQuoteTab.calculatedOverridePercentageAmount(), PremiumsAndCoveragesQuoteTab.getOverridenPremiumPercentageAmount());
		PremiumsAndCoveragesQuoteTab.dialogOverridePremium.reject();

//		10. Override premium by Percentage (20%), check calculated values. Confirm Override.
		PremiumsAndCoveragesQuoteTab.btnOverridePremium.click();
		pcTab.fillTab(tdOverridePremiumP.adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab","Override Premium","Percentage"),"20"),false);
		PremiumsAndCoveragesQuoteTab.calculatedOverrideFlatAmount().verify.equals(PremiumsAndCoveragesQuoteTab.getOverridenPremiumFlatAmount());

		PremiumsAndCoveragesQuoteTab.dialogOverridePremium.confirm();
		PremiumsAndCoveragesQuoteTab.dialogOverrideConfirmation.confirm();
		PremiumsAndCoveragesQuoteTab.lblOverridenPremium.verify.contains("Original term premium has been overridden.");
		log.info("Override message is displayed on Premium&Coverages tab");
		pcTab.submitTab();

		policy.getDefaultView().fillFromTo(td, MortgageesTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}
}
