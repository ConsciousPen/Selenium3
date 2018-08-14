package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario2 extends AutoCaChoiceBaseTest {
	private IBillingAccount billing = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testRefundCheckDocument(String state) {
		Dollar amount = new Dollar(1234);

		mainApp().open();
		String policyNum = getCopiedPolicy();
		BillingSummaryPage.open();
		billing.acceptPayment().perform(check_payment, amount);
		new BillingPaymentsAndTransactionsVerifier().setType("Payment").setSubtypeReason("Manual Payment").setAmount(amount.negate()).setStatus("Issued").verifyPresent();
		billing.refund().perform(tdRefund, amount);
		new BillingPendingTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Pending").verifyPresent();
		billing.approveRefund().perform(amount);
		new BillingPaymentsAndTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Approved").verifyPresent();
		//billing.issueRefund().perform(amount);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob, true);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob, true);
		
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Issued").verifyPresent();

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._55_3500);
	}
}
