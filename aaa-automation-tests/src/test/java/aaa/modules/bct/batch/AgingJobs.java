package aaa.modules.bct.batch;

import static aaa.helpers.jobs.BatchJob.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.SoapJobActions;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.bct.BackwardCompatibilityBaseTest;

public class AgingJobs extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(AgingJobs.class);

	@Parameters({"state"})
	@Test()
	public void runAgingJobs(@Optional("") String state) {
		SoapJobActions service = new SoapJobActions();

		for (Job job : getAgingJobsLogicalSequence()) {
			JobUtils.executeJob(job);
		}
	}

	@Parameters({"state"})
	@Test()
	public void checkSchedulerEnabled(@Optional("") String state) {
		adminApp().open();
		GeneralSchedulerPage.open();
		GeneralSchedulerPage.enableScheduler();
		adminApp().close();
	}

	private ArrayList<Job> getAgingJobsLogicalSequence() {
		ArrayList<Job> list = new ArrayList<Job>();
		list.add(aaaBatchMarkerJob);
		list.add(policyStatusUpdateJob);
		list.add(policyAutomatedRenewalAsyncTaskGenerationJob);
		list.add(renewalValidationAsyncTaskJob);
		list.add(renewalImageRatingAsyncTaskJob);
		list.add(aaaRemittanceFeedAsyncBatchReceiveJob);
		list.add(aaaRecurringPaymentsProcessingJob);
		list.add(bofaRecurringPaymentJob);
		list.add(premiumReceivablesOnPolicyEffectiveJob);
		list.add(changeCancellationPendingPoliciesStatusJob);
		list.add(aaaCancellationNoticeAsyncJob);
		list.add(aaaCancellationConfirmationAsyncJob);
		list.add(aaaCollectionCancellDebtBatchAsyncJob);
		list.add(collectionFeedBatchOrderJob);
		list.add(earnedPremiumWriteoffProcessingJob);
		list.add(offCycleBillingInvoiceAsyncJob);
		list.add(aaaBillingInvoiceAsyncTaskJob);
		list.add(aaaRefundGenerationAsyncJob);
		list.add(preRenewalReminderGenerationAsyncJob);
		list.add(aaaRenewalNoticeBillAsyncJob);
		list.add(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
		list.add(renewalOfferAsyncTaskJob);
		list.add(aaaDelayTriggerTOINoticeAsyncJob);
		list.add(lapsedRenewalProcessJob);
		list.add(aaaRenewalReminderGenerationAsyncJob);
		list.add(policyTransactionLedgerJob_NonMonthly);
		list.add(aaaDataUpdateJob);
		list.add(activityTimeoutJob);
		list.add(activityHistoryJob);
		list.add(activitySummarizationJob);
		list.add(aaaAutomatedProcessingInitiationJob);
		list.add(automatedProcessingRunReportsServicesJob);
		list.add(automatedProcessingRatingJob);
		list.add(automatedProcessingIssuingOrProposingJob);
		list.add(automatedProcessingStrategyStatusUpdateJob);
		list.add(automatedProcessingBypassingAndErrorsReportGenerationJob);
		list.add(ledgerStatusUpdateJob);
		return list;
	}
}
