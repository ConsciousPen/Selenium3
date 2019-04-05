package aaa.modules.regression.document_fulfillment.home_ss.ho6;

import java.time.LocalDateTime;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.utils.StateList;

/**
 * Check AH64XX
 * @author qyu
 *
 */
public class TestScenario1 extends HomeSSHO6BaseTest {
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);

		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getCancellationNoticeDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		JobUtils.executeJob(BatchJob.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH64XX);
	}

}
