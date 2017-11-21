package aaa.modules.regression.sales.template.functional;

import org.apache.commons.lang3.RandomStringUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestEndorsementActionRulesAbstract extends PolicyBaseTest {

	protected abstract Tab getEndorsementActionTab();
	protected abstract AssetDescriptor<TextBox> getEndorsementDate();
	protected abstract AssetDescriptor<ComboBox> getEndorsementReason();
	protected abstract AssetDescriptor<TextBox> getEndorsementOtherReason();
	
	protected void pas5860_EndorsementActionTabRulesHelper() {
		int timeShift = 91;

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(timeShift));

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.endorse().start();

		getEndorsementActionTab().getInquiryAssetList().assetFieldUnionCheck(getEndorsementDate().getLabel(), true, true, true);
		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class).verify
				.value(TimeSetterUtil.getInstance().getCurrentTime().minusDays(0).format(DateTimeUtils.MM_DD_YYYY));

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class).setValue("2011/01/01");
		getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).verify.value("Date format should be in MM/dd/yyyy");

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(timeShift).format(DateTimeUtils.MM_DD_YYYY));
		CustomAssert.assertTrue(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).getValue().contains(""));

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(timeShift - 1).format(DateTimeUtils.MM_DD_YYYY));
		CustomAssert.assertTrue(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).getValue()
				.contains("Cannot endorse policy - policy term does not exist for endorsement date"));

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(timeShift).format(DateTimeUtils.MM_DD_YYYY));
		CustomAssert.assertTrue(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).getValue().contains(""));

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(timeShift + 1).format(DateTimeUtils.MM_DD_YYYY));
		CustomAssert.assertTrue(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).getValue()
				.contains("Cannot endorse policy - policy term does not exist for endorsement date"));

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate().getLabel(), TextBox.class)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(0).format(DateTimeUtils.MM_DD_YYYY));

		getEndorsementActionTab().getInquiryAssetList().assetFieldUnionCheck(getEndorsementReason().getLabel(), true, true, true);
		getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason().getLabel(), ComboBox.class).verify.value("");
		getEndorsementActionTab().getInquiryAssetList().assetFieldUnionCheck(getEndorsementOtherReason().getLabel(), false, false, false);

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason().getLabel(), ComboBox.class).setValue("contains=Other");
		getEndorsementActionTab().getInquiryAssetList().assetFieldUnionCheck(getEndorsementOtherReason().getLabel(), true, true, true);
		getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason().getLabel(), TextBox.class).verify.value("");

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason().getLabel(), TextBox.class).setValue("other value");
		getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason().getLabel(), ComboBox.class).setValue("contains=Maintain");
		getEndorsementActionTab().getInquiryAssetList().assetFieldUnionCheck(getEndorsementOtherReason().getLabel(), false, false, false);

		getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason().getLabel(), ComboBox.class).setValue("contains=Other");
		//BUG PAS-6205 'Other' field value is not reset to Blank after Endorsment Reason is set to Other for the second time
		getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason().getLabel(),TextBox.class).verify.value("");

		//BUG PAS-6204 When entering endorsement reason into 'Other' field in Endorsement Action tab longer than 255 characters, error 500 is thrown
		getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason().getLabel(),TextBox.class).setValue(RandomStringUtils.randomAlphanumeric(256));
		CustomAssert.assertEquals(255, getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason().getLabel(),TextBox.class).getValue().length());
		Tab.buttonOk.click();

		//to check that Endorsement is started
		getEndorsementActionTab().saveAndExit();
		PolicySummaryPage.buttonPendedEndorsement.verify.present();
	}

}


