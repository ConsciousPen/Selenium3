package aaa.modules.regression.sales.auto_ss.functional;

import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.LookupQueries;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestNoPriorInsuranceError extends AutoSSBaseTest {

	/**
	*@author Viktor Petrenko
	*<p>
	*PAS-3805 New Business DE + NJ : No Prior Insurance Message
	*PAS-4244 New Business DE + NJ : No Prior Insurance Error
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
	private AssetList namedInsuredInfo = generalTab.getCurrentCarrierInfoAssetList();
	private static final String TRIGGER_OFF_EFFECTIVE_DATE = TimeSetterUtil.getInstance().getCurrentTime().minusYears(1).format(DateTimeUtils.MM_DD_YYYY);
	private static final String TRIGGER_ON_EFFECTIVE_DATE = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4244")
	public void pas4244_ErrorMessagePresence(@Optional("") String state) {
		TestData testDataCurrentCarrierInfo = getAdjustedTestData(getPolicyTD()).
				getTestData(generalTab.getMetaKey()).ksam(generalTab.getCurrentCarrierInfoAssetList().getName()).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillFromTo(getPolicyTD(), PrefillTab.class, PremiumAndCoveragesTab.class, true);

		CustomAssert.enableSoftMode();
		// Start of PAS-3805 New Business DE & NJ: No Prior Insurance Message
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TRIGGER_ON_EFFECTIVE_DATE);
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		generalTab.verifyFieldHasValue(namedInsuredInfo, AutoSSMetaData.GeneralTab.CurrentCarrierInformation.CURRENT_CARRIER_INFORMATION_WARNING_MESSAGE.getLabel(), ErrorEnum.Errors.ERROR_AAA_SS171019
				.getMessage());

		generalTab.getCurrentCarrierInfoAssetList().fill(testDataCurrentCarrierInfo);
		generalTab.verifyFieldHasValue(namedInsuredInfo, AutoSSMetaData.GeneralTab.CurrentCarrierInformation.CURRENT_CARRIER_INFORMATION_WARNING_MESSAGE.getLabel(), ErrorEnum.Errors.ERROR_AAA_SS171019
				.getMessage());
		// End of PAS-3805 New Business DE & NJ: No Prior Insurance Message

		// Start PAS-4244 New Business DE & NJ: No Prior Insurance Error
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage()).verify.present();
		errorTab.cancel();
		// Remove Trigger till Purchase Quote Error // More than 6 months Total Insurance Experience
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusYears(1).format(DateTimeUtils.MM_DD_YYYY));

		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));


		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);

		// Set trigger data for error
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().fill(testDataCurrentCarrierInfo);
		// Calculate Premium
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage()).verify.present();
		// End PAS-4244 New Business DE & NJ: No Prior Insurance Error

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@AfterClass(alwaysRun = true)
	public void disableAAAMembershipError() {
		int result = DBService.get().executeUpdate(String.format(LookupQueries.UPDATE_AAA_MEMBERSHIP_CONFIG_LOOKUP, "false",getState()));
		Assertions.assertThat(result).isGreaterThan(0);
	}

	/**
	* Prepare testdata which will trigger error appearence
	* AND the policy effective date is on or after 01/01/2018
	* AND Days Lapsed (from General page, Current Carrier section) is set to  >  0 and < 3
	*/
	private TestData getAdjustedTestData(TestData testData) {
		TestData defaultTestData = testData;
		// "no prior insurance" logic
		TestData policyInformationSection = defaultTestData.getTestData(generalTab.getMetaKey()).getTestData(generalTab.getPolicyInfoAssetList().getName())
				.adjust(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), TRIGGER_ON_EFFECTIVE_DATE);

		TestData currentCarrierSectionSection = defaultTestData.getTestData(generalTab.getMetaKey()).getTestData(generalTab.getCurrentCarrierInfoAssetList().getName())
				.adjust(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER.getLabel(), "Yes")
				.adjust(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), "index=2")
				.adjust(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), TimeSetterUtil.getInstance().parse(TRIGGER_ON_EFFECTIVE_DATE, DateTimeUtils.MM_DD_YYYY)
						.minusDays(2).format(DateTimeUtils.MM_DD_YYYY))
				.adjust(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel(), TimeSetterUtil.getInstance().parse(TRIGGER_ON_EFFECTIVE_DATE, DateTimeUtils.MM_DD_YYYY)
						.minusDays(2).format(DateTimeUtils.MM_DD_YYYY))
				.adjust(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel(), "index=1");

		TestData generalTabAdjusted = defaultTestData.getTestData(generalTab.getMetaKey())
				.adjust(generalTab.getCurrentCarrierInfoAssetList().getName(), currentCarrierSectionSection)
				.adjust(generalTab.getPolicyInfoAssetList().getName(), policyInformationSection);

		return defaultTestData.adjust(generalTab.getMetaKey(), generalTabAdjusted);
	}
}
