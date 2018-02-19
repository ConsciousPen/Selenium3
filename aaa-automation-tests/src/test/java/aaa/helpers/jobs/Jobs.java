package aaa.helpers.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import aaa.helpers.config.CustomTestProperties;
import toolkit.config.PropertyProvider;

public class Jobs {
	private static String jobFolderPrefix = PropertyProvider.getProperty(CustomTestProperties.JOB_FOLDER, "/home/mp2/pas/sit/");

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

		List<String> result = new ArrayList<>();

		foldersTemplate.forEach(template -> result.add(String.format(template, jobFolderPrefix)));

		return result;
	}

	public static Job renewalOfferGenerationPart2 = new Job("Renewal_Offer_Generation_Part2");

	public static Job renewalJob = new Job("Renewal job");

	public static Job renewalOfferGenerationJob = new Job("renewalOfferAsyncTaskJob");

	public static Job billingInvoiceAsyncTaskJob = new Job("aaaBillingInvoiceAsyncTaskJob");

	public static Job policyStatusUpdateJob = new Job("policyStatusUpdateJob");

	public static Job recurringPaymentsJob = new Job("aaaRecurringPaymentsProcessingJob");

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

	public static Job remittanceFeedBatchReceiveJob = new Job("remittanceFeedBatchReceiveJob");

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

	public static Job preRenewalReminderGenerationAsyncJob = new Job("preRenewalReminderGenerationAsyncJob");

	public static Job applyPendingTransactionAsyncjob = new Job("applypendingtransactionAsyncjob");

	public static Job aaaPreRenewalNoticeAsyncJob = new Job("aaaPreRenewalNoticeAsyncJob");

	public static Job aaaGenerateLTRNoticeJob = new Job("aaaGenerateLTRNoticeJob");

	public static Job earnedPremiumPostingAsyncTaskGenerationJob = new Job("earnedPremiumPostingAsyncTaskGenerationJob");

	public static Job policyTransactionLedgerJob = new Job("policyTransactionLedgerJob");

	public static Job policyTransactionLedgerJob_NonMonthly = new Job("policyTransactionLedgerJob_NonMonthly");

	public static Job cftDcsEodJob = new Job("cftDcsEodJob");

	public static Job aaaCreditDisclosureNoticeJob = new Job("aaaCreditDisclosureNoticeJob");

	public static Job aaaBatchMarkerJob = new Job("aaaBatchMarkerJob");

	public static Job aaaCollectionCancelDebtBatchJob = new Job("aaaCollectionCancelDebtBatchJob");

	public static Job aaaAutomatedProcessingInitiationJob = new Job("aaaAutomatedProcessingInitiationJob");

	public static Job automatedProcessingStrategyStatusUpdateJob = new Job("automatedProcessingStrategyStatusUpdateJob");

	public static Job automatedProcessingBypassingAndErrorsReportGenerationJob = new Job("automatedProcessingBypassingAndErrorsReportGenerationJob");

	public static Job policyAutomatedRenewalAsyncTaskGenerationJob = new Job("policyAutomatedRenewalAsyncTaskGenerationJob");

	public static Job aaaMembershipRenewalBatchReceiveAsyncJob = new Job("aaaMembershipRenewalBatchReceiveAsyncJob");

	public static Job renewalImageRatingAsyncTaskJob = new Job("renewalImageRatingAsyncTaskJob");

	public static Job aaaRefundCancellationAsyncJob = new Job("aaaRefundCancellationAsyncJob");

	public static Job aaaRefundsDisbursementRejectionsAsyncJob = new Job("aaaRefundsDisbursementRejectionsAsyncJob");

	public static Job aaaCCardExpiryNoticeJob = new Job("aaaCCardExpiryNoticeJob");

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
