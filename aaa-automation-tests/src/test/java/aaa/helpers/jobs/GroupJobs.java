package aaa.helpers.jobs;

import java.util.concurrent.ConcurrentHashMap;
import com.exigen.ipb.etcsa.utils.batchjob.Job;

public class GroupJobs {

	private static ConcurrentHashMap<String, JobState> jobsState = new ConcurrentHashMap<>();

	public static Job groupaaaRefundDisbursementAsyncJob = new Job("groupaaaRefundDisbursementAsyncJob");
	public static Job grouppolicyTransactionLedgerJob = new Job("grouppolicyTransactionLedgerJob");
	public static Job groupautomatedProcessingStrategyStatusUpdateJob = new Job("groupautomatedProcessingStrategyStatusUpdateJob");
	public static Job groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob = new Job("groupaaapolicyAutomatedRenewalAsyncTaskGenerationJob");
	public static Job groupactivityHistoryJob = new Job("groupactivityHistoryJob");
	public static Job groupaaaDocGenBatchJob = new Job("groupaaaDocGenBatchJob");
	public static Job groupaaaBalanceTransferConversionAsyncJob = new Job("groupaaaBalanceTransferConversionAsyncJob");
	public static Job groupaaaRecurringPaymentsProcessingJob = new Job("groupaaaRecurringPaymentsProcessingJob");
	public static Job groupaaaEARSReceiveJob = new Job("groupaaaEARSReceiveJob");
	public static Job groupaaaExpiraionNoticeJob = new Job("groupaaaExpiraionNoticeJob");
	public static Job grouprenewalValidationAsyncTaskJob = new Job("grouprenewalValidationAsyncTaskJob");
	public static Job groupaaaMvrRenewBatchOrderAsyncJob = new Job("groupaaaMvrRenewBatchOrderAsyncJob");
	public static Job groupPolicyStatusUpdateJob = new Job("groupPolicyStatusUpdateJob");
	public static Job groupaaaDirectRenewInterruptedPoliciesJob = new Job("groupaaaDirectRenewInterruptedPoliciesJob");
	public static Job groupaaaRenewalReminderGenerationAsyncJob = new Job("groupaaaRenewalReminderGenerationAsyncJob");
	public static Job groupaaaRecurringPaymentsResponseProcessAsyncJob = new Job("groupaaaRecurringPaymentsResponseProcessAsyncJob");
	public static Job groupEarnedPremiumPostingAsyncTaskGenerationJob = new Job("groupEarnedPremiumPostingAsyncTaskGenerationJob");
	public static Job groupaaaRenewalNoticeBillAsyncJob = new Job("groupaaaRenewalNoticeBillAsyncJob");
	public static Job groupaaaPreRenewalNoticeAsyncJob = new Job("groupaaaPreRenewalNoticeAsyncJob");
	public static Job groupcumulativeFileReadJob = new Job("groupcumulativeFileReadJob");
	public static Job groupisoRenewalBatchOrderJobBatch = new Job("groupisoRenewalBatchOrderJobBatch");
	public static Job groupaaaBatchProcessingDate = new Job("groupaaaBatchProcessingDate");
	public static Job groupboaCheckReconciliationBatchRecieveJob = new Job("groupboaCheckReconciliationBatchRecieveJob");
	public static Job groupaaaBCTCEAPlcyDtlsAsyncJob = new Job("groupaaaBCTCEAPlcyDtlsAsyncJob");
	public static Job grouprenewalOfferAsyncTaskJob = new Job("grouprenewalOfferAsyncTaskJob");
	public static Job groupaaaImportPolicyHomeCaHdesAsyncTaskJob = new Job("groupaaaImportPolicyHomeCaHdesAsyncTaskJob");
	public static Job groupchangeCancellationPendingPoliciesStatusJob = new Job("groupchangeCancellationPendingPoliciesStatusJob");
	public static Job grouppolicyCovnersionImportAsyncTaskJob = new Job("grouppolicyCovnersionImportAsyncTaskJob");
	public static Job groupaaaImportPolicyHomeCaSisAsyncTaskJob = new Job("groupaaaImportPolicyHomeCaSisAsyncTaskJob");
	public static Job grouprenewalClaimOrderAsyncJob = new Job("grouprenewalClaimOrderAsyncJob");
	public static Job groupaaaCarfaxReportImportAsyncJob = new Job("groupaaaCarfaxReportImportAsyncJob");
	public static Job groupaaaRenewalRedZoneImmediateBillAsyncJob = new Job("groupaaaRenewalRedZoneImmediateBillAsyncJob");
	public static Job groupaaacancellationNoticeGenerationJob = new Job("groupaaacancellationNoticeGenerationJob");
	public static Job groupaaaRenewalDataRefreshAsyncJob = new Job("groupaaaRenewalDataRefreshAsyncJob");
	public static Job grouprenewalClaimReceiveAsyncJob = new Job("grouprenewalClaimReceiveAsyncJob");
	public static Job groupPolicyStatusUpdateJob_new = new Job("groupPolicyStatusUpdateJob_new");
	public static Job groupaaaCCardExpiryNoticeJob = new Job("groupaaaCCardExpiryNoticeJob");
	public static Job groupavsReceiveJob = new Job("groupavsReceiveJob");
	public static Job groupaaaEscheatmentVoidTransferProcessAsyncJob = new Job("groupaaaEscheatmentVoidTransferProcessAsyncJob");
	public static Job groupaaaConvRenDetailBatchJob = new Job("groupaaaConvRenDetailBatchJob");
	public static Job groupaaaCreditDisclosureNoticeJob = new Job("groupaaaCreditDisclosureNoticeJob");
	public static Job groupAAATelematicSafetyScoreOrderAsyncJob = new Job("groupAAATelematicSafetyScoreOrderAsyncJob");
	public static Job groupmembershipRenewalBatchOrderJob = new Job("groupmembershipRenewalBatchOrderJob");
	public static Job groupautomatedProcessingBypassingAndErrorsReportGenerationJob = new Job("groupautomatedProcessingBypassingAndErrorsReportGenerationJob");
	public static Job groupimportMiniPolicyAsyncTaskJob = new Job("groupimportMiniPolicyAsyncTaskJob");
	public static Job groupaaacancellationConfirmationGenerationJob = new Job("groupaaacancellationConfirmationGenerationJob");
	public static Job groupaaaPligaFeeAsyncJob = new Job("groupaaaPligaFeeAsyncJob");
	public static Job groupdmvBatchJob = new Job("groupdmvBatchJob");
	public static Job groupaaaBillingInvoiceGenerationAsyncTaskJob = new Job("groupaaaBillingInvoiceGenerationAsyncTaskJob");
	public static Job groupaaaRecordCustomerDetailsJob = new Job("groupaaaRecordCustomerDetailsJob");
	public static Job grouprenewalClaimPropertyReceiveAsyncJob = new Job("grouprenewalClaimPropertyReceiveAsyncJob");
	public static Job grouplegacyPolicyStatusImportJob = new Job("grouplegacyPolicyStatusImportJob");
	public static Job groupadesPolicyConversionImportJob = new Job("groupadesPolicyConversionImportJob");
	public static Job groupaaaPaymentCentralRejectFeedAsyncJob = new Job("groupaaaPaymentCentralRejectFeedAsyncJob");
	public static Job groupaaaDocGenManualServiceAsyncJob = new Job("groupaaaDocGenManualServiceAsyncJob");
	public static Job groupDMHaaaBillDueBatchJob = new Job("groupDMHaaaBillDueBatchJob");
	public static Job groupaaaOperationalReportsOrderingAsyncJob = new Job("groupaaaOperationalReportsOrderingAsyncJob");
	public static Job groupbofaRecurringPaymentJob = new Job("groupbofaRecurringPaymentJob");
	public static Job groupaaaInsuranceIDCardIssueAsyncJob = new Job("groupaaaInsuranceIDCardIssueAsyncJob");
	public static Job groupaaaGenerateEscheatmentReportJob = new Job("groupaaaGenerateEscheatmentReportJob");
	public static Job groupaaaImportMiniPupPolicyAsyncTaskJob = new Job("groupaaaImportMiniPupPolicyAsyncTaskJob");
	public static Job groupaaaMvrRenewAsyncBatchReceiveJob = new Job("groupaaaMvrRenewAsyncBatchReceiveJob");
	public static Job grouplapsedRenewalProcessJob = new Job("grouplapsedRenewalProcessJob");
	public static Job groupgenerateHomeBankingReminder = new Job("groupgenerateHomeBankingReminder");
	public static Job groupPolicyConversionImportJob = new Job("groupPolicyConversionImportJob");
	public static Job groupaaaAutomatedProcessingInitiationJob = new Job("groupaaaAutomatedProcessingInitiationJob");
	public static Job groupsisauPolicyConversionImportJob = new Job("groupsisauPolicyConversionImportJob");
	public static Job groupaaaPCNSFUnenrollFeedBatchJob = new Job("groupaaaPCNSFUnenrollFeedBatchJob");
	public static Job groupcurrentPremiumReceivablesUpdateJob = new Job("groupcurrentPremiumReceivablesUpdateJob");
	public static Job groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob = new Job("groupaaaMortgageeRenewalReminderAndExpNoticeAsyncJob");
	public static Job groupbpmTaskUnsuspendJob = new Job("groupbpmTaskUnsuspendJob");
	public static Job groupaaaDelayTriggerTOINoticeAsyncJob = new Job("groupaaaDelayTriggerTOINoticeAsyncJob");
	public static Job groupAAABlckLnLedgerJob = new Job("groupAAABlckLnLedgerJob");
	public static Job groupaaaCollectionFeedBatchOrderJob = new Job("groupaaaCollectionFeedBatchOrderJob");
	public static Job grouprefundGenerationJob = new Job("grouprefundGenerationJob");
	public static Job groupaaaInsuranceScoreRenewBachOrderAsyncJob = new Job("groupaaaInsuranceScoreRenewBachOrderAsyncJob");
	public static Job groupmonthlypolicyTransactionLedgerJob = new Job("groupmonthlypolicyTransactionLedgerJob");
	public static Job groupMonthlyAAABlckLnLedgerJob = new Job("groupMonthlyAAABlckLnLedgerJob");
	public static Job grouppremiumReceivablesOnPolicyEffectiveJob = new Job("grouppremiumReceivablesOnPolicyEffectiveJob");
	public static Job groupAAAIsoRenewAsyncBatchReceiveJob = new Job("groupAAAIsoRenewAsyncBatchReceiveJob");
	public static Job groupautomatedProcessingRunReportsServicesJob = new Job("groupautomatedProcessingRunReportsServicesJob");
	public static Job groupaaaMembershipRenewalBatchReceiveAsyncJob = new Job("groupaaaMembershipRenewalBatchReceiveAsyncJob");
	public static Job groupaaaInsuranceScoreRenewalBatchReceiveAsyncJob = new Job("groupaaaInsuranceScoreRenewalBatchReceiveAsyncJob");
	public static Job groupaaaBillDueBatchJob = new Job("groupaaaBillDueBatchJob");
	public static Job groupaaaDocgenFormEventHistoryPurgeJob = new Job("groupaaaDocgenFormEventHistoryPurgeJob");
	public static Job groupPolicyDoNotRenewAsyncJob = new Job("groupPolicyDoNotRenewAsyncJob");
	public static Job grouprenewalImageRatingAsyncTaskJob = new Job("grouprenewalImageRatingAsyncTaskJob");
	public static Job groupaaaRiskAlertGenerationJob = new Job("groupaaaRiskAlertGenerationJob");
	public static Job groupautomatedProcessingRatingJob = new Job("groupautomatedProcessingRatingJob");
	public static Job groupDMHbofaRecurringPaymentJob = new Job("groupDMHbofaRecurringPaymentJob");
	public static Job groupaaaPendingDeficiencyMigrationAsyncJob = new Job("groupaaaPendingDeficiencyMigrationAsyncJob");
	public static Job groupwaivedFeeBalanceFixJob = new Job("groupwaivedFeeBalanceFixJob");
	public static Job groupautomatedProcessingIssuingOrProposingJob = new Job("groupautomatedProcessingIssuingOrProposingJob");
	public static Job groupaaaDataArchive = new Job("groupaaaDataArchive");
	public static Job groupactivityTimeoutJob = new Job("groupactivityTimeoutJob");
	public static Job grouppreRenewalReminderGenerationAsyncJob = new Job("grouppreRenewalReminderGenerationAsyncJob");
	public static Job groupaaaRemittanceFeedBatchReceiveJob = new Job("groupaaaRemittanceFeedBatchReceiveJob");
	public static Job groupapplyPendingTransactionsAsyncJob = new Job("groupapplyPendingTransactionsAsyncJob");
	public static Job groupaaaAgencySweepJob = new Job("groupaaaAgencySweepJob");
	public static Job groupAAAEscheatmentProcessAsyncJob = new Job("groupAAAEscheatmentProcessAsyncJob");
	public static Job groupearnedPremiumWriteoffProcessingJob = new Job("groupearnedPremiumWriteoffProcessingJob");
	public static Job groupoffCycleInvoiceGenerationJob = new Job("groupoffCycleInvoiceGenerationJob");
	public static Job groupaaaDataUpdate = new Job("groupaaaDataUpdate");
	public static Job groupaaaUserRepositoryAgencyLoadBatchJob = new Job("groupaaaUserRepositoryAgencyLoadBatchJob");
	public static Job groupfeecleanupjob = new Job("groupfeecleanupjob");
	public static Job groupAAAGenerateLTRNoticeJob = new Job("groupAAAGenerateLTRNoticeJob");
	public static Job groupboaCheckReconciliationBatchOrderJob = new Job("groupboaCheckReconciliationBatchOrderJob");
	public static Job groupaaaBatchMarkerJob = new Job("groupaaaBatchMarkerJob");
	public static Job grouppolicyBORTransferJob = new Job("grouppolicyBORTransferJob");
	public static Job groupledgerStatusUpdateJob = new Job("groupledgerStatusUpdateJob");
	public static Job groupaaaCollectionCancellDebtBatchJob = new Job("groupaaaCollectionCancellDebtBatchJob");

	public enum JobState {
		TRUE, FALSE, FAILED
	}

	public static void setJobState(String jobName, JobState state) {
		jobsState.put(jobName, state);
	}

	public static JobState getJobState(String jobName) {
		JobState s = jobsState.get(jobName);
		if (s == null) {
			s = JobState.FALSE;
			jobsState.put(jobName, JobState.FALSE);
		}
		return s;
	}

	public static void clearJobsState() {
		jobsState = new ConcurrentHashMap<>();
	}

}
