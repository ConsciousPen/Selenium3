package aaa.modules.regression.sales.auto_ss.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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

	private GeneralTab generalTab = new GeneralTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssetList assetListNamedInsuredInfo = generalTab.getCurrentCarrierInfoAssetList();
	private AssetList assetListAAAProductOwned = generalTab.getAAAProductOwnedAssetList();
	private static final String QUOTE_EFFECTIVE_DATE = "01/01/2018";

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4244")
	public void pas4244_MembershipValidationError(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillFromTo(getPolicyTD(), PrefillTab.class, DriverTab.class, false);

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
		// Start of PAS-3795 New Business DE & NJ: Member Validation Failed Message
		generalTab.getAAAProductOwnedAssetList().fill(getAdjustedTestData());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);

		errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage()).verify.present();

		TestData defaultTestData = getPolicyTD();
		TestData ratingDetailReportsTab = defaultTestData.getTestData("RatingDetailReportsTab");

		TestData addMemberSinceDialog = new SimpleDataProvider();
		addMemberSinceDialog.adjust("Member Since","/today-1y");
		addMemberSinceDialog.adjust("OK","click");

		TestData AaaMembershipReportRow = new SimpleDataProvider();
		AaaMembershipReportRow.adjust("Action","Add Member Since");

		List<TestData> AAAMembershipReport = ratingDetailReportsTab.getTestDataList("AAAMembershipReport");
		AAAMembershipReport.add(AaaMembershipReportRow);
		AAAMembershipReport.add(addMemberSinceDialog);

		// add this to defaultTestData

		// End of PAS-3795 New Business DE & NJ: Member Validation Failed Message

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	*Prepare testdata which will trigger error appearence
	*AND the policy effective date is on or after 01/01/2018
	*invalid membership number
	*/
	private TestData getAdjustedTestData() {
		String membershipNumber = "9920702826992041";

		TestData defaultTestData = getPolicyTD();
		TestData policyInformation = defaultTestData.getTestData(generalTab.getMetaKey()).getTestData(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel())
				.adjust(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), QUOTE_EFFECTIVE_DATE);
		TestData aaaProductsOwned = defaultTestData.getTestData(generalTab.getMetaKey()).getTestData(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel())
				.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), "Yes")
				.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), membershipNumber);
		TestData generalTabAdjusted = defaultTestData.getTestData(generalTab.getMetaKey())
				.adjust(AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), aaaProductsOwned)
				.adjust(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), policyInformation);
		//defaultTestData.adjust(generalTab.getMetaKey(), generalTabAdjusted)
		return generalTabAdjusted;
	}
}
