package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestNoPriorInsuranceErrorTemplate extends AutoSSBaseTest {
	private GeneralTab generalTab = new GeneralTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssetList namedInsuredInfo = generalTab.getCurrentCarrierInfoAssetList();
	private final String quoteEffectiveDate = "01/01/2018";

	public void verifyNoPriorInsuranceErrorDARTab() {
		TestData testDataGeneralTabCurrentCarrierInformation = getAdjustedTestData().getTestData("GeneralTab").ksam("CurrentCarrierInformation").resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillFromTo(getPolicyTD(), PrefillTab.class, PremiumAndCoveragesTab.class, true);

		CustomAssert.enableSoftMode();
		// Start of PAS-3805 New Business DE & NJ: No Prior Insurance Message
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(quoteEffectiveDate);
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		generalTab.verifyFieldHasValue(namedInsuredInfo, AutoSSMetaData.GeneralTab.CurrentCarrierInformation.CURRENT_CARRIER_INFORMATION_WARNING_MESSAGE.getLabel(), ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage());
		// fill only Current Carrier Information
		generalTab.getCurrentCarrierInfoAssetList().fill(testDataGeneralTabCurrentCarrierInformation);
		generalTab.verifyFieldHasValue(namedInsuredInfo, AutoSSMetaData.GeneralTab.CurrentCarrierInformation.CURRENT_CARRIER_INFORMATION_WARNING_MESSAGE.getLabel(), ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage());
		// End of PAS-3805 New Business DE & NJ: No Prior Insurance Message
		// Start PAS-4244 New Business DE & NJ: No Prior Insurance Error
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage()).verify.present();
		errorTab.cancel();
		// Trigger Purchase Quote Error
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
		//generalTab.getCurrentCarrierInfoAssetList().fill(getPolicyTD().getTestData("GeneralTab").ksam("CurrentCarrierInformation").resolveLinks());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		policy.getDefaultView().fillFromTo(getPolicyTD(), PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);

		// Set trigger data for error
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE).setValue(quoteEffectiveDate);
		generalTab.getCurrentCarrierInfoAssetList().fill(testDataGeneralTabCurrentCarrierInformation);
		// Calculate Premium
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, ErrorEnum.Errors.ERROR_AAA_SS171019.getMessage()).verify.present();
		// End PAS-4244 New Business DE & NJ: No Prior Insurance Error

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * Prepare testdata which will trigger error appearence
	 * AND the policy effective date is on or after 01/01/2018
	 * AND Days Lapsed (from General page, Current Carrier section) is set to  >  0 and < 3
	 */
	private TestData getAdjustedTestData() {
		TestData defaultTestData = getPolicyTD();
		TestData policyInformation = defaultTestData.getTestData("GeneralTab").getTestData("PolicyInformation")
                .adjust("Effective Date", quoteEffectiveDate);
        TestData currentCarrierSectionAdjusted = defaultTestData.getTestData("GeneralTab").getTestData("CurrentCarrierInformation")
                .adjust("Override Prefilled Current Carrier?", "Yes")
				.adjust("Agent Entered Current/Prior Carrier", "index=2")
				.adjust("Agent Entered Inception Date", TimeSetterUtil.getInstance().parse(quoteEffectiveDate, DateTimeUtils.MM_DD_YYYY).minusDays(2).format(DateTimeUtils.MM_DD_YYYY))
				.adjust("Agent Entered Expiration Date", TimeSetterUtil.getInstance().parse(quoteEffectiveDate, DateTimeUtils.MM_DD_YYYY).minusDays(2).format(DateTimeUtils.MM_DD_YYYY))
				.adjust("Agent Entered BI Limits", "index=1");

		TestData generalTabAdjusted = defaultTestData.getTestData("GeneralTab")
				.adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted)
				.adjust("PolicyInformation", policyInformation);

		return defaultTestData.adjust("GeneralTab", generalTabAdjusted);
	}
}
