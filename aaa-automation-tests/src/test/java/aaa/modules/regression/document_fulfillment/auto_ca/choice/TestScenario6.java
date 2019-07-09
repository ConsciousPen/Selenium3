package aaa.modules.regression.document_fulfillment.auto_ca.choice;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;

public class TestScenario6 extends AutoCaChoiceBaseTest {
	private String policyNumber;
	private LocalDateTime dd1;
	private IBillingAccount billing = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		
		BillingSummaryPage.open();
		dd1 = BillingSummaryPage.getInstallmentDueDate(2);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC02_BillGeneration(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd1));
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC03_DeclineRecurringPayment(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dd1));
		JobUtils.executeJob(BatchJob.aaaRecurringPaymentsProcessingJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_NoFeeNoRestriction"), 1);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC04_Check605004(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._60_5004); // TODO
	}
}
