package aaa.modules.regression.document_fulfillment.auto_ca.select;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario2 extends AutoCaSelectBaseTest {
	private IBillingAccount billing = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	private String REFUND_GENERATION_FOLDER_PATH = "/home/mp2/pas/sit/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testRefundCheckDocument(@Optional("CA") String state) {
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
		JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob, true);
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob, true);
		
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Issued").verifyPresent();

		//JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		//DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._55_3500);
		//refund check are now generated throw csv files PASBB-795
		List<String> documentsFilePaths = RemoteHelper.get().waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, "csv", 10, policyNum);
		assertThat(documentsFilePaths.size()).isGreaterThan(0);
	}
}
