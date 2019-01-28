package aaa.modules.regression.sales.auto_ss.functional;

import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.LookupQueries;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestMembershipValidationError extends AutoSSBaseTest {

	private GeneralTab generalTab = new GeneralTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssetList assetListAAAProductOwned = generalTab.getOtherAAAProductOwnedAssetList();

	/**
	*@author Viktor Petrenko
	*
	*PAS-3794 New Business NJ & DE: Non-Member Message
	*PAS-3795 New Business DE & NJ: Member Validation Failed Message
	*@name Test No Prior Insurance Error and Message presence
	*@scenario
	*0. Create customer
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3795,PAS-3794")
	public void pas3795_MembershipValidationError(@Optional("NJ") String state) {
		int result = DBService.get().executeUpdate(String.format(LookupQueries.UPDATE_AAA_MEMBERSHIP_CONFIG_LOOKUP, "true",getState()));
		Assertions.assertThat(result).isGreaterThan(0);
		TestData testDataAdjusted = getAdjustedTestData();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillFromTo(testDataAdjusted, PrefillTab.class, GeneralTab.class, false);

		CustomSoftAssertions.assertSoftly(softly -> {
			// Start of PAS-3794 New Business DE & NJ: Non-Member Message
			generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("No");
			softly.assertThat(assetListAAAProductOwned.getAsset(AutoSSMetaData.GeneralTab.AAAMembership.EXISTING_MEMBERSHIP_NO_NJ_DE_WARNING_BLOCK)).hasValue(ErrorEnum.Errors.ERROR_AAA_SS171018
					.getMessage());

			generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER).setValue("Membership Pending");
			softly.assertThat(assetListAAAProductOwned.getAsset(AutoSSMetaData.GeneralTab.AAAMembership.EXISTING_MEMBERSHIP_NO_NJ_DE_WARNING_BLOCK)).hasValue(ErrorEnum.Errors.ERROR_AAA_SS171018
					.getMessage());
			// End of PAS-3794 New Business DE & NJ: Non-Member Message
			policy.getDefaultView().fillFromTo(getAdjustedTestData(), GeneralTab.class, PremiumAndCoveragesTab.class, true);

			// Start of PAS-3795 New Business DE & NJ: Member Validation Failed Message
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

			String message = Constants.States.NJ.equals(state) ? ErrorEnum.Errors.ERROR_AAA_SS171018_NJ.getMessage() : ErrorEnum.Errors.ERROR_AAA_SS171018_DE.getMessage();
			softly.assertThat(errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, message)).exists();
			errorTab.cancel();
			// End of PAS-3795 New Business DE & NJ: Member Validation Failed Message
			PremiumAndCoveragesTab.buttonSaveAndExit.click();
		});
	}

	@AfterClass(alwaysRun = true)
	public void disableAAAMembershipError() {
		int result = DBService.get().executeUpdate(String.format(LookupQueries.UPDATE_AAA_MEMBERSHIP_CONFIG_LOOKUP, "false",getState()));
		Assertions.assertThat(result).isGreaterThan(0);
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
				.adjust(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));

		TestData testDataAAAProductsOwned = testDataGeneralTab.getTestData(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel())
				.adjust(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "Yes")
				.adjust(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "9920702826992041");

		TestData generalTabAdjusted = testDataGeneralTab
				.adjust(AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), testDataAAAProductsOwned)
				.adjust(AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), testDataPolicyInformation);

		testData.adjust(generalTab.getMetaKey(),generalTabAdjusted).resolveLinks();
		// End of General tab
		return  testData;
		//return VinUploadAutoSSHelper.addMembershipSinceDateToTestData(testData);
	}
}
