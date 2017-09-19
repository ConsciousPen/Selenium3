package aaa.modules.regression.sales.auto_ss;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestQuoteDiscounts extends AutoSSBaseTest {

	/**
	 * Membership discount
	 * Enter Membership number on General tab
	 * check that Membership discount is applying on Premium & Coverage page
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testMembershipDiscount(@Optional("") String state) {
		TestData td = getPolicyTD();
		GeneralTab generalTab = new GeneralTab();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		//'Current AAA Member' should be filled with 'Membership Pending' value (or 'Yes)
		policy.getDefaultView().fillUpTo(td
				.adjust(getTestSpecificTD("MembershipDiscount")).resolveLinks(), PremiumAndCoveragesTab.class, true);

		//Verify that discount is present
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));

		//Delete conditions for discount and verify is it displayed
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Membership Discount"));
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		log.info("Membership discount is verified successfully");
	}


	/**
	 * Loyalty discount
	 * Enter Current Carrier on General tab
	 * check that Loyalty Discount discount is applying on Premium & Coverage page
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testLoyaltyDiscount(@Optional("") String state) {

		TestData td = getPolicyTD();
		GeneralTab generalTab = new GeneralTab();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		// 'Current AAA Member' field on General tab should be set to YES for this discount
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);

		//Verify that discount is present
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Loyalty Discount"));

		//Delete conditions for discount and verify is it displayed
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED).getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getValue().toString().contains("Loyalty Discount"));
		PremiumAndCoveragesTab.buttonSaveAndExit.click();

		log.info("Loyalty discount is verified successfully");
	}
}