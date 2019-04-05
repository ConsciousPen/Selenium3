package aaa.modules.bct.batch;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import aaa.helpers.jobs.BCTJobs;
import aaa.helpers.jobs.BatchJob;

public enum BackendJobNamesEnum {

	AAA_BATCH_MARKER_JOB(BCTJobs.groupaaaBatchMarkerJob, BatchJob.aaaBatchMarkerJob, "aaaBatchMarkerJob"),
	RENEWAL_CLAIM_ORDER_ASYNC_JOB(BCTJobs.grouprenewalClaimOrderAsyncJob, BatchJob.renewalClaimOrderAsyncJob, "renewalClaimOrderAsyncJob"),
	AAA_MVR_RENEW_BATCH_ORDER_ASYNC_JOB(BCTJobs.groupaaaMvrRenewBatchOrderAsyncJob, BatchJob.aaaMvrRenewBatchOrderAsyncJob, "aaaMvrRenewBatchOrderAsyncJob"),
	AAA_MEMBERSHIP_RENEWAL_BATCH_ORDER_ASYNC_JOB(BCTJobs.groupmembershipRenewalBatchOrderJob, BatchJob.aaaMembershipRenewalBatchOrderAsyncJob, "aaaMembershipRenewalBatchOrderAsyncJob"),
	POLICY_STATUS_UPDATE_JOB(BCTJobs.groupPolicyStatusUpdateJob, BatchJob.policyStatusUpdateJob, "PolicyStatusUpdateJob"),
	AAA_RECURRING_PAYMENTS_ASYNC_PROCESS_JOB(BCTJobs.groupaaaRecurringPaymentsProcessingJob, BatchJob.aaaRecurringPaymentsAsyncProcessJob, "aaaRecurringPaymentsAsyncProcessJob"),
	APPLY_PENDING_TRANSACTIONS_ASYNC_JOB(BCTJobs.groupapplyPendingTransactionsAsyncJob, BatchJob.applyPendingTransactionsAsyncJob, "applyPendingTransactionsAsyncJob"),
	AAA_CANCELLATION_NOTICE_ASYNC_JOB(BCTJobs.groupaaacancellationNoticeGenerationJob, BatchJob.aaaCancellationNoticeAsyncJob, "aaaCancellationNoticeAsyncJob"),
	AAA_CANCELLATION_CONFIRMATION_ASYNC_JOB(BCTJobs.groupaaacancellationConfirmationGenerationJob, BatchJob.aaaCancellationConfirmationAsyncJob, "aaaCancellationConfirmationAsyncJob"),
	AAA_COLLECTION_CANCEL_DEBT_BATCH_ASYNC_JOB(BCTJobs.groupaaaCollectionCancellDebtBatchJob, BatchJob.aaaCollectionCancellDebtBatchAsyncJob, "aaaCollectionCancellDebtBatchAsyncJob"),
	AAA_REFUND_GENERATION_ASYNC_JOB(BCTJobs.grouprefundGenerationJob, BatchJob.aaaRefundGenerationAsyncJob, "aaaRefundGenerationAsyncJob"),
	AAA_OFFCYCLE_BILLING_INVOICE_ASYNC_JOB(BCTJobs.groupoffCycleInvoiceGenerationJob, BatchJob.offCycleBillingInvoiceAsyncJob, "aaaOffCycleBillingInvoiceAsyncJob"),
	AAA_BILLING_INVOICE_ASYNC_TASK_JOB(BCTJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob, BatchJob.aaaBillingInvoiceAsyncTaskJob, "aaaBillingInvoiceAsyncTaskJob"),
	CHANGE_CANCELLATION_PENDING_POLICIES_STATUS_JOB(BCTJobs.groupchangeCancellationPendingPoliciesStatusJob, BatchJob.changeCancellationPendingPoliciesStatusJob, "ChangeCancellationPendingPoliciesStatusJob"),
	POLICY_DO_NOT_RENEW_ASYNC_JOB(BCTJobs.groupPolicyDoNotRenewAsyncJob, BatchJob.policyDoNotRenewAsyncJob, "policyDoNotRenewAsyncJob"),
	AAA_POLICY_AUTOMATED_RENEWAL_ASYNC_TASK_GENERATION_JOB(BCTJobs.groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob, BatchJob.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob, "AAAPolicyAutomatedRenewalAsyncTaskGenerationJob"),
	AAA_RENEWAL_DATA_REFRESH_ASYNC_JOB(BCTJobs.groupaaaRenewalDataRefreshAsyncJob, BatchJob.aaaRenewalDataRefreshAsyncJob, "aaaRenewalDataRefreshAsyncJob"),
	RENEWAL_VALIDATION_ASYNC_TASK_JOB(BCTJobs.grouprenewalValidationAsyncTaskJob, BatchJob.renewalValidationAsyncTaskJob, "renewalValidationAsyncTaskJob"),
	RENEWAL_IMAGE_RATING_ASYNC_TASK_JOB(BCTJobs.grouprenewalImageRatingAsyncTaskJob, BatchJob.renewalImageRatingAsyncTaskJob, "renewalImageRatingAsyncTaskJob"),
	RENEWAL_OFFER_ASYNC_TASK_JOB(BCTJobs.grouprenewalOfferAsyncTaskJob, BatchJob.renewalOfferAsyncTaskJob, "renewalOfferAsyncTaskJob"),
	POLICY_LAPSED_RENEWAL_PROCESS_ASYNC_JOB(BCTJobs.grouplapsedRenewalProcessJob, BatchJob.lapsedRenewalProcessJob, "policyLapsedRenewalProcessAsyncJob"),
	AAA_RENEWAL_REMINDER_GENERATION_ASYNC_JOB(BCTJobs.groupaaaRenewalReminderGenerationAsyncJob, BatchJob.aaaRenewalReminderGenerationAsyncJob, "aaaRenewalReminderGenerationAsyncJob"),
	AAA_RENEWAL_NOTICE_BILL_ASYNC_JOB(BCTJobs.groupaaaRenewalNoticeBillAsyncJob, BatchJob.aaaRenewalNoticeBillAsyncJob, "aaaRenewalNoticeBillAsyncJob"),
	AAA_PRERENEWAL_NOTICE_ASYNC_JOB(BCTJobs.groupaaaPreRenewalNoticeAsyncJob, BatchJob.aaaPreRenewalNoticeAsyncJob, "aaaPreRenewalNoticeAsyncJob"),
	AAA_AUTOMATED_PROCESSING_INITIATION_JOB(BCTJobs.groupaaaAutomatedProcessingInitiationJob, BatchJob.aaaAutomatedProcessingInitiationJob, "AAAAutomatedProcessingInitiationJob"),
	AUTOMATED_PROCESSING_RATING_JOB(BCTJobs.groupautomatedProcessingRatingJob, BatchJob.automatedProcessingRatingJob, "AutomatedProcessingRatingJob"),
	AAA_MORTGAGEE_RENEWAL_REMINDER_AND_EXP_NOTICE_ASYNC_JOB(BCTJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob, BatchJob.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob, "aaaMortgageeRenewalReminderAndExpNoticeAsyncJob"),
	AAA_REFUND_DISBURSEMENT_ASYNC_JOB(BCTJobs.groupaaaRefundDisbursementAsyncJob, BatchJob.aaaRefundDisbursementAsyncJob, "aaaRefundDisbursementAsyncJob"),
	AAA_ESCHEATMENT_PROCESS_ASYNC_JOB(BCTJobs.groupAAAEscheatmentProcessAsyncJob, BatchJob.aaaEscheatmentProcessAsyncJob, "aaaEscheatmentProcessAsyncJob"),
	AAA_GENERATE_ESCHEATMENT_REPORT_JOB(BCTJobs.groupaaaGenerateEscheatmentReportJob, BatchJob.aaaGenerateEscheatmentReportJob, "aaaGenerateEscheatmentReportJob"),
	;

	private Job groupJobName;
	private Job jobName;
	private String backendJobName;

	BackendJobNamesEnum() {
		this.groupJobName = null;
		this.jobName = null;
		this.backendJobName = null;
	}

	BackendJobNamesEnum(Job grouprenewalClaimOrderAsyncJob, Job renewalClaimOrderAsyncJob, String backendJobName) {
		this.groupJobName = grouprenewalClaimOrderAsyncJob;
		this.jobName = renewalClaimOrderAsyncJob;
		this.backendJobName = backendJobName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("groupJobsName", groupJobName.toString())
				.append("jobName", jobName.toString())
				.append("backendJobName", backendJobName)
				.toString();
	}

	public Job getGroupJobsName() {
		return groupJobName;
	}

	public void setGroupJobsName(Job groupJobsName) {
		this.groupJobName = groupJobsName;
	}

	public Job getJobName() {
		return jobName;
	}

	public void setJobName(Job jobName) {
		this.jobName = jobName;
	}

	public String getBackendJobName() {
		return backendJobName;
	}

	public void setBackendJobName(String backendJobName) {
		this.backendJobName = backendJobName;
	}
}



