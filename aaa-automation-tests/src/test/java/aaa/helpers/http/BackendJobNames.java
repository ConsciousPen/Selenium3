package aaa.helpers.http;

import java.util.HashMap;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.jobs.BCTJobs;
import aaa.helpers.jobs.BatchJob;

public class BackendJobNames {
	private static Logger log = LoggerFactory.getLogger(BackendJobNames.class);

	/**
	 * This names are used in logs at the Job's logs page.\
	 * @param  -> jobName
	 * @return
	 */
	public static String getBackEndJobNames(String jobName) {
		HashMap<String, String> storage = new HashMap<>();
		storage.put(BCTJobs.groupaaaBatchMarkerJob.getJobName(), "aaaBatchMarkerJob");
		storage.put(BatchJob.aaaBatchMarkerJob.getJobName(), "aaaBatchMarkerJob");
		storage.put(BCTJobs.grouprenewalClaimOrderAsyncJob.getJobName(), "renewalClaimOrderAsyncJob");
		storage.put(BCTJobs.groupaaaMvrRenewBatchOrderAsyncJob.getJobName(), "aaaMvrRenewBatchOrderAsyncJob");
		storage.put(BCTJobs.groupmembershipRenewalBatchOrderJob.getJobName(), "aaaMembershipRenewalBatchOrderAsyncJob");
		storage.put(BCTJobs.groupPolicyStatusUpdateJob.getJobName(), "PolicyStatusUpdateJob");
		storage.put(BCTJobs.groupaaaRecurringPaymentsProcessingJob.getJobName(), "aaaRecurringPaymentsAsyncProcessJob");
		storage.put(BCTJobs.groupapplyPendingTransactionsAsyncJob.getJobName(), "applyPendingTransactionsAsyncJob");
		storage.put(BCTJobs.groupaaacancellationNoticeGenerationJob.getJobName(), "aaaCancellationNoticeAsyncJob");
		storage.put(BCTJobs.groupaaacancellationConfirmationGenerationJob.getJobName(), "aaaCancellationConfirmationAsyncJob");
		storage.put(BCTJobs.groupaaaCollectionCancellDebtBatchJob.getJobName(), "aaaCollectionCancellDebtBatchAsyncJob");
		storage.put(BCTJobs.grouprefundGenerationJob.getJobName(), "grouprefundGenerationJob");
		storage.put(BCTJobs.groupoffCycleInvoiceGenerationJob.getJobName(), "aaaOffCycleBillingInvoiceAsyncJob");
		storage.put(BCTJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob.getJobName(), "aaaBillingInvoiceAsyncTaskJob");
		storage.put(BCTJobs.groupchangeCancellationPendingPoliciesStatusJob.getJobName(), "ChangeCancellationPendingPoliciesStatusJob");
		storage.put(BCTJobs.groupPolicyDoNotRenewAsyncJob.getJobName(), "policyDoNotRenewAsyncJob");
		storage.put(BCTJobs.groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob.getJobName(), "AAAPolicyAutomatedRenewalAsyncTaskGenerationJob");
		storage.put(BCTJobs.groupaaaRenewalDataRefreshAsyncJob.getJobName(), "aaaRenewalDataRefreshAsyncJob");
		storage.put(BCTJobs.grouprenewalValidationAsyncTaskJob.getJobName(), "renewalValidationAsyncTaskJob");
		storage.put(BCTJobs.grouprenewalImageRatingAsyncTaskJob.getJobName(), "renewalImageRatingAsyncTaskJob");
		storage.put(BCTJobs.grouprenewalOfferAsyncTaskJob.getJobName(), "policyRenewalOfferJob");
		storage.put(BCTJobs.grouplapsedRenewalProcessJob.getJobName(), "policyLapsedRenewalProcessAsyncJob");
		storage.put(BCTJobs.groupaaaRenewalReminderGenerationAsyncJob.getJobName(), "aaaRenewalReminderGenerationAsyncJob");
		storage.put(BCTJobs.groupaaaRenewalNoticeBillAsyncJob.getJobName(), "aaaRenewalNoticeBillAsyncJob");
		storage.put(BCTJobs.groupaaaPreRenewalNoticeAsyncJob.getJobName(), "aaaPreRenewalNoticeAsyncJob");
		storage.put(BCTJobs.groupaaaAutomatedProcessingInitiationJob.getJobName(), "AAAAutomatedProcessingInitiationJob");
		storage.put(BCTJobs.groupautomatedProcessingRatingJob.getJobName(), "AutomatedProcessingRatingJob");
		storage.put(BCTJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob.getJobName(), "aaaMortgageeRenewalReminderAndExpNoticeAsyncJob");
		storage.put(BCTJobs.groupaaaRefundDisbursementAsyncJob.getJobName(), "aaaRefundDisbursementAsyncJob");


		if(storage.containsKey(jobName)){
			log.info("HTTP: Backend job name was used : {}", storage.get(jobName));
			return storage.get(jobName);
		}
		else{
			throw new NoSuchElementException(jobName + " doesn't exist in the list or wrong jobName");
		}
	}
}