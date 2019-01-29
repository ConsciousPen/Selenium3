package aaa.helpers.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

public class BatchJob {

	public static Job policyAutomatedRenewalAsyncTaskGenerationJob = new Job("policyAutomatedRenewalAsyncTaskGenerationJob", new HashMap<>(), getRenewPart1JobFolders());
	public static Job aaaRecurringPaymentsProcessingJob = new Job("aaaRecurringPaymentsProcessingJob");
	public static Job aaaAgencyConsolidationBatchJob = new Job("aaaAgencyConsolidationBatchJob");
	public static Job aaaAutomatedProcessingInitiationJob = new Job("aaaAutomatedProcessingInitiationJob");
	public static Job aaaBalanceTransferConversionAsyncJob = new Job("aaaBalanceTransferConversionAsyncJob",
			getAaaBalanceTransferConversionAsyncJobParameters(), Arrays.asList("/home/mp2/pas/sit/BIL_E_EBPHUB_EXGBLL_7900_D/inbound"));
	public static Job aaaBatchMarkerJob = new Job("aaaBatchMarkerJob");
	public static Job aaaBCTCEAPlcyDtlsAsyncJob = new Job("aaaBCTCEAPlcyDtlsAsyncJob");
	public static Job aaaBillDueBatchJob = new Job("aaaBillDueBatchJob");
	public static Job aaaBillingInvoiceAsyncTaskJob = new Job("aaaBillingInvoiceAsyncTaskJob");
	public static Job aaaBillingManualServiceUpdatePaymentPlanAsyncJob = new Job("aaaBillingManualServiceUpdatePaymentPlanAsyncJob");
	public static Job aaaBlckLnLedgerJob = new Job("aaaBlckLnLedgerJob");
	public static Job aaaCancellationConfirmationAsyncJob = new Job("aaaCancellationConfirmationAsyncJob");
	public static Job aaaCancellationNoticeAsyncJob = new Job("aaaCancellationNoticeAsyncJob");
	public static Job aaaCarfaxReportImportAsyncJob = new Job("aaaCarfaxReportImportAsyncJob");
	public static Job aaaCCardExpiryNoticeAsyncJob = new Job("aaaCCardExpiryNoticeAsyncJob");
	public static Job aaaClueRenewAsyncBatchReceiveJob = new Job("aaaClueRenewAsyncBatchReceiveJob");
	public static Job aaaClueRenewBatchOrderAsyncJob = new Job("aaaClueRenewBatchOrderAsyncJob");
	public static Job aaaCollectionCancellDebtBatchAsyncJob = new Job("aaaCollectionCancellDebtBatchAsyncJob");
	public static Job aaaCompanyDeclineBatchJob = new Job("aaaCompanyDeclineBatchJob");
	public static Job aaaConvertedPolicyStatusUpdateAsyncTaskJob = new Job("aaaConvertedPolicyStatusUpdateAsyncTaskJob");
	public static Job aaaConvRenDetailBatchJob = new Job("aaaConvRenDetailBatchJob");
	public static Job aaaCreditDisclosureNoticeJob = new Job("aaaCreditDisclosureNoticeJob");
	public static Job aaaDataArchiveJob = new Job("aaaDataArchiveJob");
	public static Job aaaDatabaseMigrationJob = new Job("aaaDatabaseMigrationJob");
	public static Job aaaDataServiceJob = new Job("aaaDataServiceJob");
	public static Job aaaDataUpdateJob = new Job("aaaDataUpdateJob");
	public static Job aaaDelayTriggerTOINoticeAsyncJob = new Job("aaaDelayTriggerTOINoticeAsyncJob");
	public static Job aaaDocGenBatchJob = new Job("aaaDocGenAsyncBatchJob");
	public static Job aaaDocgenFormEventHistoryPurgeJob = new Job("aaaDocgenFormEventHistoryPurgeJob");
	public static Job aaaDocGenManualServiceAsyncJob = new Job("aaaDocGenManualServiceAsyncJob");
	public static Job aaaEarnedPremiumPostingAsyncTaskGenerationJob = new Job("aaaEarnedPremiumPostingAsyncTaskGenerationJob");
	public static Job aaaEARSExtractJob = new Job("aaaEARSExtractJob");
	public static Job aaaEARSReceiveJob = new Job("aaaEARSReceiveJob");
	public static Job aaaEscheatmentProcessAsyncJob = new Job("aaaEscheatmentProcessAsyncJob");
	public static Job aaaEscheatmentVoidTransferProcessAsyncJob = new Job("aaaEscheatmentVoidTransferProcessAsyncJob");
	public static Job aaaGenerateEscheatmentReportJob = new Job("aaaGenerateEscheatmentReportJob");
	public static Job aaaGenerateHomeBankingReminderAsyncJob = new Job("aaaGenerateHomeBankingReminderAsyncJob");
	public static Job aaaGenerateLTRNoticeJob = new Job("aaaGenerateLTRNoticeJob");
	public static Job aaaGenTOIForLapsedPolicyJob = new Job("aaaGenTOIForLapsedPolicyJob");
	public static Job aaaHDESCEAPolicyDetailsFeedAsyncJob = new Job("aaaHDESCEAPolicyDetailsFeedAsyncJob");
	public static Job aaaImportMiniPupPolicyAsyncTaskJob = new Job("aaaImportMiniPupPolicyAsyncTaskJob",
			getAaaImportMiniPupPolicyAsyncTaskJobParameters(), getFolders(getAaaImportMiniPupPolicyAsyncTaskJobParameters()));
	public static Job aaaImportMiniPupPolicyJob = new Job("aaaImportMiniPupPolicyJob");
	public static Job aaaImportPolicyHomeCaHdesAsyncTaskJob = new Job("aaaImportPolicyHomeCaHdesAsyncTaskJob",
			getAaaImportPolicyHomeCaHdesAsyncTaskJobParameters(), getFolders(getAaaImportPolicyHomeCaHdesAsyncTaskJobParameters()));
	public static Job aaaImportPolicyHomeCaHdesJob = new Job("aaaImportPolicyHomeCaHdesJob");
	public static Job aaaImportPolicyHomeCaSisAsyncTaskJob = new Job("aaaImportPolicyHomeCaSisAsyncTaskJob",
			getAaaImportPolicyHomeCaSisAsyncTaskJobParameters(), getFolders(getAaaImportPolicyHomeCaSisAsyncTaskJobParameters()));
	public static Job aaaImportPolicyHomeCaSisJob = new Job("aaaImportPolicyHomeCaSisJob");
	public static Job aaaInsuranceIDCardIssueAsyncJob = new Job("aaaInsuranceIDCardIssueAsyncJob");
	public static Job aaaInsuranceScoreRenewalBatchReceiveAsyncJob = new Job("aaaInsuranceScoreRenewalBatchReceiveAsyncJob");
	public static Job aaaInsuranceScoreRenewBachOrderAsyncJob = new Job("aaaInsuranceScoreRenewBachOrderAsyncJob");
	public static Job AAAIsoRenewAsyncBatchReceiveJob = new Job("AAAIsoRenewAsyncBatchReceiveJob");
	public static Job aaaLedgerTransactionAuditJob = new Job("aaaLedgerTransactionAuditJob");
	public static Job aaaMembershipRenewalBatchOrderAsyncJob = new Job("aaaMembershipRenewalBatchOrderAsyncJob");
	public static Job aaaMembershipRenewalBatchReceiveAsyncJob = new Job("aaaMembershipRenewalBatchReceiveAsyncJob");
	public static Job aaaMortgageeRenewalReminderAndExpNoticeAsyncJob = new Job("aaaMortgageeRenewalReminderAndExpNoticeAsyncJob");
	public static Job aaaMvrRenewAsyncBatchReceiveJob = new Job("aaaMvrRenewAsyncBatchReceiveJob");
	public static Job aaaMvrRenewBatchOrderAsyncJob = new Job("aaaMvrRenewBatchOrderAsyncJob");
	public static Job offCycleBillingInvoiceAsyncJob = new Job("aaaOffCycleBillingInvoiceAsyncJob");
	public static Job aaaOperationalReportsOrderingAsyncJob = new Job("aaaOperationalReportsOrderingAsyncJob");
	public static Job aaaPaidThroughDateUpdateJob = new Job("aaaPaidThroughDateUpdateJob");
	public static Job aaaPaymentCentralRejectFeedAsyncJob = new Job("aaaPaymentCentralRejectFeedAsyncJob");
	public static Job aaaPCNSFUnenrollFeedBatchJob = new Job("aaaPCNSFUnenrollFeedBatchJob");
	public static Job aaaPendingDeficiencyMigrationAsyncJob = new Job("aaaPendingDeficiencyMigrationAsyncJob");
	public static Job aaaPligaFeeAsyncJob = new Job("aaaPligaFeeAsyncJob");
	public static Job aaaPolicyAutomatedRenewalAsyncTaskGenerationJob = new Job("aaaPolicyAutomatedRenewalAsyncTaskGenerationJob");
	public static Job aaaPreRenewalNoticeAsyncJob = new Job("aaaPreRenewalNoticeAsyncJob");
	public static Job aaaRecordCustomerDetailsJob = new Job("aaaRecordCustomerDetailsJob");
	public static Job aaaRecurringPaymentsAsyncProcessJob = new Job("aaaRecurringPaymentsAsyncProcessJob");
	public static Job aaaRecurringPaymentsResponseProcessAsyncJob = new Job("aaaRecurringPaymentsResponseProcessAsyncJob");
	public static Job aaaRefundCancellationAsyncJob = new Job("aaaRefundCancellationAsyncJob");
	public static Job aaaRefundDisbursementAsyncJob = new Job("aaaRefundDisbursementAsyncJob");
	public static Job aaaRefundGenerationAsyncJob = new Job("aaaRefundGenerationAsyncJob");
	public static Job aaaRefundsDisbursementReceiveInfoAsyncJob = new Job("aaaRefundsDisbursementReceiveInfoAsyncJob");
	public static Job aaaRefundsDisbursementRejectionsAsyncJob = new Job("aaaRefundsDisbursementRejectionsAsyncJob");
	public static Job aaaRemittanceFeedAsyncBatchReceiveJob = new Job("aaaRemittanceFeedAsyncBatchReceiveJob");
	public static Job aaaRenewalDataRefreshAsyncJob = new Job("aaaRenewalDataRefreshAsyncJob");
	public static Job aaaRenewalNoticeBillAsyncJob = new Job("aaaRenewalNoticeBillAsyncJob");
	public static Job aaaRenewalRedZoneImmediateBillAsyncJob = new Job("aaaRenewalRedZoneImmediateBillAsyncJob");
	public static Job aaaRenewalReminderGenerationAsyncJob = new Job("aaaRenewalReminderGenerationAsyncJob");
	public static Job aaaRiskAlertGenerationJob = new Job("aaaRiskAlertGenerationJob");
	public static Job aaaSimplyRateJob = new Job("aaaSimplyRateJob");
	public static Job aaaSimplyRateSyncJob = new Job("aaaSimplyRateSyncJob");
	public static Job aaaTelematicSafetyScoreOrderAsyncJob = new Job("aaaTelematicSafetyScoreOrderAsyncJob");
	public static Job aaaUserRepositoryAgencyLoadBatchJob = new Job("aaaUserRepositoryAgencyLoadBatchJob");
	public static Job activityHistoryJob = new Job("activityHistoryJob");
	public static Job activitySummarizationJob = new Job("activitySummarizationJob");
	public static Job activityTimeoutJob = new Job("activityTimeoutJob");
	public static Job agencyHoldStatusUpdateJob = new Job("agencyHoldStatusUpdateJob");
	public static Job anniversaryAgingProcessingJob = new Job("anniversaryAgingProcessingJob");
	public static Job applyPendingTransactionsAsyncJob = new Job("applyPendingTransactionsAsyncJob");
	public static Job applyPendingTransactionsJob = new Job("applyPendingTransactionsJob");
	public static Job automatedProcessingBypassingAndErrorsReportGenerationJob = new Job("automatedProcessingBypassingAndErrorsReportGenerationJob");
	public static Job automatedProcessingInitiationJob = new Job("automatedProcessingInitiationJob");
	public static Job automatedProcessingIssuingOrProposingJob = new Job("automatedProcessingIssuingOrProposingJob");
	public static Job automatedProcessingRatingJob = new Job("automatedProcessingRatingJob");
	public static Job automatedProcessingRunReportsServicesJob = new Job("automatedProcessingRunReportsServicesJob");
	public static Job automatedProcessingStrategyStatusUpdateJob = new Job("automatedProcessingStrategyStatusUpdateJob");
	public static Job avsReceiveAsyncJob = new Job("avsReceiveAsyncJob");
	public static Job avsReceiveJob = new Job("avsReceiveJob");
	public static Job balanceMigrationJob = new Job("balanceMigrationJob");
	public static Job balanceMigrationPriorityJob = new Job("balanceMigrationPriorityJob");
	public static Job balanceTransferReconciliationJob = new Job("balanceTransferReconciliationJob");
	public static Job batchPaymentProcessingJob = new Job("batchPaymentProcessingJob");
	public static Job billingInvoiceGenerationJob = new Job("billingInvoiceGenerationJob");
	public static Job billingMoratoriumJob = new Job("billingMoratoriumJob");
	public static Job billingProcessDefinitionMigrationJob = new Job("billingProcessDefinitionMigrationJob");
	public static Job boaCheckReconciliationBatchReceiveJob = new Job("boaCheckReconciliationBatchReceiveJob");
	public static Job bofaRecurringPaymentJob = new Job("bofaRecurringPaymentJob");
	public static Job bofaRejectionPaymentJob = new Job("bofaRejectionPaymentJob");
	public static Job bpmTaskGenerationJob = new Job("bpmTaskGenerationJob");
	public static Job bulletinExpirationJob = new Job("bulletinExpirationJob");
	public static Job calculatePremiumDifferenceJob = new Job("calculatePremiumDifferenceJob");
	public static Job campaignArchiveJob = new Job("campaignArchiveJob");
	public static Job campaignCompletionJob = new Job("campaignCompletionJob");
	public static Job campaignStartJob = new Job("campaignStartJob");
	public static Job campaignSuspendJob = new Job("campaignSuspendJob");
	public static Job cancellationConfirmationGenerationJob = new Job("cancellationConfirmationGenerationJob");
	public static Job cancellationNoticeGenerationJob = new Job("cancellationNoticeGenerationJob");
	public static Job cascadingTransactionCertificateProcessingDispatchingJob = new Job("cascadingTransactionCertificateProcessingDispatchingJob");
	public static Job cascadingTransactionCompletionDetectionJob = new Job("cascadingTransactionCompletionDetectionJob");
	public static Job cascadingTransactionProcessingInitiationJob = new Job("cascadingTransactionProcessingInitiationJob");
	public static Job changeCancellationPendingPoliciesStatusJob = new Job("changeCancellationPendingPoliciesStatusJob");
	public static Job claimsRenewBatchOrderJob = new Job("claimsRenewBatchOrderJob");
	public static Job claimsRenewBatchReceiveJob = new Job("claimsRenewBatchReceiveJob");
	public static Job collectionFeedBatchOrderJob = new Job("collectionFeedBatchOrderJob");
	public static Job commissionAsyncDisbursementJob = new Job("commissionAsyncDisbursementJob");
	public static Job commissionBonusDisbursementJob = new Job("commissionBonusDisbursementJob");
	public static Job commissionBulkAdjustmentProcessingJob = new Job("commissionBulkAdjustmentProcessingJob");
	public static Job commissionDisbursementJob = new Job("commissionDisbursementJob");
	public static Job CommissionInvoiceMigrationJob = new Job("CommissionInvoiceMigrationJob");
	public static Job commissionStatusUpdateJob = new Job("commissionStatusUpdateJob");
	public static Job cumulativeFileReadJob = new Job("cumulativeFileReadJob");
	public static Job currentPremiumReceivablesUpdateJob = new Job("currentPremiumReceivablesUpdateJob");
	public static Job customerAccountPartyConversionJob = new Job("customerAccountPartyConversionJob");
	public static Job dataExportIndividualCustomerJob = new Job("dataExportIndividualCustomerJob");
	public static Job dmvBatchJob = new Job("dmvBatchJob");
	public static Job docgenCleanupJob = new Job("docgenCleanupJob");
	public static Job docgenTestEventJob = new Job("docgenTestEventJob");
	public static Job earnedPremiumCorrectionJob = new Job("earnedPremiumCorrectionJob");
	public static Job earnedPremiumPostingAsyncTaskGenerationJob = new Job("earnedPremiumPostingAsyncTaskGenerationJob");
	public static Job earnedPremiumWriteOffNoticeProcessingJob = new Job("earnedPremiumWriteOffNoticeProcessingJob");
	public static Job earnedPremiumWriteoffProcessingJob = new Job("earnedPremiumWriteoffProcessingJob");
	public static Job executeFileTransferJob = new Job("executeFileTransferJob");
	public static Job expiringProductOwnedOpportunityCreationJob = new Job("expiringProductOwnedOpportunityCreationJob");
	public static Job fileIntakeImportJob = new Job("fileIntakeImportJob");
	public static Job groupEnrollmentProcessingJob = new Job("groupEnrollmentProcessingJob");
	public static Job importBalanceTransferAsyncTaskJob = new Job("importBalanceTransferAsyncTaskJob");
	public static Job importBalanceTransferJob = new Job("importBalanceTransferJob");
	public static Job importCustomerAsyncTaskJob = new Job("importCustomerAsyncTaskJob");
	public static Job importCustomerJob = new Job("importCustomerJob");
	public static Job importCustomerRelationshipAsyncTaskJob = new Job("importCustomerRelationshipAsyncTaskJob");
	public static Job importCustomerRelationshipJob = new Job("importCustomerRelationshipJob");
	public static Job importCustomerUpdateAsyncTaskJob = new Job("importCustomerUpdateAsyncTaskJob");
	public static Job importCustomerUpdateJob = new Job("importCustomerUpdateJob");
	public static Job importDocsAsyncTaskJob = new Job("importDocsAsyncTaskJob");
	public static Job importDocsJob = new Job("importDocsJob");
	public static Job importHeaderJob = new Job("importHeaderJob");
	public static Job importMiniPolicyAsyncTaskJob = new Job("importMiniPolicyAsyncTaskJob",
			getImportMiniPolicyAsyncTaskJobParameters(), getFolders(getImportMiniPolicyAsyncTaskJobParameters()));
	public static Job importMiniPolicyJob = new Job("importMiniPolicyJob");
	public static Job importMiniQuoteAsyncTaskJob = new Job("importMiniQuoteAsyncTaskJob");
	public static Job importMiniQuoteJob = new Job("importMiniQuoteJob");
	public static Job importPolicyAsyncTaskJob = new Job("importPolicyAsyncTaskJob");
	public static Job importPolicyJob = new Job("importPolicyJob");
	public static Job importPolicyTxAsyncTaskJob = new Job("importPolicyTxAsyncTaskJob");
	public static Job importPolicyTxJob = new Job("importPolicyTxJob");
	public static Job importQuoteAsyncTaskJob = new Job("importQuoteAsyncTaskJob");
	public static Job importQuoteJob = new Job("importQuoteJob");
	public static Job importUserNoteAsyncTaskJob = new Job("importUserNoteAsyncTaskJob");
	public static Job importUserNoteJob = new Job("importUserNoteJob");
	public static Job importUserTaskAsyncTaskJob = new Job("importUserTaskAsyncTaskJob");
	public static Job importUserTaskJob = new Job("importUserTaskJob");
	public static Job invoiceDiscardDateMigrationJob = new Job("invoiceDiscardDateMigrationJob");
	public static Job invoiceReminderNoticeProcessingJob = new Job("invoiceReminderNoticeProcessingJob");
	public static Job invoiceSplitMigrationJob = new Job("invoiceSplitMigrationJob");
	public static Job isoRenewalBatchOrderJob = new Job("isoRenewalBatchOrderJob");
	public static Job jobLogCleanup = new Job("jobLogCleanup");
	public static Job jobStatisticsCleanup = new Job("jobStatisticsCleanup");
	public static Job ledgerStatusUpdateJob = new Job("ledgerStatusUpdateJob");
	public static Job legacyPolicyStatusImportJob = new Job("legacyPolicyStatusImportJob");
	public static Job lockBoxDataProcessingJob = new Job("lockBoxDataProcessingJob");
	public static Job lockBoxLookupFileCreatorJob = new Job("lockBoxLookupFileCreatorJob");
	public static Job obsoleteRuleOverrideEntryRemoval = new Job("obsoleteRuleOverrideEntryRemoval");
	public static Job offerExpirationJob = new Job("offerExpirationJob");
	public static Job onlineQuotePurgeJob = new Job("onlineQuotePurgeJob");
	public static Job opportunityInactivateJob = new Job("opportunityInactivateJob");
	public static Job opportunityInactivationNoticeGenerationJob = new Job("opportunityInactivationNoticeGenerationJob");
	public static Job paymentDetailsPartyConversionJob = new Job("paymentDetailsPartyConversionJob");
	public static Job pendingUpdateJob = new Job("pendingUpdateJob");
	public static Job personalBillingAccountPartyConversionJob = new Job("personalBillingAccountPartyConversionJob");
	public static Job renewalClaimOrderAsyncJob = new Job("renewalClaimOrderAsyncJob", getRenewalClaimOrderAsyncJobParameters(), getFolders(getRenewalClaimOrderAsyncJobParameters()));
	public static Job policyBORTransferJob = new Job("policyBORTransferJob");
	public static Job policyBundleExpirationJob = new Job("policyBundleExpirationJob");
	public static Job policyBundleRemovalJob = new Job("policyBundleRemovalJob");
	public static Job policyConversionImportAsyncTaskJob = new Job("policyConversionImportAsyncTaskJob",
			getPolicyConversionImportAsyncTaskJobParameters(), getFolders(getPolicyConversionImportAsyncTaskJobParameters()));
	public static Job policyConversionImportJob = new Job("policyConversionImportJob");
	public static Job policyDoNotRenewAsyncJob = new Job("policyDoNotRenewAsyncJob");
	public static Job policyExportToAgentDownloadsJob = new Job("policyExportToAgentDownloadsJob");
	public static Job lapsedRenewalProcessJob = new Job("policyLapsedRenewalProcessAsyncJob");
	public static Job policyStatusUpdateJob = new Job("policyStatusUpdateJob");
	public static Job policySummaryPartyConversionJob = new Job("policySummaryPartyConversionJob");
	public static Job policyTermExtensionJob = new Job("policyTermExtensionJob");
	public static Job policyTermUbiJob = new Job("policyTermUbiJob");
	public static Job policyTransactionLedgerJob_NonMonthly = new Job("policyTransactionLedgerJob");
	public static Job premiumReceivablesOnPolicyEffectiveJob = new Job("premiumReceivablesOnPolicyEffectiveJob");
	public static Job preRenewalReminderGenerationAsyncJob = new Job("preRenewalReminderGenerationAsyncJob");
	public static Job quoteExpirationJob = new Job("quoteExpirationJob");
	public static Job recurringPaymentNoticesProcessingJob = new Job("recurringPaymentNoticesProcessingJob");
	public static Job recurringPaymentsMethodsExpiryReminderJob = new Job("recurringPaymentsMethodsExpiryReminderJob");
	public static Job recurringPaymentsProcessingJob = new Job("recurringPaymentsProcessingJob");
	public static Job refundGenerationJob = new Job("refundGenerationJob");
	public static Job removeExpiredLocksJob = new Job("removeExpiredLocksJob");
	public static JobGroup cftDcsEodJob = new JobGroup("cftDcsEodJob", getCftDcsEodJob_batchJobs());
	public static Job renewalClaimPropertyReceiveAsyncJob = new Job("renewalClaimPropertyReceiveAsyncJob");
	public static Job renewalClaimReceiveAsyncJob = new Job("renewalClaimReceiveAsyncJob");
	public static Job renewalImageRatingAsyncTaskJob = new Job("renewalImageRatingAsyncTaskJob");
	public static Job renewalOfferAsyncTaskJob = new Job("renewalOfferAsyncTaskJob");
	public static Job renewalProposingJob = new Job("renewalProposingJob");
	public static Job renewalRatingJob = new Job("renewalRatingJob");
	public static Job renewalValidationAsyncTaskJob = new Job("renewalValidationAsyncTaskJob");

	/*	public static Job sampleAsyncTaskGeneratorJob = new Job("sampleAsyncTaskGeneratorJob");
		public static Job sampleCustomerAccountPartyConversionJob = new Job("sampleCustomerAccountPartyConversionJob");
		public static Job sampleJob = new Job("sampleJob");
		public static Job samplePolicyToPartyConversionJob = new Job("samplePolicyToPartyConversionJob");
		public static Job scheduledUserProfileUpdateJob = new Job("scheduledUserProfileUpdateJob");
		public static Job schemeStatusUpdateJob = new Job("schemeStatusUpdateJob");
		public static Job storedProcedureJob = new Job("storedProcedureJob");
		public static Job testJobWithUiParameters = new Job("testJobWithUiParameters");
		public static Job ubiDetailBatchJob = new Job("ubiDetailBatchJob");
		public static Job vendorStatusActivationJob = new Job("vendorStatusActivationJob");
		public static Job waivedFeeBalanceFixJob = new Job("waivedFeeBalanceFixJob");
		public static Job workTaskEscalationJob = new Job("workTaskEscalationJob");
		public static Job workTaskUnsuspendJob = new Job("workTaskUnsuspendJob");*/
	public static JobGroup collectionFeedBatch_earnedPremiumWriteOff = new JobGroup("collectionFeedBatch_earnedPremiumWriteOff", Arrays.asList(collectionFeedBatchOrderJob, earnedPremiumWriteoffProcessingJob));
	public static JobGroup earnedPremiumBillGenerationJob = new JobGroup("earnedPremiumBillGenerationJob", Arrays.asList(aaaCollectionCancellDebtBatchAsyncJob, aaaDocGenBatchJob));
	public static JobGroup membershipValidationJob = new JobGroup("membershipValidationJob", getMembershipValidationJob_batchJobs());
	public static JobGroup renewalOfferGenerationPart1 = new JobGroup("renewalOfferGenerationPart1", getRenewal_Offer_Generation_Part1_batchJobs());
	public static JobGroup renewalOfferGenerationPart2 = new JobGroup("renewalOfferGenerationPart2", getRenewal_Offer_Generation_Part2_batchJobs());
	private static String jobFolderPrefix = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER, "/home/mp2/pas/sit/");

	public static HashMap<String, String> getRenewalClaimOrderAsyncJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", getFormattedFolderPath("%SPAS_B_PASHUB_EXGPAS_4001_D/inbound"));
		parameters.put("processedFolder", getFormattedFolderPath("%SPAS_B_EXGPAS_PASHUB_4001_D/outbound"));
		return parameters;
	}

	private static List<Job> getMembershipValidationJob_batchJobs() {
		return Arrays.asList(
				aaaAutomatedProcessingInitiationJob,
				automatedProcessingRatingJob,
				automatedProcessingRunReportsServicesJob,
				automatedProcessingIssuingOrProposingJob,
				automatedProcessingStrategyStatusUpdateJob,
				automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	private static List<Job> getRenewal_Offer_Generation_Part1_batchJobs() {
		return Arrays.asList(
				policyAutomatedRenewalAsyncTaskGenerationJob,
				aaaMvrRenewBatchOrderAsyncJob,
				claimsRenewBatchOrderJob,
				aaaMembershipRenewalBatchOrderAsyncJob,
				aaaInsuranceScoreRenewBachOrderAsyncJob);
	}

	private static List<Job> getRenewal_Offer_Generation_Part2_batchJobs() {
		return Arrays.asList(
				aaaMvrRenewAsyncBatchReceiveJob,
				claimsRenewBatchReceiveJob,
				aaaMembershipRenewalBatchReceiveAsyncJob,
				aaaInsuranceScoreRenewalBatchReceiveAsyncJob,
				policyAutomatedRenewalAsyncTaskGenerationJob,
				renewalValidationAsyncTaskJob,
				renewalImageRatingAsyncTaskJob,
				renewalOfferAsyncTaskJob);
	}

	private static List<String> getRenewPart1JobFolders() {
		List<String> foldersTemplate = Arrays.asList(
				"%sPAS_B_EXGPAS_PASHUB_4004_D/inbound",
				"%sPAS_B_EXGPAS_PASHUB_4004_D/outbound",
				"%sPAS_B_EXGPAS_DMVFED_3051_D/inbound",
				"%sPAS_B_EXGPAS_DMVFED_3051_D/outbound",
				"%sPAS_B_EXGPAS_PASHUB_4001_D/inbound",
				"%sPAS_B_EXGPAS_PASHUB_4001_D/outbound");
		return getFormattedJobFolders(foldersTemplate);
	}

	private static List<Job> getCftDcsEodJob_batchJobs() {
		return Arrays.asList(
				aaaCCardExpiryNoticeAsyncJob,
				aaaMvrRenewBatchOrderAsyncJob,
				claimsRenewBatchOrderJob,
				aaaMembershipRenewalBatchOrderAsyncJob,
				aaaInsuranceScoreRenewBachOrderAsyncJob,
				aaaMvrRenewAsyncBatchReceiveJob,
				claimsRenewBatchReceiveJob,
				aaaMembershipRenewalBatchReceiveAsyncJob,
				aaaInsuranceScoreRenewalBatchReceiveAsyncJob,
				cumulativeFileReadJob,
				aaaGenerateHomeBankingReminderAsyncJob,
				policyStatusUpdateJob,
				aaaRecurringPaymentsAsyncProcessJob,
				aaaRemittanceFeedAsyncBatchReceiveJob,
				applyPendingTransactionsAsyncJob,
				aaaCancellationNoticeAsyncJob,
				aaaCancellationConfirmationAsyncJob,
				aaaCollectionCancellDebtBatchAsyncJob,
				aaaRecurringPaymentsResponseProcessAsyncJob,
				aaaBCTCEAPlcyDtlsAsyncJob,
				bofaRejectionPaymentJob,
				aaaPaymentCentralRejectFeedAsyncJob,
				aaaRefundGenerationAsyncJob,
				aaaAgencyConsolidationBatchJob,
				bofaRecurringPaymentJob,
				offCycleBillingInvoiceAsyncJob,
				aaaBillingInvoiceAsyncTaskJob,
				preRenewalReminderGenerationAsyncJob,
				premiumReceivablesOnPolicyEffectiveJob,
				changeCancellationPendingPoliciesStatusJob,
				ledgerStatusUpdateJob,
				policyStatusUpdateJob,
				policyAutomatedRenewalAsyncTaskGenerationJob,
				isoRenewalBatchOrderJob,
				AAAIsoRenewAsyncBatchReceiveJob,
				aaaRenewalDataRefreshAsyncJob,
				renewalValidationAsyncTaskJob,
				renewalImageRatingAsyncTaskJob,
				renewalOfferAsyncTaskJob,
				lapsedRenewalProcessJob,
				policyExportToAgentDownloadsJob,
				aaaEscheatmentProcessAsyncJob,
				aaaGenerateEscheatmentReportJob,
				collectionFeedBatchOrderJob,
				earnedPremiumWriteoffProcessingJob,
				aaaRenewalNoticeBillAsyncJob,
				aaaPreRenewalNoticeAsyncJob,
				aaaGenerateLTRNoticeJob,
				aaaBillDueBatchJob,
				aaaDocGenBatchJob,
				dmvBatchJob,
				activityTimeoutJob,
				activityHistoryJob,
				premiumReceivablesOnPolicyEffectiveJob);
	}

	private static HashMap<String, String> getAaaBalanceTransferConversionAsyncJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", getFormattedFolderPath("%sBIL_E_EBPHUB_EXGBLL_7900_D/inbound"));
		parameters.put("processedFolder", getFormattedFolderPath("%sBIL_E_EBPHUB_EXGBLL_7900_D/inbound"));
		parameters.put("responseFolder", getFormattedFolderPath("%sBIL_E_EBPHUB_EXGBLL_7900_D/inbound"));
		return parameters;
	}

	private static HashMap<String, String> getAaaImportMiniPupPolicyAsyncTaskJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", "/ipb/import/Regression/FoxPro/import");
		parameters.put("processedFolder", "/ipb/import/Regression/FoxPro/processed");
		parameters.put("errorFolder", "/ipb/import/Regression/FoxPro/error");
		parameters.put("responseFolder", "/ipb/import/Regression/FoxPro/response");
		parameters.put("importFileFilter", ".*.xml");
		parameters.put("subFolderName", "");
		parameters.put("responseSubFolderName", "");
		return parameters;
	}

	private static HashMap<String, String> getAaaImportPolicyHomeCaHdesAsyncTaskJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", "/ipb/import/CaFunctional/import");
		parameters.put("processedFolder", "/ipb/import/CaFunctional/processed");
		parameters.put("errorFolder", "/ipb/import/CaFunctional/error");
		parameters.put("responseFolder", "/ipb/import/CaFunctional/response");
		parameters.put("importFileFilter", ".*.xml");
		parameters.put("subFolderName", "");
		parameters.put("responseSubFolderName", "");
		return parameters;
	}

	private static HashMap<String, String> getAaaImportPolicyHomeCaSisAsyncTaskJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", "/ipb/import/Regression/Sis/import");
		parameters.put("processedFolder", "/ipb/import/Regression/Sis/processed");
		parameters.put("errorFolder", "/ipb/import/Regression/Sis/error");
		parameters.put("responseFolder", "/ipb/import/Regression/Sis/response");
		parameters.put("importFileFilter", ".*.xml");
		parameters.put("subFolderName", "");
		parameters.put("responseSubFolderName", "");
		return parameters;
	}

	private static HashMap<String, String> getImportMiniPolicyAsyncTaskJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", "/ipb/import/Deloitte");
		parameters.put("processedFolder", "/ipb/import/Deloitte");
		parameters.put("responseFolder", "/ipb/result/Deloitte");
		parameters.put("importFileFilter", ".*");
		parameters.put("responseFileName", "{0}");
		parameters.put("responseSubFolderName", "");
		return parameters;
	}

	private static HashMap<String, String> getPolicyConversionImportAsyncTaskJobParameters() {
		HashMap<String, String> parameters = new HashMap();
		parameters.put("importFolder", "/ipb/import/Smoke");
		parameters.put("processedFolder", "/ipb/import/Smoke");
		parameters.put("responseFolder", "/ipb/result/Smoke");
		parameters.put("importFileFilter", ".*.xml");
		parameters.put("responseFileName", "{0}");
		parameters.put("responseSubFolderName", "{jobDate}");
		return parameters;
	}

	private static List<String> getFolders(HashMap<String, String> map) {
		List<String> folders = Arrays.asList(
				map.get("importFolder"),
				map.get("processedFolder"),
				map.get("responceFolder"));
		if (map.containsKey("errorFolder")) {
			folders.add(map.get("errorFolder"));
		}
		return folders;
	}

	private static List<String> getFormattedJobFolders(List<String> foldersTemplate) {
		List<String> result = new ArrayList<>();
		foldersTemplate.forEach(template -> result.add(getFormattedFolderPath(template)));
		return result;
	}

	private static String getFormattedFolderPath(String template) {
		return String.format(template, jobFolderPrefix);
	}
}
