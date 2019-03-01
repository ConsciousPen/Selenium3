package aaa.helpers.http;

import java.util.HashMap;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.helpers.jobs.GroupJobs;
import aaa.helpers.jobs.Jobs;

public class BackendJobNames {
	private static Logger log = LoggerFactory.getLogger(BackendJobNames.class);

	/**
	 * This names are used in logs at the Job's logs page.\
	 * @param  -> jobName
	 * @return
	 */
	public static String getBackEndJobNames(String jobName) {
		HashMap<String, String> storage = new HashMap<>();
		storage.put(GroupJobs.groupaaaBatchMarkerJob.getJobName(), "aaaBatchMarkerJob");
		storage.put(Jobs.aaaBatchMarkerJob.getJobName(), "aaaBatchMarkerJob");
		storage.put(GroupJobs.grouprenewalClaimOrderAsyncJob.getJobName(), "renewalClaimOrderAsyncJob");
		storage.put(GroupJobs.groupaaaMvrRenewBatchOrderAsyncJob.getJobName(), "aaaMvrRenewBatchOrderAsyncJob");
		storage.put(GroupJobs.groupmembershipRenewalBatchOrderJob.getJobName(), "aaaMembershipRenewalBatchOrderAsyncJob");
		storage.put(GroupJobs.groupPolicyStatusUpdateJob.getJobName(), "PolicyStatusUpdateJob");
		storage.put(GroupJobs.groupaaaRecurringPaymentsProcessingJob.getJobName(), "aaaRecurringPaymentsAsyncProcessJob");
		storage.put(GroupJobs.groupapplyPendingTransactionsAsyncJob.getJobName(), "applyPendingTransactionsAsyncJob");
		storage.put(GroupJobs.groupaaacancellationNoticeGenerationJob.getJobName(), "aaaCancellationNoticeAsyncJob");
		storage.put(GroupJobs.groupaaacancellationConfirmationGenerationJob.getJobName(), "aaaCancellationConfirmationAsyncJob");
		storage.put(GroupJobs.groupaaaCollectionCancellDebtBatchJob.getJobName(), "aaaCollectionCancellDebtBatchAsyncJob");
		storage.put(GroupJobs.grouprefundGenerationJob.getJobName(), "aaaRefundGenerationAsyncJob");
		storage.put(GroupJobs.groupoffCycleInvoiceGenerationJob.getJobName(), "aaaOffCycleBillingInvoiceAsyncJob");
		storage.put(GroupJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob.getJobName(), "aaaBillingInvoiceAsyncTaskJob");
		storage.put(GroupJobs.groupchangeCancellationPendingPoliciesStatusJob.getJobName(), "ChangeCancellationPendingPoliciesStatusJob");
		storage.put(GroupJobs.groupPolicyDoNotRenewAsyncJob.getJobName(), "policyDoNotRenewAsyncJob");
		storage.put(GroupJobs.groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob.getJobName(), "AAAPolicyAutomatedRenewalAsyncTaskGenerationJob");
		storage.put(GroupJobs.groupaaaRenewalDataRefreshAsyncJob.getJobName(), "aaaRenewalDataRefreshAsyncJob");
		storage.put(GroupJobs.grouprenewalValidationAsyncTaskJob.getJobName(), "renewalValidationAsyncTaskJob");
		storage.put(GroupJobs.grouprenewalImageRatingAsyncTaskJob.getJobName(), "renewalImageRatingAsyncTaskJob");
		storage.put(GroupJobs.grouprenewalOfferAsyncTaskJob.getJobName(), "policyRenewalOfferJob");
		storage.put(GroupJobs.grouplapsedRenewalProcessJob.getJobName(), "policyLapsedRenewalProcessAsyncJob");
		storage.put(GroupJobs.groupaaaRenewalReminderGenerationAsyncJob.getJobName(), "aaaRenewalReminderGenerationAsyncJob");
		storage.put(GroupJobs.groupaaaRenewalNoticeBillAsyncJob.getJobName(), "aaaRenewalNoticeBillAsyncJob");
		storage.put(GroupJobs.groupaaaPreRenewalNoticeAsyncJob.getJobName(), "aaaPreRenewalNoticeAsyncJob");
		storage.put(GroupJobs.groupaaaAutomatedProcessingInitiationJob.getJobName(), "AAAAutomatedProcessingInitiationJob");
		storage.put(GroupJobs.groupautomatedProcessingRatingJob.getJobName(), "AutomatedProcessingRatingJob");
		storage.put(GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob.getJobName(), "aaaMortgageeRenewalReminderAndExpNoticeAsyncJob");
		storage.put(GroupJobs.groupaaaRefundDisbursementAsyncJob.getJobName(), "aaaRefundDisbursementAsyncJob");
		storage.put(GroupJobs.groupAAAEscheatmentProcessAsyncJob.getJobName(), "aaaEscheatmentProcessAsyncJob");


		if(storage.containsKey(jobName)){
			log.info("HTTP: Backend job name was used : {}", storage.get(jobName));
			return storage.get(jobName);
		}
		else{
			throw new NoSuchElementException(jobName + " doesn't exist in the list or wrong jobName");
		}
	}
}