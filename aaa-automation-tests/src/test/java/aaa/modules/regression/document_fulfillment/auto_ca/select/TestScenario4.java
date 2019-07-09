package aaa.modules.regression.document_fulfillment.auto_ca.select;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;

import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

/**
 * Validate if 550029 is generated
 * @author qyu
 *
 */
public class TestScenario4 extends AutoCaSelectBaseTest {
	private String policyNumber;
	private LocalDateTime dd6;
	private BillingAccount billing = new BillingAccount();
	private TestData cash_payment = testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash");
	
	@Parameters({ "state" })
	@StateList(states = States.CA)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		BillingSummaryPage.open();
		dd6 = BillingSummaryPage.getInstallmentDueDate(2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd6));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dd6));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billing.acceptPayment().perform(cash_payment, totalDue);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._550029);
	}
}
