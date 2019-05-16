package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.xml.model.pasdoc.DataElement;
import aaa.helpers.xml.model.pasdoc.Document;
import aaa.helpers.xml.model.pasdoc.DocumentGenerationRequest;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;

public class PasDoc_OnlineBatch_Cancel extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario26_1(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policy_cancel = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		PasDocImpl.verifyDocumentsGenerated(policy_cancel, AH61XX);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario26_2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policy_cancel_in_3_days = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));
		PasDocImpl.verifyDocumentsGenerated(policy_cancel_in_3_days, AH61XX);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario27(@Optional("") String state) {
		List<LocalDateTime> installmentDueDate;
		Dollar minDue;
		TestData td = getPolicyTD().adjust(TestData.makeKeyPath("PremiumAndCoveragesTab", "Payment Plan"), "Eleven Pay - Standard");
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(td);
		BillingSummaryPage.open();
		installmentDueDate = BillingHelper.getInstallmentDueDates();
		//1a
		new BillingAccount().generateFutureStatement().perform();
		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(1).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
				.isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
				.isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
		//1b DD1+8
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDate.get(1)));
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		/*mainApp().open();
		SearchPage.openPolicy(policyNumber);*/
		searchForPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		//1c cancelDueDate(+8)
		LocalDateTime cancelDueDate = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(cancelDueDate);
		searchForPolicy(policyNumber);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH67XX);
		//
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(3).getCell("Subtype/Reason")
				.getValue()).isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);
		//2a reinstatementDueDate(+10)
		LocalDateTime reinstatementDueDate = DateTimeUtils.getCurrentDateTime().plusDays(10);
		TimeSetterUtil.getInstance().nextPhase(reinstatementDueDate);
		searchForPolicy(policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		BillingSummaryPage.open();
		minDue = new Dollar(BillingSummaryPage.getMinimumDue());
		//2b
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//2c
		new BillingAccount().generateFutureStatement().perform();
		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(4).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
				.isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
				.isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
		//2d DD3+8
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDate.get(3)));
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		searchForPolicy(policyNumber);
		//2e cancelDueDate(+8)
		LocalDateTime cancelDueDate2 = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(cancelDueDate2);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(3).getCell("Subtype/Reason")
				.getValue()).isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH67XX);

		//test scenario 28
		// 1a reinstatementDueDate(+10)
		LocalDateTime reinstatementDueDate2 = DateTimeUtils.getCurrentDateTime().plusDays(10);
		TimeSetterUtil.getInstance().nextPhase(reinstatementDueDate2);
		searchForPolicy(policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		BillingSummaryPage.open();
		minDue = new Dollar(BillingSummaryPage.getMinimumDue());
		//1b
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		//1c
		new BillingAccount().generateFutureStatement().perform();
		assertThat(BillingSummaryPage.tableInstallmentSchedule.getRow(6).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_STATUS).getValue())
				.isEqualTo(BillingConstants.InstallmentScheduleBilledStatus.BILLED);
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE).getValue())
				.isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
		//1d DD5+8
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDueDate.get(5)));
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		searchForPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		//1e cancelDueDate(+8)
		LocalDateTime cancelDueDate3 = getTimePoints().getCancellationDate(DateTimeUtils.getCurrentDateTime());
		TimeSetterUtil.getInstance().nextPhase(cancelDueDate3);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH63XX);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(3).getCell("Subtype/Reason")
				.getValue()).isEqualTo(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM);
		//2
		String policy_for_cancel1 = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation Reason"),
						"Underwriting - Substantial Increase in Hazard"));
		PasDocImpl.verifyDocumentsGenerated(policy_for_cancel1, AH63XX);
		//3
		String policy_for_cancel2 = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation Reason"),
						"Underwriting - Fraudulent Misrepresentation"));
		PasDocImpl.verifyDocumentsGenerated(policy_for_cancel2, AH63XX);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario29(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation Reason"),
						"New Business Rescission - Underwriting Fraudulent Misrepresentation"));
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH61XXA, AH63XX);

		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus10Days")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation Reason"),
						"New Business Rescission - Underwriting Fraudulent Misrepresentation"));
		PasDocImpl.verifyDocumentsGenerated(false, policyNumber, AH61XXA);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario30(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policy1 = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation Reason"),
						"New Business Rescission - NSF on Down Payment"));
		PasDocImpl.verifyDocumentsGenerated(policy1, AH60XXA);

		String policy2 = createPolicy();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus10Days")
				.adjust(TestData.makeKeyPath("CancellationActionTab", "Cancellation Reason"),
						"New Business Rescission - NSF on Down Payment"));

		PasDocImpl.verifyDocumentsGenerated(false, policy2, AH60XXA);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario31(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		TestData td_2financialDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData_2FinancialDrivers").resolveLinks());
		String policy_2financialDrivers = createPolicy(td_2financialDrivers);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		assertThat(countDocuments(policy_2financialDrivers, null, AASR26)).isEqualTo(2);

		TestData td2_2financialDrivers = getPolicyTD().adjust(getTestSpecificTD("TestData2_2FinancialDrivers").resolveLinks());
		String policy2_2financialDrivers = createPolicy(td2_2financialDrivers);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus10Days"));

		assertThat(countDocuments(policy2_2financialDrivers, null, AASR26)).isEqualTo(1);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario32(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData").adjust(TestData
						.makeKeyPath("CancelNoticeActionTab", "Cancellation Reason"),
				"Insured Non-Payment Of Premium"));
		CustomSoftAssertions.assertSoftly(softly -> {
			PasDocImpl.verifyDocumentsGenerated(softly, true, policyNumber, AH34XX);
			PasDocImpl.verifyDocumentsGenerated(softly, false, policyNumber, AH61XX);
		});
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario33(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policy1 = createPolicy();
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData").adjust(TestData
						.makeKeyPath("CancelNoticeActionTab", "Cancellation Reason"),
				"Underwriting - Fraudulent Misrepresentation"));
		PasDocImpl.verifyDocumentsGenerated(policy1, AH61XX);

		String policy2 = createPolicy();
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData").adjust(TestData
						.makeKeyPath("CancelNoticeActionTab", "Cancellation Reason"),
				"Underwriting - Substantial Increase in Hazard"));
		PasDocImpl.verifyDocumentsGenerated(policy2, AH61XX);
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
	public void testScenario34(@Optional("") String state) {
		LocalDateTime renewalDueDate;

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		renewalDueDate = PolicySummaryPage.getExpirationDate();
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDueDate));
		JobUtils.executeJob(Jobs.policyDoNotRenewAsyncJob);
		PasDocImpl.verifyDocumentsGenerated(policyNumber, AH65XX);
	}

	private int countDocuments(String policyNumber, DocGenEnum.EventName eventName, DocGenEnum.Documents document) {
		DocumentGenerationRequest docGenReq = PasDocImpl.getDocumentRequest(policyNumber, eventName, document);
		Document doc = docGenReq.getDocuments().stream().filter(c -> document.getIdInXml().equals(c.getTemplateId())).findFirst().get();
		//doc.getAdditionalData().getDataElement().stream().filter(dataElement -> dataElement.getName().equals("")).collect(Collectors.toList());
		List<String> dataElementList = new ArrayList<>();
		for (DataElement dataElement : doc.getAdditionalData().getDataElement()) {
			dataElementList.add(dataElement.getName());
		}
		log.info("Count of documents: " + dataElementList.size());
		return dataElementList.size();
	}
}