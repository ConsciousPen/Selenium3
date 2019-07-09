package aaa.modules.regression.sales.template.functional;

import org.apache.commons.lang3.RandomStringUtils;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
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

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate())).isPresent().isEnabled().isRequired();
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate()))
					.hasValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(0).format(DateTimeUtils.MM_DD_YYYY));

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate()).setValue("2011/01/01");
			softly.assertThat(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate())).hasValue("Date format should be in MM/dd/yyyy");

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate())
					.setValue(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(timeShift).format(DateTimeUtils.MM_DD_YYYY));
			softly.assertThat(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).getValue().contains("")).isTrue();

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate())
					.setValue(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(timeShift - 1).format(DateTimeUtils.MM_DD_YYYY));
			softly.assertThat(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate())).valueContains("Cannot endorse policy - policy term does not exist for endorsement date");

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate())
					.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(timeShift).format(DateTimeUtils.MM_DD_YYYY));
			softly.assertThat(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate().getLabel()).getValue().contains("")).isTrue();

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate())
					.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(timeShift + 1).format(DateTimeUtils.MM_DD_YYYY));
			softly.assertThat(getEndorsementActionTab().getAssetList().getWarning(getEndorsementDate())).valueContains("Cannot endorse policy - policy term does not exist for endorsement date");

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementDate())
					.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(0).format(DateTimeUtils.MM_DD_YYYY));

			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason())).isPresent().isEnabled().isRequired();
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason())).hasValue("");
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason())).isPresent(false);

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason()).setValue("contains=Other");
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason())).isPresent().isEnabled().isRequired();
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason())).hasValue("");

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason()).setValue("other value");
			getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason().getLabel(), ComboBox.class).setValue("contains=Maintain");
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason())).isPresent(false);

			getEndorsementActionTab().getAssetList().getAsset(getEndorsementReason()).setValue("contains=Other");
			//BUG PAS-6205 'Other' field value is not reset to Blank after Endorsment Reason is set to Other for the second time
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason())).hasValue("");

			//BUG PAS-6204 When entering endorsement reason into 'Other' field in Endorsement Action tab longer than 255 characters, error 500 is thrown
			getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason()).setValue(RandomStringUtils.randomAlphanumeric(256));
			softly.assertThat(getEndorsementActionTab().getAssetList().getAsset(getEndorsementOtherReason()).getValue()).hasSize(255);
			Tab.buttonOk.click();

			//to check that Endorsement is started
			getEndorsementActionTab().saveAndExit();
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isPresent();
		});
	}
}


