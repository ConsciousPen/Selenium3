package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestMembershipValidationError extends AutoSSBaseTest {

	private GeneralTab generalTab = new GeneralTab();
	private ErrorTab errorTab = new ErrorTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private AssetList assetListNamedInsuredInfo = generalTab.getCurrentCarrierInfoAssetList();
	private AssetList assetListAAAProductOwned = generalTab.getAAAProductOwnedAssetList();
	private static final String QUOTE_EFFECTIVE_DATE = "01/01/2018";

	/**
	*@author Viktor Petrenko
	*
	*PAS-3794 New Business NJ & DE: Non-Member Message
	*PAS-3795 New Business DE & NJ: Member Validation Failed Message
	*@name Test No Prior Insurance Error and Message presence
	*@scenario 0. Create customer
	*1. Initiate Auto SS quote creation and make it ready for purchase
	*2. Go to the GeneralTab and Change current carrier section to trigger error
	*3. Verify warning message presence
	*4. Navigate to Driver Activity Reports tab
	*5. Verify Error message presence
	*6. Return current carrier section to the default state
	*7. Purchase Reports at the DAR tab
	*8. Go to the GeneralTab and Change current carrier section to trigger error
	*9. Calculate Premium and purchase quote
	*10. Verify Error message presence
	*@details
	*/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3795")
	public void pas3795_MembershipValidationError(@Optional("") String state) {
		TestData testDataAdjusted = getAdjustedTestData();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillFromTo(testDataAdjusted, PrefillTab.class, DriverTab.class, false);

		CustomAssert.enableSoftMode();

		// Start of PAS-3794 New Business DE & NJ: Non-Member Message
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(QUOTE_EFFECTIVE_DATE);

		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		generalTab.verifyFieldHasValue(assetListAAAProductOwned, AutoSSMetaData.GeneralTab.AAAProductOwned.EXISTING_MEMBERSHIP_NO_NJ_DE_WARNING_BLOCK.getLabel(), ErrorEnum.Errors.ERROR_AAA_SS171018
				.getMessage());

		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		generalTab.verifyFieldHasValue(assetListAAAProductOwned, AutoSSMetaData.GeneralTab.AAAProductOwned.EXISTING_MEMBERSHIP_NO_NJ_DE_WARNING_BLOCK.getLabel(), ErrorEnum.Errors.ERROR_AAA_SS171018
				.getMessage());
		// End of PAS-3794 New Business DE & NJ: Non-Member Message

		policy.getDefaultView().fillFromTo(testDataAdjusted, GeneralTab.class, PremiumAndCoveragesTab.class, true);

		// Start of PAS-3795 New Business DE & NJ: Member Validation Failed Message
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		String message = Constants.States.NJ.equals(state) ? ErrorEnum.Errors.ERROR_AAA_SS171018_NJ.getMessage() : ErrorEnum.Errors.ERROR_AAA_SS171018_DE.getMessage();
		errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, message).verify.present();
		errorTab.cancel();

		// End of PAS-3795 New Business DE & NJ: Member Validation Failed Message

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	*Prepare test data which will trigger error appearance
	*AND the policy effective date is on or after 01/01/2018
	*invalid membership number
	*/
	private TestData getAdjustedTestData() {
		TestData testData = getPolicyTD();
		// Start General Tab
		TestData testDataGeneralTab = testData.getTestData(generalTab.getMetaKey());

		TestData testDataPolicyInformation = testDataGeneralTab.getTestData(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel())
				.adjust(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), QUOTE_EFFECTIVE_DATE);
		TestData testDataAAAProductsOwned = testDataGeneralTab.getTestData(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel())
				.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Yes")
				.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), "9920702826992041");
		TestData generalTabAdjusted = testDataGeneralTab
				.adjust(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), testDataAAAProductsOwned)
				.adjust(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), testDataPolicyInformation);

		testData.adjust(generalTab.getMetaKey(),generalTabAdjusted).resolveLinks();
		// End of General tab

		// Start of  Rating DetailReports Tab
		TestData addMemberSinceDialog = new SimpleDataProvider().adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), "/today-1y:MM/dd/yyyy").adjust(AutoSSMetaData.RatingDetailReportsTab.AddMemberSinceDialog.BTN_OK.getLabel(), "click");
		TestData aaaMembershipReportRow = new SimpleDataProvider().adjust("Action", "Add Member Since").adjust(AutoSSMetaData.RatingDetailReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG.getLabel(), addMemberSinceDialog);

		// Rating DetailReports Tab
		TestData testDataRatingDetailReportsTab = testData.getTestData(ratingDetailReportsTab.getMetaKey())
				.adjust(AutoSSMetaData.RatingDetailReportsTab.AAA_MEMBERSHIP_REPORT.getLabel(), aaaMembershipReportRow);
		testData.adjust(ratingDetailReportsTab.getMetaKey(), testDataRatingDetailReportsTab).resolveLinks();
		// End of  Rating DetailReports Tab

		return testData;
	}
}
