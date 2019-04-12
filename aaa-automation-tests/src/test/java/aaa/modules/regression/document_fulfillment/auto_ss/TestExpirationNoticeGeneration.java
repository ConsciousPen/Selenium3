package aaa.modules.regression.document_fulfillment.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestExpirationNoticeGeneration extends AutoSSBaseTest {

	/**
	 * @author Denis Semenov
	 * @name Test Expiration Notice Generation (C-AU-SS-AZ-435-OH)
	 * @scenario
	 * 1. Create customer
	 * 2. Create police
	 * 3. (DD3-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill
	 * 4. (DD3) Make Payment for the generated bill
	 * 5. (DD6-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill
	 * 6. (DD6) Make Payment for the generated bill
	 * 7. (DD9-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill
	 * 8. (R-96) Run the Renewal_Offer_Generation_Part2
	 * 9. (DD9) Make Payment for the generated bill
	 * 10. (R-63) Run the Renewal_Offer_Generation_Part1
	 * 11. (R-45) Renewal_Offer_Generation_Part2
	 * 12. (R-35)  Run the Renewal_Offer_Generation_Part2 and Search for AARNXX form in DB (RENEWAL_OFFER event)
	 * 13. (R-20) Run the aaaRenewalNoticeBillAsyncJob and generate the bill
	 * 14. (R+1) Run the policyStatusUpdateJob and policyLapsedRenewalProcessAsyncJob
	 * 15. (R+5) Run aaaRenewalReminderGenerationAsyncJob and aaaDocGenBatchJob and Search for AH64XX form in DB (EXPIRATION_NOTICE event)
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.OH})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)

	public void testExpirationNoticeGeneration(@Optional("OH") String state) {
		List<LocalDateTime> installmentDueDates;
		LocalDateTime billGenDate;
		LocalDateTime renewalDate;
		Dollar minDue;

		TestData policyTd = getPolicyDefaultTD().adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), "Payment Plan"), "Quarterly");
		TestData loginTD = getLoginTD().adjust("Groups", "A30");
		loginTD.adjust("User", "qa_roles").adjust("Password", "qa_roles");

		mainApp().open(loginTD);
		//Create customer
		createCustomerIndividual();
		//Create police
		String policyNumber = createPolicy(policyTd);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		SearchPage.openBilling(policyNumber);
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		renewalDate = installmentDueDates.get(0).plusYears(1);

		//(DD3-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(1), billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//(DD3) Make Payment for the generated bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());

		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(1)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

		//(DD6-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//(DD6) Make Payment for the generated bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(2)));

		mainApp().open();
		SearchPage.openBilling(policyNumber);


        minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(2)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

		//(DD9-20) Run the aaaBillingInvoiceAsyncTaskJob and generate the bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(3)));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(3), billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//(R-96) Run the Renewal_Offer_Generation_Part2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		//(DD9) Make Payment for the generated bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(3)));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
        minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(3)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

		//(R-63) Run the Renewal_Offer_Generation_Part1
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);

		//(R-45) Renewal_Offer_Generation_Part2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		//(R-35)  Run the Renewal_Offer_Generation_Part2 and Search for AARNXX form in DB (RENEWAL_OFFER event)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AARNXX);

		//(R-20) Run the aaaRenewalNoticeBillAsyncJob and generate the bill
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(renewalDate);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(renewalDate, billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//(R+1) Run the policyStatusUpdateJob and policyLapsedRenewalProcessAsyncJob
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_EXPIRED);

		//(R+5) Run aaaRenewalReminderGenerationAsyncJob and aaaDocGenBatchJob and Search for AH64XX form in DB (EXPIRATION_NOTICE event)
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(renewalDate));

		JobUtils.executeJob(BatchJob.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);

		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH64XX);
	}
}
