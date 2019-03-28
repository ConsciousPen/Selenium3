package aaa.modules.regression.document_fulfillment.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
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
	 * @name Test Expiration Notice Generation
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
		//1
		createCustomerIndividual();
		//2
		String policyNumber = createPolicy(policyTd);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		SearchPage.openBilling(policyNumber);
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		renewalDate = installmentDueDates.get(0).plusYears(1);

		//3
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(1)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(1), billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//4
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(1)));

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		minDue = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());

		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(1)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

		//5
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//6
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(2)));

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(2)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

		//7
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDueDates.get(3)));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(3), billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//8
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		//9
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(installmentDueDates.get(3)));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillDueDate(installmentDueDates.get(3)))
				.setSubtypeReason("Manual Payment").setAmount(minDue.negate()).verifyPresent();

		//10
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);

		//11
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		//12
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();

		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AARNXX);

		//13
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(renewalDate));
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billGenDate = getTimePoints().getBillGenerationDate(renewalDate);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(renewalDate, billGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		//14
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(renewalDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.policyLapsedRenewalProcessAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_EXPIRED);

		//15
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(renewalDate));

		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH64XX);
	}
}
