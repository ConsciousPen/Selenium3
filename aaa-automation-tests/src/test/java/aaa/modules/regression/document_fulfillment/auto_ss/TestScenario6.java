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
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.BillingConstants;
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

		TimeSetterUtil.getInstance().nextPhase(installmentDD1.minusDays(20));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Map<String, String> map = new HashMap<String, String>();
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Deposit Payment");
		billing.declinePayment().start(map);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDD1));
		JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1));
		JobUtils.executeJob(BatchJob.aaaCancellationConfirmationAsyncJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1).plusDays(15));
		JobUtils.executeJob(BatchJob.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_6101);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1).plusDays(30));
		JobUtils.executeJob(BatchJob.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_6102);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationDate(installmentDD1).plusDays(45));
		JobUtils.executeJob(BatchJob.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents._55_6103);
	}
}
