package aaa.modules.regression.service.helper;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.toolkit.webdriver.customcontrols.JavaScriptButton;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestMiniServicesNonPremiumBearingAbstract extends PolicyBaseTest {


	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();


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
		NavigationPage.comboBoxListAction.verify.noOption("Endorse");

		//will be used to check PAS-6364 Sleepy hollow: when doing Service Endorsement after regular endorsement, components are loaded in incorrect order
		secondEndorsementIssueCheck();

		//PAS-343 start
		String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", "%%");
		int numberOfDocumentsRecordsInDb = Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get());
		//PAS-343 end

		String emailAddressChanged = "osi.test@email.com";
		String authorizedBy = "John Smith";
		HelperCommon.executeContactInfoRequest(policyNumber, emailAddressChanged, authorizedBy);

		emailUpdateTransactionHistoryCheck(policyNumber);
		emailAddressChangedInEndorsementCheck(emailAddressChanged, authorizedBy);

		//PAS-343 start
		CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get()), numberOfDocumentsRecordsInDb);
		//PAS-343 end

		HelperCommon.executeContactInfoRequest(policyNumber, emailAddressChanged, authorizedBy);

		//Popup to avoid conflicting transactions
		policy.endorse().start();
		CustomAssert
				.assertTrue("Policy version you are working with is marked as NOT current (Probable cause - another user working with the same policy). Please reload policy to continue working with it."
						.equals(Page.dialogConfirmation.labelMessage.getValue()));
		Page.dialogConfirmation.reject();

		SearchPage.openPolicy(policyNumber);
		secondEndorsementIssueCheck();
	}


	private void emailAddressChangedInEndorsementCheck(String emailAddressChanged, String authorizedBy) {
		policy.policyInquiry().start();

		getGeneralTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel()).verify.value(emailAddressChanged);
		NavigationPage.toViewTab(getDocumentsAndBindTab());

		if (getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel()).isPresent()) {
			getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel()).verify.value(emailAddressChanged);
		}
		if (getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY.getLabel()).isPresent()) {
			getDocumentsAndBindTabElement().getInquiryAssetList().getStaticElement(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY.getLabel()).verify.value(authorizedBy);
		}
		Tab.buttonCancel.click();
	}

	private void secondEndorsementIssueCheck() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(getPremiumAndCoverageTab());
		getPremiumAndCoverageTabElement().getAssetList().getAsset(getCalculatePremium()).click();
		getPremiumAndCoverageTabElement().saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	private void emailUpdateTransactionHistoryCheck(String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).verify.present();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").verify.value("Email Updated - Exte...");
		Tab.buttonCancel.click();
	}

	public Dollar getCoverage(int index, String coverageCd, String... replacement) {
		String coverage = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(index, coverageCd);
		for (String replace : replacement) {
			coverage = coverage.replace(replace, "");
		}
		return new Dollar(coverage);
	}

	public String getCoverages(int index, String coverageCd, String... replacement) {
		String coverage = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(index, coverageCd);
		for (String replace : replacement) {
			coverage = coverage.replace(replace, "");

		}
		return coverage;
	}

}