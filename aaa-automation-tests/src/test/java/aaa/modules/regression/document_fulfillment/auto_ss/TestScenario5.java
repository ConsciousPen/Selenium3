package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * Check AH34XX for DC
 * @author qyu
 *
 */
public class TestScenario5 extends AutoSSBaseTest {
	private String policyNumber;
	private LocalDateTime installmentDD1;

	@Parameters({"state"})
	@StateList(states = States.DC)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void TC01_CreatePolicy(@Optional("") String state) {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusYears(1));
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData")));
		BillingSummaryPage.open();
		installmentDD1 = BillingSummaryPage.getInstallmentDueDate(2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(installmentDD1));
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(installmentDD1));
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, Documents.AH34XX);
	}
}
