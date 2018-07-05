package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.db.DBService;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {


	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract String getVehicleTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindTabElement();

	protected abstract Tab getVehicleTabElement();

	protected abstract AssetDescriptor<JavaScriptButton> getCalculatePremium();

	protected void pas1441_emailChangeOutOfPasTestBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//BUG PAS-5815 There is an extra Endorse action available for product
		assertThat(NavigationPage.comboBoxListAction).doesNotContainOption("Endorse");

		//will be used to check PAS-6364 Sleepy hollow: when doing Service Endorsement after regular endorsement, components are loaded in incorrect order
		testEValueDiscount.secondEndorsementIssueCheck();

		//PAS-343 start
		String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "%%");
		java.util.Optional<String> numberOfDocumentsRecordsInDb = DBService.get().getValue(numberOfDocumentsRecordsInDbQuery);
		//PAS-343 end

		String emailAddressChanged = "osi.test@email.com";
		String authorizedBy = "John Smith";
		HelperCommon.executeContactInfoRequest(policyNumber, emailAddressChanged, authorizedBy);

		emailUpdateTransactionHistoryCheck(policyNumber);
		emailAddressChangedInEndorsementCheck(emailAddressChanged, authorizedBy);

		//PAS-343 start
		assertThat(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery)).isEqualTo(numberOfDocumentsRecordsInDb);
		//PAS-343 end

		HelperCommon.executeContactInfoRequest(policyNumber, emailAddressChanged, authorizedBy);

		//Popup to avoid conflicting transactions
		policy.endorse().start();
		assertThat(Page.dialogConfirmation.labelMessage).hasValue("Policy version you are working with is marked as NOT current (Probable cause - another user working with the same policy). Please reload policy to continue working with it.");
		Page.dialogConfirmation.reject();

		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.secondEndorsementIssueCheck();
	}


	private void emailAddressChangedInEndorsementCheck(String emailAddressChanged, String authorizedBy) {
		policy.policyInquiry().start();

		assertThat(getGeneralTabElement().getInquiryAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL)).hasValue(emailAddressChanged);
		NavigationPage.toViewTab(getDocumentsAndBindTab());

		if (getDocumentsAndBindTabElement().getInquiryAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).isPresent()) {
			assertThat(getDocumentsAndBindTabElement().getInquiryAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL)).hasValue(emailAddressChanged);
		}
		if (getDocumentsAndBindTabElement().getInquiryAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY).isPresent()) {
			assertThat(getDocumentsAndBindTabElement().getInquiryAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY)).hasValue(authorizedBy);
		}
		Tab.buttonCancel.click();
	}

	private void emailUpdateTransactionHistoryCheck(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled(false);
		PolicySummaryPage.buttonTransactionHistory.click();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1)).exists();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason")).hasValue("Email Updated - Exte...");
		Tab.buttonCancel.click();
	}



}