package aaa.helpers.jobs;

import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Jobs {
	private static String jobFolderPrefix = PropertyProvider.getProperty(CsaaTestProperties.JOB_FOLDER, "/home/mp2/pas/sit/");

	private static final String CLAIM_ORDER_JOB_FOLDER_TEMPLATE = "%SPAS_B_EXGPAS_PASHUB_4001_D/outbound";

	private static final String CLAIM_RECEIVE_JOB_FOLDER_TEMPLATE = "%SPAS_B_PASHUB_EXGPAS_4001_D/inbound";

	private static ConcurrentHashMap<String, JobState> jobsState = new ConcurrentHashMap<>();

	public static Job renewalOfferGenerationPart1 = new Job("Renewal_Offer_Generation_Part1", getJobFoldersPath());

	private static List<String> getJobFoldersPath() {
		List<String> foldersTemplate = Arrays.asList(
				"%sPAS_B_EXGPAS_PASHUB_4004_D/inbound",
				"%sPAS_B_EXGPAS_PASHUB_4004_D/outbound",
				"%sPAS_B_EXGPAS_DMVFED_3051_D/inbound",
				"%sPAS_B_EXGPAS_DMVFED_3051_D/outbound",
				"%sPAS_B_EXGPAS_PASHUB_4001_D/inbound",
				"%sPAS_B_EXGPAS_PASHUB_4001_D/outbound");
		return getFormattedJobFolders(foldersTemplate);
	}

	private static List<String> getClaimOrderJobFolders() {
		return Collections.singletonList(getClaimOrderJobFolder());
	}

	public static String getClaimOrderJobFolder() {
		return getFormattedFolderPath(CLAIM_ORDER_JOB_FOLDER_TEMPLATE);
	}

	public static String getClaimReceiveJobFolder() {
		return getFormattedFolderPath(CLAIM_RECEIVE_JOB_FOLDER_TEMPLATE);
	}

	public static Job renewalOfferGenerationPart2 = new Job("Renewal_Offer_Generation_Part2");

	public static Job renewalJob = new Job("Renewal job");

	public static Job renewalOfferAsyncTaskJob = new Job("renewalOfferAsyncTaskJob");

	public static Job aaaBillingInvoiceAsyncTaskJob = new Job("aaaBillingInvoiceAsyncTaskJob");

	public static Job policyStatusUpdateJob = new Job("policyStatusUpdateJob");

	public static Job aaaRecurringPaymentsProcessingJob = new Job("aaaRecurringPaymentsProcessingJob");

	public static Job lapsedRenewalProcessJob = new Job("policyLapsedRenewalProcessAsyncJob");

	public static Job aaaCancellationNoticeAsyncJob = new Job("aaaCancellationNoticeAsyncJob");

	public static Job aaaCancellationConfirmationAsyncJob = new Job("aaaCancellationConfirmationAsyncJob");

	public static Job aaaRefundDisbursementAsyncJob = new Job("aaaRefundDisbursementAsyncJob");

	public static Job aaaRefundDisbursementRecieveInfoJob = new Job("aaaRefundsDisbursementReceiveInfoAsyncJob");

	public static Job earnedPremiumBillGenerationJob = new Job("earnedPremiumBillGenerationJob");

	public static Job offCycleBillingInvoiceAsyncJob = new Job("aaaOffCycleBillingInvoiceAsyncJob");

	public static Job collectionFeedBatch_earnedPremiumWriteOff = new Job("collectionFeedBatch_earnedPremiumWriteOff");

	public static Job earnedPremiumWriteoffProcessingJob = new Job("earnedPremiumWriteoffProcessingJob");

	public static Job aaaDocGenBatchJob = new Job("aaaDocGenBatchJob");

	public static Job aaaRemittanceFeedAsyncBatchReceiveJob = new Job("aaaRemittanceFeedAsyncBatchReceiveJob");

	public static Job aaaRefundGenerationAsyncJob = new Job("aaaRefundGenerationAsyncJob");

	public static Job refundGenerationJob = new Job("refundGenerationJob");

	// public static Job automatedProcessingInitiationJob = new Job("automatedProcessingInitiationJob"); BASE job not used anymore, aaaAutomatedProcessingInitiationJob is used instead

	public static Job automatedProcessingRunReportsServicesJob = new Job("automatedProcessingRunReportsServicesJob");

	public static Job automatedProcessingRatingJob = new Job("automatedProcessingRatingJob");

	public static Job automatedProcessingIssuingOrProposingJob = new Job("automatedProcessingIssuingOrProposingJob");

	public static Job aaaImportPolicyHomeCAHdesAsyncTaskJob = new Job("aaaImportPolicyHomeCAHdesAsyncTaskJob");

	public static Job aaaImportPolicyHomeCaSisAsyncTaskJob = new Job("aaaImportPolicyHomeCaSisAsyncTaskJob");

	public static Job aaaImportMiniPupPolicyAsyncTaskJob = new Job("aaaImportMiniPupPolicyAsyncTaskJob");

	public static Job importMiniPolicyAsyncTaskJob = new Job("ImportMiniPolicyAsyncTaskJob");

	public static Job aaaRenewalNoticeBillAsyncJob = new Job("aaaRenewalNoticeBillAsyncJob");

	public static Job changeCancellationPendingPoliciesStatus = new Job("changeCancellationPendingPoliciesStatus");

	public static Job aaaMembershipRenewalBatchOrderAsyncJob = new Job("aaaMembershipRenewalBatchOrderAsyncJob");

	public static Job aaaRenewalReminderGenerationAsyncJob = new Job("aaaRenewalReminderGenerationAsyncJob");

	public static Job aaaMortgageeRenewalReminderAndExpNoticeAsyncJob = new Job("aaaMortgageeRenewalReminderAndExpNoticeAsyncJob");

	public static Job aaaEscheatmentProcessAsyncJob = new Job("aaaEscheatmentProcessAsyncJob");

	public static Job doNotRenewJob = new Job("DoNotRenewJob");

	public static Job policyDoNotRenewAsyncJob = new Job("policyDoNotRenewAsyncJob");

	public static Job preRenewalReminderGenerationAsyncJob = new Job("preRenewalReminderGenerationAsyncJob");

	public static Job applyPendingTransactionAsyncjob = new Job("applypendingtransactionAsyncjob");

	public static Job aaaPreRenewalNoticeAsyncJob = new Job("aaaPreRenewalNoticeAsyncJob");

	public static Job aaaGenerateLTRNoticeJob = new Job("aaaGenerateLTRNoticeJob");

	public static Job earnedPremiumPostingAsyncTaskGenerationJob = new Job("earnedPremiumPostingAsyncTaskGenerationJob");

	public static Job policyTransactionLedgerJob = new Job("policyTransactionLedgerJob");

	public static Job policyTransactionLedgerJob_NonMonthly = new Job("policyTransactionLedgerJob_NonMonthly");

	public static Job ledgerStatusUpdateJob = new Job("ledgerStatusUpdateJob");

	public static Job cftDcsEodJob = new Job("cftDcsEodJob");

	public static Job aaaCreditDisclosureNoticeJob = new Job("aaaCreditDisclosureNoticeJob");

	public static Job aaaBatchMarkerJob = new Job("aaaBatchMarkerJob");

	public static Job aaaCollectionCancelDebtBatchJob = new Job("aaaCollectionCancelDebtBatchJob");

	public static Job aaaCollectionCancelDebtBatchAsyncJob = new Job("aaaCollectionCancelDebtBatchAsyncJob");

	public static Job collectionFeedBatchorderJob = new Job("collectionFeedBatchOrderJob");

	public static Job aaaAutomatedProcessingInitiationJob = new Job("aaaAutomatedProcessingInitiationJob");

	public static Job automatedProcessingStrategyStatusUpdateJob = new Job("automatedProcessingStrategyStatusUpdateJob");

	public static Job automatedProcessingBypassingAndErrorsReportGenerationJob = new Job("automatedProcessingBypassingAndErrorsReportGenerationJob");

	public static Job policyAutomatedRenewalAsyncTaskGenerationJob = new Job("policyAutomatedRenewalAsyncTaskGenerationJob");

	public static Job aaaMembershipRenewalBatchReceiveAsyncJob = new Job("aaaMembershipRenewalBatchReceiveAsyncJob");

	public static Job renewalImageRatingAsyncTaskJob = new Job("renewalImageRatingAsyncTaskJob");

	public static Job aaaRefundCancellationAsyncJob = new Job("aaaRefundCancellationAsyncJob");

	public static Job aaaRefundsDisbursementRejectionsAsyncJob = new Job("aaaRefundsDisbursementRejectionsAsyncJob");

	public static Job aaaCCardExpiryNoticeJob = new Job("aaaCCardExpiryNoticeJob");

    public static Job membershipValidationJob = new Job("membershipValidationJob");

	public static Job aaaPaymentCentralRejectFeedAsyncJob = new Job("aaaPaymentCentralRejectFeedAsyncJob");

	public static Job aaaRecurringPaymentsResponseProcessAsyncJob = new Job("aaaRecurringPaymentsResponseProcessAsyncJob");

	public static Job renewalClaimOrderAsyncJob = new Job("renewalClaimOrderAsyncJob", getClaimOrderJobFolders());

	public static Job renewalClaimReceiveAsyncJob = new Job("renewalClaimReceiveAsyncJob");

	public static Job renewalValidationAsyncTaskJob = new Job("renewalValidationAsyncTaskJob");

	public static Job isoRenewalBatchOrderJob = new Job("isoRenewalBatchOrderJob");

	public static Job aaaInsuranceScoreRenewBachOrder = new Job("aaaInsuranceScoreRenewBachOrder");

	public static Job aaaClueRenewBatchOrderAsyncJob = new Job("aaaClueRenewBatchOrderAsyncJob");

	public static Job aaaInsuranceScoreRenewalBatchReceiveJob = new Job("aaaInsuranceScoreRenewalBatchReceiveJob");

	public static Job aaaClueRenewAsyncBatchReceiveJob = new Job("aaaClueRenewAsyncBatchReceiveJob");

	public static Job aaaRenewalDataRefreshAsyncJob = new Job("aaaRenewalDataRefreshAsyncJob");

	public static Job policyBORTransferJob = new Job("policyBORTransferJob");

	public static Job bofaRecurringPaymentJob = new Job("bofaRecurringPaymentJob");

	public static Job premiumreceivablesonpolicyeffectivejob = new Job("premiumreceivablesonpolicyeffectivejob");

	public static Job aaaDelayTriggerTOINoticeAsyncJob = new Job("aaaDelayTriggerTOINoticeAsyncJob");

	public static Job policyLapsedRenewalProcessAsyncJob = new Job("policyLapsedRenewalProcessAsyncJob");

	public static Job aaaDataUpdateJob = new Job("aaaDataUpdateJob");

	public static Job activityTimeoutJob = new Job("activityTimeoutJob");

	public static Job activityHistoryJob = new Job("activityHistoryJob");

	public static Job activitySummarizationJob = new Job("activitySummarizationJob");

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

	private static List<String> getFormattedJobFolders(List<String> foldersTemplate) {
		List<String> result = new ArrayList<>();
		foldersTemplate.forEach(template -> result.add(getFormattedFolderPath(template)));
		return result;
	}

	private static String getFormattedFolderPath(String template) {
		return String.format(template, jobFolderPrefix);
	}

}
