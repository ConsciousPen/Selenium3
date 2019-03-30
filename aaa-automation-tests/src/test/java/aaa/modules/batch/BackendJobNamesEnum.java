package aaa.modules.batch;

import org.apache.commons.lang3.builder.ToStringBuilder;
import aaa.helpers.jobs.GroupJobs;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.Jobs;

public enum BackendJobNamesEnum {

	AAA_BATCH_MARKER_JOB(GroupJobs.groupaaaBatchMarkerJob,Jobs.aaaBatchMarkerJob, "aaaBatchMarkerJob"),
	RENEWAL_CLAIM_ORDER_ASYNC_JOB(GroupJobs.grouprenewalClaimOrderAsyncJob, Jobs.renewalClaimOrderAsyncJob, "renewalClaimOrderAsyncJob"),
	AAA_MVR_RENEW_BATCH_ORDER_ASYNC_JOB(GroupJobs.groupaaaMvrRenewBatchOrderAsyncJob, Jobs.aaaMvrRenewBatchOrderAsyncJob, "aaaMvrRenewBatchOrderAsyncJob"),
	AAA_MEMBERSHIP_RENEWAL_BATCH_ORDER_ASYNC_JOB(GroupJobs.groupmembershipRenewalBatchOrderJob, Jobs.aaaMembershipRenewalBatchOrderAsyncJob, "aaaMembershipRenewalBatchOrderAsyncJob"),
	POLICY_STATUS_UPDATE_JOB(GroupJobs.groupPolicyStatusUpdateJob, Jobs.policyStatusUpdateJob, "PolicyStatusUpdateJob"),
	AAA_RECURRING_PAYMENTS_ASYNC_PROCESS_JOB(GroupJobs.groupaaaRecurringPaymentsProcessingJob, Jobs.aaaRecurringPaymentsAsyncProcessJob, "aaaRecurringPaymentsAsyncProcessJob"),
	APPLY_PENDING_TRANSACTIONS_ASYNC_JOB(GroupJobs.groupapplyPendingTransactionsAsyncJob, Jobs.applyPendingTransactionsAsyncJob, "applyPendingTransactionsAsyncJob"),
	AAA_CANCELLATION_NOTICE_ASYNC_JOB(GroupJobs.groupaaacancellationNoticeGenerationJob, Jobs.aaaCancellationNoticeAsyncJob, "aaaCancellationNoticeAsyncJob"),
	AAA_CANCELLATION_CONFIRMATION_ASYNC_JOB(GroupJobs.groupaaacancellationConfirmationGenerationJob, Jobs.aaaCancellationConfirmationAsyncJob, "aaaCancellationConfirmationAsyncJob"),
	AAA_COLLECTION_CANCEL_DEBT_BATCH_ASYNC_JOB(GroupJobs.groupaaaCollectionCancellDebtBatchJob, Jobs.aaaCollectionCancelDebtBatchAsyncJob, "aaaCollectionCancellDebtBatchAsyncJob"),
	AAA_REFUND_GENERATION_ASYNC_JOB(GroupJobs.grouprefundGenerationJob, Jobs.aaaRefundGenerationAsyncJob, "aaaRefundGenerationAsyncJob"),
	AAA_OFFCYCLE_BILLING_INVOICE_ASYNC_JOB(GroupJobs.groupoffCycleInvoiceGenerationJob, Jobs.offCycleBillingInvoiceAsyncJob, "aaaOffCycleBillingInvoiceAsyncJob"),
	AAA_BILLING_INVOICE_ASYNC_TASK_JOB(GroupJobs.groupaaaBillingInvoiceGenerationAsyncTaskJob, Jobs.aaaBillingInvoiceAsyncTaskJob, "aaaBillingInvoiceAsyncTaskJob"),
	CHANGE_CANCELLATION_PENDING_POLICIES_STATUS_JOB(GroupJobs.groupchangeCancellationPendingPoliciesStatusJob, Jobs.changeCancellationPendingPoliciesStatus, "ChangeCancellationPendingPoliciesStatusJob"),
	POLICY_DO_NOT_RENEW_ASYNC_JOB(GroupJobs.groupPolicyDoNotRenewAsyncJob, Jobs.policyDoNotRenewAsyncJob, "policyDoNotRenewAsyncJob"),
	AAA_POLICY_AUTOMATED_RENEWAL_ASYNC_TASK_GENERATION_JOB(GroupJobs.groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob, Jobs.aaaPolicyAutomatedRenewalAsyncTaskGenerationJob, "AAAPolicyAutomatedRenewalAsyncTaskGenerationJob"),
	AAA_RENEWAL_DATA_REFRESH_ASYNC_JOB(GroupJobs.groupaaaRenewalDataRefreshAsyncJob, Jobs.aaaRenewalDataRefreshAsyncJob, "aaaRenewalDataRefreshAsyncJob"),
	RENEWAL_VALIDATION_ASYNC_TASK_JOB(GroupJobs.grouprenewalValidationAsyncTaskJob, Jobs.renewalValidationAsyncTaskJob, "renewalValidationAsyncTaskJob"),
	RENEWAL_IMAGE_RATING_ASYNC_TASK_JOB(GroupJobs.grouprenewalImageRatingAsyncTaskJob, Jobs.renewalImageRatingAsyncTaskJob, "renewalImageRatingAsyncTaskJob"),
	RENEWAL_OFFER_ASYNC_TASK_JOB(GroupJobs.grouprenewalOfferAsyncTaskJob, Jobs.renewalOfferAsyncTaskJob, "renewalOfferAsyncTaskJob"),
	POLICY_LAPSED_RENEWAL_PROCESS_ASYNC_JOB(GroupJobs.grouplapsedRenewalProcessJob, Jobs.policyLapsedRenewalProcessAsyncJob, "policyLapsedRenewalProcessAsyncJob"),
	AAA_RENEWAL_REMINDER_GENERATION_ASYNC_JOB(GroupJobs.groupaaaRenewalReminderGenerationAsyncJob, Jobs.aaaRenewalReminderGenerationAsyncJob, "aaaRenewalReminderGenerationAsyncJob"),
	AAA_RENEWAL_NOTICE_BILL_ASYNC_JOB(GroupJobs.groupaaaRenewalNoticeBillAsyncJob, Jobs.aaaRenewalNoticeBillAsyncJob, "aaaRenewalNoticeBillAsyncJob"),
	AAA_PRERENEWAL_NOTICE_ASYNC_JOB(GroupJobs.groupaaaPreRenewalNoticeAsyncJob, Jobs.aaaPreRenewalNoticeAsyncJob, "aaaPreRenewalNoticeAsyncJob"),
	AAA_AUTOMATED_PROCESSING_INITIATION_JOB(GroupJobs.groupaaaAutomatedProcessingInitiationJob, Jobs.aaaAutomatedProcessingInitiationJob, "AAAAutomatedProcessingInitiationJob"),
	AUTOMATED_PROCESSING_RATING_JOB(GroupJobs.groupautomatedProcessingRatingJob, Jobs.automatedProcessingRatingJob, "AutomatedProcessingRatingJob"),
	AAA_MORTGAGEE_RENEWAL_REMINDER_AND_EXP_NOTICE_ASYNC_JOB(GroupJobs.groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob, Jobs.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob, "aaaMortgageeRenewalReminderAndExpNoticeAsyncJob"),
	AAA_REFUND_DISBURSEMENT_ASYNC_JOB(GroupJobs.groupaaaRefundDisbursementAsyncJob, Jobs.aaaRefundDisbursementAsyncJob, "aaaRefundDisbursementAsyncJob"),
	AAA_ESCHEATMENT_PROCESS_ASYNC_JOB(GroupJobs.groupAAAEscheatmentProcessAsyncJob, Jobs.aaaEscheatmentProcessAsyncJob, "aaaEscheatmentProcessAsyncJob"),
	AAA_GENERATE_ESCHEATMENT_REPORT_JOB(GroupJobs.groupaaaGenerateEscheatmentReportJob, Jobs.aaaGenerateEscheatmentReportJob, "aaaGenerateEscheatmentReportJob"),
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
