package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * Check 55 6101, 55 6102, 55 6103 for NV
 * @author qyu
 *
 */
public class TestScenario6 extends AutoSSBaseTest {
	private IBillingAccount billing = new BillingAccount();
	private String policyNumber;
	private LocalDateTime installmentDD1;
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusYears(1));
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		BillingSummaryPage.open();
		installmentDD1 = BillingSummaryPage.getInstallmentDueDate(2);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_DeclinePayment(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(installmentDD1.minusDays(20));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Map<String, String> map = new HashMap<String, String>();
		map.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Deposit Payment");
		billing.declinePayment().start(map);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_GenerateCancelNotice(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDD1));
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_GenerateCancellation(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1));
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC05_Check556101(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1).plusDays(15));
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_6101);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC06_Check556102(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1).plusDays(30));
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_6102);
	}
	
	@Parameters({ "state" })
	@StateList(states = States.NV)
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC07_Check556103(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1).plusDays(45));
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_6103);
	}
}
