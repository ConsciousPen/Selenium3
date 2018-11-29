package aaa.helpers.jobs;

import java.util.Arrays;
import java.util.List;
import com.exigen.ipb.etcsa.utils.batchjob.Job;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;

public class BatchJobGroup {
	public static JobGroup aaaAutomatedProcessingInitiationJob = new JobGroup("aaaAutomatedProcessingInitiationJob", BatchJob.aaaAutomatedProcessingInitiationJob);
	public static JobGroup aaaBalanceTransferConversionAsyncJob = new JobGroup("aaaBalanceTransferConversionAsyncJob", BatchJob.aaaBalanceTransferConversionAsyncJob);
	public static JobGroup aaaBatchMarkerJob = new JobGroup("aaaBatchMarkerJob", BatchJob.aaaBatchMarkerJob);
	public static JobGroup aaaBillDueBatchJob = new JobGroup("aaaBillDueBatchJob", BatchJob.aaaBillDueBatchJob);
	public static JobGroup aaaBillingInvoiceAsyncTaskJob = new JobGroup("aaaBillingInvoiceAsyncTaskJob", BatchJob.aaaBillingInvoiceAsyncTaskJob);
	public static JobGroup aaaBlckLnLedgerJob = new JobGroup("aaaBlckLnLedgerJob", BatchJob.aaaBlckLnLedgerJob);
	public static JobGroup aaaCancellationConfirmationAsyncJob = new JobGroup("aaaCancellationConfirmationAsyncJob", BatchJob.aaaCancellationConfirmationAsyncJob);
	public static JobGroup aaaCancellationNoticeAsyncJob = new JobGroup("aaaCancellationNoticeAsyncJob", BatchJob.aaaCancellationNoticeAsyncJob);
	public static JobGroup aaaCCardExpiryNoticeJob = new JobGroup("aaaCCardExpiryNoticeJob", BatchJob.aaaCCardExpiryNoticeAsyncJob);
	public static JobGroup aaaCollectionCancelDebtBatchJob = new JobGroup("aaaCollectionCancelDebtBatchJob", BatchJob.aaaCollectionCancellDebtBatchAsyncJob);
	public static JobGroup aaaCreditDisclosureNoticeJob = new JobGroup("aaaCreditDisclosureNoticeJob", BatchJob.aaaCreditDisclosureNoticeJob);
	public static JobGroup aaaDocGenBatchJob = new JobGroup("aaaDocGenBatchJob", BatchJob.aaaDocGenAsyncBatchJob);
	public static JobGroup aaaEscheatmentProcessAsyncJob = new JobGroup("aaaEscheatmentProcessAsyncJob", BatchJob.aaaEscheatmentProcessAsyncJob);
	public static JobGroup aaaGenerateHomeBankingReminderAsyncJob = new JobGroup("aaaGenerateHomeBankingReminderAsyncJob", BatchJob.aaaGenerateHomeBankingReminderAsyncJob);
	public static JobGroup aaaGenerateLTRNoticeJob = new JobGroup("aaaGenerateLTRNoticeJob", BatchJob.aaaGenerateLTRNoticeJob);
	public static JobGroup aaaImportMiniPupPolicyAsyncTaskJob = new JobGroup("aaaImportMiniPupPolicyAsyncTaskJob", BatchJob.aaaImportMiniPupPolicyAsyncTaskJob);
	public static JobGroup aaaImportPolicyHomeCAHdesAsyncTaskJob = new JobGroup("aaaImportPolicyHomeCAHdesAsyncTaskJob", BatchJob.aaaImportPolicyHomeCaHdesAsyncTaskJob);
	public static JobGroup aaaImportPolicyHomeCaSisAsyncTaskJob = new JobGroup("aaaImportPolicyHomeCaSisAsyncTaskJob", BatchJob.aaaImportPolicyHomeCaSisAsyncTaskJob);
	public static JobGroup aaaInsuranceScoreRenewalBatchOrderAsyncJob = new JobGroup("aaaInsuranceScoreRenewalBatchOrderAsyncJob", BatchJob.aaaInsuranceScoreRenewBachOrderAsyncJob);
	public static JobGroup aaaInsuranceScoreRenewalBatchReceiveAsyncJob = new JobGroup("aaaInsuranceScoreRenewalBatchReceiveAsyncJob", BatchJob.aaaInsuranceScoreRenewalBatchReceiveAsyncJob);
	public static JobGroup aaaMembershipRenewalBatchOrderAsyncJob = new JobGroup("aaaMembershipRenewalBatchOrderAsyncJob", BatchJob.aaaMembershipRenewalBatchOrderAsyncJob);
	public static JobGroup aaaMembershipRenewalBatchReceiveAsyncJob = new JobGroup("aaaMembershipRenewalBatchReceiveAsyncJob", BatchJob.aaaMembershipRenewalBatchReceiveAsyncJob);
	public static JobGroup aaaMortgageeRenewalReminderAndExpNoticeAsyncJob = new JobGroup("aaaMortgageeRenewalReminderAndExpNoticeAsyncJob", BatchJob.aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	public static JobGroup aaaOffCycleBillingInvoiceAsyncJob = new JobGroup("aaaOffCycleBillingInvoiceAsyncJob", BatchJob.aaaOffCycleBillingInvoiceAsyncJob);
	public static JobGroup aaaPaymentCentralRejectFeedAsyncJob = new JobGroup("aaaPaymentCentralRejectFeedAsyncJob", BatchJob.aaaPaymentCentralRejectFeedAsyncJob);
	public static JobGroup aaaPreRenewalNoticeAsyncJob = new JobGroup("aaaPreRenewalNoticeAsyncJob", BatchJob.aaaPreRenewalNoticeAsyncJob);
	public static JobGroup aaaRecurringPaymentsProcessingJob = new JobGroup("aaaRecurringPaymentsProcessingJob", BatchJob.aaaRecurringPaymentsAsyncProcessJob);
	public static JobGroup aaaRecurringPaymentsResponseProcessAsyncJob = new JobGroup("aaaRecurringPaymentsResponseProcessAsyncJob", BatchJob.aaaRecurringPaymentsResponseProcessAsyncJob);
	public static JobGroup aaaRefundCancellationAsyncJob = new JobGroup("aaaRefundCancellationAsyncJob", BatchJob.aaaRefundCancellationAsyncJob);
	public static JobGroup aaaRefundDisbursementAsyncJob = new JobGroup("aaaRefundDisbursementAsyncJob", BatchJob.aaaRefundDisbursementAsyncJob);
	public static JobGroup aaaRefundGenerationAsyncJob = new JobGroup("aaaRefundGenerationAsyncJob", BatchJob.aaaRefundGenerationAsyncJob);
	public static JobGroup aaaRefundsDisbursementReceiveInfoAsyncJob = new JobGroup("aaaRefundsDisbursementReceiveInfoAsyncJob", BatchJob.aaaRefundsDisbursementReceiveInfoAsyncJob);
	public static JobGroup aaaRemittanceFeedAsyncBatchReceiveJob = new JobGroup("aaaRemittanceFeedAsyncBatchReceiveJob", BatchJob.aaaRemittanceFeedAsyncBatchReceiveJob);
	public static JobGroup aaaRenewalNoticeBillAsyncJob = new JobGroup("aaaRenewalNoticeBillAsyncJob", BatchJob.aaaRenewalNoticeBillAsyncJob);
	public static JobGroup aaaRenewalRedZoneImmediateBillAsyncJob = new JobGroup("aaaRenewalRedZoneImmediateBillAsyncJob", BatchJob.aaaRenewalRedZoneImmediateBillAsyncJob);
	public static JobGroup aaaRenewalReminderGenerationAsyncJob = new JobGroup("aaaRenewalReminderGenerationAsyncJob", BatchJob.aaaRenewalReminderGenerationAsyncJob);
	public static JobGroup applyPendingTransactionsAsyncJob = new JobGroup("applyPendingTransactionsAsyncJob", BatchJob.applyPendingTransactionsAsyncJob);
	public static JobGroup automatedProcessingBypassingAndErrorsReportGenerationJob = new JobGroup("automatedProcessingBypassingAndErrorsReportGenerationJob", BatchJob.automatedProcessingBypassingAndErrorsReportGenerationJob);
	public static JobGroup automatedProcessingIssuingOrProposingJob = new JobGroup("automatedProcessingIssuingOrProposingJob", BatchJob.automatedProcessingIssuingOrProposingJob);
	public static JobGroup automatedProcessingRatingJob = new JobGroup("automatedProcessingRatingJob", BatchJob.automatedProcessingRatingJob);
	public static JobGroup automatedProcessingRunReportsServicesJob = new JobGroup("automatedProcessingRunReportsServicesJob", BatchJob.automatedProcessingRunReportsServicesJob);
	public static JobGroup automatedProcessingStrategyStatusUpdateJob = new JobGroup("automatedProcessingStrategyStatusUpdateJob", BatchJob.automatedProcessingStrategyStatusUpdateJob);
	public static JobGroup cftDcsEodJob = new JobGroup("cftDcsEodJob", getCftDcsEodJob_batchJobs());
	public static JobGroup changeCancellationPendingPoliciesStatus = new JobGroup("changeCancellationPendingPoliciesStatus", BatchJob.changeCancellationPendingPoliciesStatusJob);
	public static JobGroup clearBalanceDataLedgerJob = new JobGroup("clearBalanceDataLedgerJob", BatchJob.policyTransactionLedgerJob);//+Parameter "noBalanceValidation"
	public static JobGroup collectionFeedBatch_earnedPremiumWriteOff = new JobGroup("collectionFeedBatch_earnedPremiumWriteOff", Arrays.asList(BatchJob.collectionFeedBatchOrderJob, BatchJob.earnedPremiumWriteoffProcessingJob));
	public static JobGroup CumulativeFileReadJob = new JobGroup("CumulativeFileReadJob", BatchJob.cumulativeFileReadJob);
	public static JobGroup earnedPremiumBillGenerationJob = new JobGroup("earnedPremiumBillGenerationJob", Arrays.asList(BatchJob.aaaCollectionCancellDebtBatchAsyncJob, BatchJob.aaaDocGenAsyncBatchJob));
	public static JobGroup earnedPremiumPostingAsyncTaskGenerationJob = new JobGroup("earnedPremiumPostingAsyncTaskGenerationJob", BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
	public static JobGroup earnedPremiumWriteoffProcessingJob = new JobGroup("earnedPremiumWriteoffProcessingJob", BatchJob.earnedPremiumWriteoffProcessingJob);
	public static JobGroup ImportMiniPolicyAsyncTaskJob = new JobGroup("ImportMiniPolicyAsyncTaskJob", BatchJob.importMiniPolicyAsyncTaskJob);
	public static JobGroup membershipValidationJob = new JobGroup("membershipValidationJob", getMembershipValidationJob_batchJobs());
	public static JobGroup policyAutomatedRenewalAsyncTaskGenerationJob = new JobGroup("policyAutomatedRenewalAsyncTaskGenerationJob", BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob);
	public static JobGroup policyBORTransferJob = new JobGroup("policyBORTransferJob", BatchJob.policyBORTransferJob);
	public static JobGroup PolicyConversionImportJobSmoke = new JobGroup("PolicyConversionImportJobSmoke", BatchJob.policyConversionImportAsyncTaskJob);
	public static JobGroup policyDoNotRenewAsyncJob = new JobGroup("policyDoNotRenewAsyncJob", BatchJob.policyDoNotRenewAsyncJob);
	public static JobGroup policyExportToAgentDownloadsJob = new JobGroup("policyExportToAgentDownloadsJob", BatchJob.policyExportToAgentDownloadsJob);
	public static JobGroup policyLapsedRenewalProcessAsyncJob = new JobGroup("policyLapsedRenewalProcessAsyncJob", BatchJob.policyLapsedRenewalProcessAsyncJob);
	public static JobGroup policyStatusUpdateJob = new JobGroup("policyStatusUpdateJob", BatchJob.policyStatusUpdateJob);
	public static JobGroup policyTransactionLedgerJob = new JobGroup("policyTransactionLedgerJob", BatchJob.policyTransactionLedgerJob);//+Parameter "MONTHLY"
	public static JobGroup policyTransactionLedgerJob_NonMonthly = new JobGroup("policyTransactionLedgerJob_NonMonthly", BatchJob.policyTransactionLedgerJob);
	public static JobGroup premiumReceivablesOnPolicyEffectiveJob = new JobGroup("premiumReceivablesOnPolicyEffectiveJob", BatchJob.premiumReceivablesOnPolicyEffectiveJob);
	public static JobGroup preRenewalReminderGenerationAsyncJob = new JobGroup("preRenewalReminderGenerationAsyncJob", BatchJob.preRenewalReminderGenerationAsyncJob);
	public static JobGroup Renewal_Offer_Generation_Part1 = new JobGroup("Renewal_Offer_Generation_Part1", getRenewal_Offer_Generation_Part1_batchJobs());
	public static JobGroup Renewal_Offer_Generation_Part2 = new JobGroup("Renewal_Offer_Generation_Part2", getRenewal_Offer_Generation_Part2_batchJobs());
	public static JobGroup renewalImageRatingAsyncTaskJob = new JobGroup("renewalImageRatingAsyncTaskJob", BatchJob.renewalImageRatingAsyncTaskJob);
	public static JobGroup RenewalImageRatingAsyncTaskJob = new JobGroup("RenewalImageRatingAsyncTaskJob", BatchJob.renewalImageRatingAsyncTaskJob);

	private static List<Job> getCftDcsEodJob_batchJobs() {
		return Arrays.asList(
				BatchJob.aaaCCardExpiryNoticeAsyncJob,
				BatchJob.aaaMvrRenewBatchOrderAsyncJob,
				BatchJob.claimsRenewBatchOrderJob,
				BatchJob.aaaMembershipRenewalBatchOrderAsyncJob,
				BatchJob.aaaInsuranceScoreRenewBachOrderAsyncJob,
				BatchJob.aaaMvrRenewAsyncBatchReceiveJob,
				BatchJob.claimsRenewBatchReceiveJob,
				BatchJob.aaaMembershipRenewalBatchReceiveAsyncJob,
				BatchJob.aaaInsuranceScoreRenewalBatchReceiveAsyncJob,
				BatchJob.cumulativeFileReadJob,
				BatchJob.aaaGenerateHomeBankingReminderAsyncJob,
				BatchJob.policyStatusUpdateJob,
				BatchJob.aaaRecurringPaymentsAsyncProcessJob,
				BatchJob.aaaRemittanceFeedAsyncBatchReceiveJob,
				BatchJob.applyPendingTransactionsAsyncJob,
				BatchJob.aaaCancellationNoticeAsyncJob,
				BatchJob.aaaCancellationConfirmationAsyncJob,
				BatchJob.aaaCollectionCancellDebtBatchAsyncJob,
				BatchJob.aaaRecurringPaymentsResponseProcessAsyncJob,
				BatchJob.aaaBCTCEAPlcyDtlsAsyncJob,
				BatchJob.bofaRejectionPaymentJob,
				BatchJob.aaaPaymentCentralRejectFeedAsyncJob,
				BatchJob.aaaRefundGenerationAsyncJob,
				BatchJob.aaaAgencyConsolidationBatchJob,
				BatchJob.bofaRecurringPaymentJob,
				BatchJob.aaaOffCycleBillingInvoiceAsyncJob,
				BatchJob.aaaBillingInvoiceAsyncTaskJob,
				BatchJob.preRenewalReminderGenerationAsyncJob,
				BatchJob.premiumReceivablesOnPolicyEffectiveJob,
				BatchJob.changeCancellationPendingPoliciesStatusJob,
				BatchJob.ledgerStatusUpdateJob,
				BatchJob.policyStatusUpdateJob,
				BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob,
				BatchJob.isoRenewalBatchOrderJob,
				BatchJob.AAAIsoRenewAsyncBatchReceiveJob,
				BatchJob.aaaRenewalDataRefreshAsyncJob,
				BatchJob.renewalValidationAsyncTaskJob,
				BatchJob.renewalImageRatingAsyncTaskJob,
				BatchJob.renewalOfferAsyncTaskJob,
				BatchJob.policyLapsedRenewalProcessAsyncJob,
				BatchJob.policyExportToAgentDownloadsJob,
				BatchJob.aaaEscheatmentProcessAsyncJob,
				BatchJob.aaaGenerateEscheatmentReportJob,
				BatchJob.collectionFeedBatchOrderJob,
				BatchJob.earnedPremiumWriteoffProcessingJob,
				BatchJob.aaaRenewalNoticeBillAsyncJob,
				BatchJob.aaaPreRenewalNoticeAsyncJob,
				BatchJob.aaaGenerateLTRNoticeJob,
				BatchJob.aaaBillDueBatchJob,
				BatchJob.aaaDocGenAsyncBatchJob,
				BatchJob.dmvBatchJob,
				BatchJob.activityTimeoutJob,
				BatchJob.activityHistoryJob,
				BatchJob.premiumReceivablesOnPolicyEffectiveJob);
	}

	private static List<Job> getMembershipValidationJob_batchJobs() {
		return Arrays.asList(
				BatchJob.aaaAutomatedProcessingInitiationJob,
				BatchJob.automatedProcessingRatingJob,
				BatchJob.automatedProcessingRunReportsServicesJob,
				BatchJob.automatedProcessingIssuingOrProposingJob,
				BatchJob.automatedProcessingStrategyStatusUpdateJob,
				BatchJob.automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	private static List<Job> getRenewal_Offer_Generation_Part1_batchJobs() {
		return Arrays.asList(
				BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob,
				BatchJob.aaaMvrRenewBatchOrderAsyncJob,
				BatchJob.claimsRenewBatchOrderJob,
				BatchJob.aaaMembershipRenewalBatchOrderAsyncJob,
				BatchJob.aaaInsuranceScoreRenewBachOrderAsyncJob);
	}

	private static List<Job> getRenewal_Offer_Generation_Part2_batchJobs() {
		return Arrays.asList(
				BatchJob.aaaMvrRenewAsyncBatchReceiveJob,
				BatchJob.claimsRenewBatchReceiveJob,
				BatchJob.aaaMembershipRenewalBatchReceiveAsyncJob,
				BatchJob.aaaInsuranceScoreRenewalBatchReceiveAsyncJob,
				BatchJob.policyAutomatedRenewalAsyncTaskGenerationJob,
				BatchJob.renewalValidationAsyncTaskJob,
				BatchJob.renewalImageRatingAsyncTaskJob,
				BatchJob.renewalOfferAsyncTaskJob);
	}
}
