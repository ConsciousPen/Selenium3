package aaa.modules.bct.batch;

import static aaa.helpers.jobs.BatchJob.*;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class BatchTestParallel extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(BatchTestParallel.class);

	/**
	 * Scheme of dependencies can be found by link below
	 * https://csaaig.atlassian.net/wiki/spaces/PAS/pages/1113391525/BCT+Batch+Jobs+runbook
	 */
	@Parameters({"state"})
	@Test(groups = {"queue1_aaaBatchMarkerJob"}, alwaysRun = true)
	public void queue1_aaaBatchMarkerJob(@Optional("") String state) {
		executeBatchTest(aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue2_aaaMvrRenewBatchOrderAsyncJob"}, dependsOnMethods = "queue1_aaaBatchMarkerJob", alwaysRun = true)
	public void queue2_aaaMvrRenewBatchOrderAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaMvrRenewBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue2_renewalClaimOrderAsyncJob"}, dependsOnMethods = "queue1_aaaBatchMarkerJob", alwaysRun = true)
	public void queue2_renewalClaimOrderAsyncJob(@Optional("") String state) {
		executeBatchTest(renewalClaimOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue2_aaaMembershipRenewalBatchOrderAsyncJob"}, dependsOnMethods = "queue1_aaaBatchMarkerJob", alwaysRun = true)
	public void queue2_aaaMembershipRenewalBatchOrderAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaMembershipRenewalBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue3_policyStatusUpdateJob"}, dependsOnMethods = "queue2_aaaMembershipRenewalBatchOrderAsyncJob", alwaysRun = true)
	public void queue3_policyStatusUpdateJob(@Optional("") String state) {
		executeBatchTest(policyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue4_aaaPolicyAutomatedRenewalAsyncTaskGenerationJob"}, dependsOnMethods = "queue3_policyStatusUpdateJob", alwaysRun = true)
	public void queue4_aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(@Optional("") String state) {
		executeBatchTest(aaaPolicyAutomatedRenewalAsyncTaskGenerationJob.setJobParameters(Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Parameters({"state"})
	@Test(groups = {"queue5_renewalValidationAsyncTaskJob"}, dependsOnMethods = "queue4_aaaPolicyAutomatedRenewalAsyncTaskGenerationJob", alwaysRun = true)
	public void queue5_renewalValidationAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(renewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue6_aaaRenewalDataRefreshAsyncJob"}, dependsOnMethods = "queue5_renewalValidationAsyncTaskJob", alwaysRun = true)
	public void queue6_aaaRenewalDataRefreshAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRenewalDataRefreshAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_aaaPreRenewalNoticeAsyncJob"}, dependsOnMethods = "queue6_aaaRenewalDataRefreshAsyncJob", alwaysRun = true)
	public void queue7_aaaPreRenewalNoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaPreRenewalNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue8_renewalImageRatingAsyncTaskJob"}, dependsOnMethods = "queue7_aaaPreRenewalNoticeAsyncJob", alwaysRun = true)
	public void queue8_renewalImageRatingAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(renewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue4_aaaRemittanceFeedAsyncBatchReceiveJob"}, dependsOnMethods = "queue3_policyStatusUpdateJob", alwaysRun = true)
	public void queue4_aaaRemittanceFeedAsyncBatchReceiveJob(@Optional("") String state) {
		executeBatchTest(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue4_aaaRecurringPaymentsAsyncProcessJob"}, dependsOnMethods = "queue3_policyStatusUpdateJob", alwaysRun = true)
	public void queue4_aaaRecurringPaymentsAsyncProcessJob(@Optional("") String state) {
		executeBatchTest(aaaRecurringPaymentsAsyncProcessJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue4_aaaAutomatedProcessingInitiationJob"}, dependsOnMethods = "queue3_policyStatusUpdateJob", alwaysRun = true)
	public void queue4_aaaAutomatedProcessingInitiationJob(@Optional("") String state) {
		executeBatchTest(aaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue5_automatedProcessingRunReportsServicesJob"}, dependsOnMethods = "queue4_aaaAutomatedProcessingInitiationJob", alwaysRun = true)
	public void queue5_automatedProcessingRunReportsServicesJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingRunReportsServicesJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue6_automatedProcessingRatingJob"}, dependsOnMethods = "queue5_automatedProcessingRunReportsServicesJob", alwaysRun = true)
	public void queue6_automatedProcessingRatingJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_automatedProcessingIssuingOrProposingJob"}, dependsOnMethods = "queue6_automatedProcessingRatingJob", alwaysRun = true)
	public void queue7_automatedProcessingIssuingOrProposingJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingIssuingOrProposingJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue8_automatedProcessingStrategyStatusUpdateJob"}, dependsOnMethods = "queue7_automatedProcessingIssuingOrProposingJob", alwaysRun = true)
	public void queue8_automatedProcessingStrategyStatusUpdateJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingStrategyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue9_automatedProcessingBypassingAndErrorsReportGenerationJob"}, dependsOnMethods = "queue8_automatedProcessingStrategyStatusUpdateJob", alwaysRun = true)
	public void queue9_automatedProcessingBypassingAndErrorsReportGenerationJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue5_bofaRecurringPaymentJob"}, dependsOnMethods = "queue4_aaaRecurringPaymentsAsyncProcessJob", alwaysRun = true)
	public void queue5_bofaRecurringPaymentJob(@Optional("") String state) {
		executeBatchTest(bofaRecurringPaymentJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue6_premiumReceivablesOnPolicyEffectiveJob"}, dependsOnMethods = "queue5_bofaRecurringPaymentJob", alwaysRun = true)
	public void queue6_premiumReceivablesOnPolicyEffectiveJob(@Optional("") String state) {
		executeBatchTest(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_changeCancellationPendingPoliciesStatusJob"}, dependsOnMethods = "queue6_premiumReceivablesOnPolicyEffectiveJob", alwaysRun = true)
	public void queue7_changeCancellationPendingPoliciesStatusJob(@Optional("") String state) {
		executeBatchTest(changeCancellationPendingPoliciesStatusJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_aaaCancellationNoticeAsyncJob"}, dependsOnMethods = "queue6_premiumReceivablesOnPolicyEffectiveJob", alwaysRun = true)
	public void queue7_aaaCancellationNoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(new Job("aaaCancellationNoticeAsyncJob",Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_aaaCancellationConfirmationAsyncJob"}, dependsOnMethods = "queue6_premiumReceivablesOnPolicyEffectiveJob", alwaysRun = true)
	public void queue7_aaaCancellationConfirmationAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaCancellationConfirmationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_activityTimeoutJob"}, dependsOnMethods = "queue6_premiumReceivablesOnPolicyEffectiveJob", alwaysRun = true)
	public void queue7_activityTimeoutJob(@Optional("") String state) {
		executeBatchTest(activityTimeoutJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_activityHistoryJob"}, dependsOnMethods = "queue6_premiumReceivablesOnPolicyEffectiveJob", alwaysRun = true)
	public void queue7_activityHistoryJob(@Optional("") String state) {
		executeBatchTest(activityHistoryJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue7_activitySummarizationJob"}, dependsOnMethods = "queue6_premiumReceivablesOnPolicyEffectiveJob", alwaysRun = true)
	public void queue7_activitySummarizationJob(@Optional("") String state) {
		executeBatchTest(activitySummarizationJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue8_aaaCollectionCancellDebtBatchAsyncJob"}, dependsOnMethods = "queue7_aaaCancellationConfirmationAsyncJob", alwaysRun = true)
	public void queue8_aaaCollectionCancellDebtBatchAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaCollectionCancellDebtBatchAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue9_collectionFeedBatchOrderJob"}, dependsOnMethods = "queue8_aaaCollectionCancellDebtBatchAsyncJob", alwaysRun = true)
	public void queue9_collectionFeedBatchOrderJob(@Optional("") String state) {
		executeBatchTest(collectionFeedBatchOrderJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue10_earnedPremiumWriteoffProcessingJob"}, dependsOnMethods = "queue9_collectionFeedBatchOrderJob", alwaysRun = true)
	public void queue10_earnedPremiumWriteoffProcessingJob(@Optional("") String state) {
		executeBatchTest(earnedPremiumWriteoffProcessingJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue11_aaaOffCycleBillingInvoiceAsyncJob"}, dependsOnMethods = "queue10_earnedPremiumWriteoffProcessingJob", alwaysRun = true)
	public void queue11_aaaOffCycleBillingInvoiceAsyncJob(@Optional("") String state) {
		executeBatchTest(offCycleBillingInvoiceAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue11_aaaBillingInvoiceAsyncTaskJob"}, dependsOnMethods = "queue10_earnedPremiumWriteoffProcessingJob", alwaysRun = true)
	public void queue11_aaaBillingInvoiceAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(aaaBillingInvoiceAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue12_aaaRefundGenerationAsyncJob"}, dependsOnMethods = "queue11_aaaBillingInvoiceAsyncTaskJob", alwaysRun = true)
	public void queue12_aaaRefundGenerationAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRefundGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue13_ledgerStatusUpdateJob"}, dependsOnMethods = "queue12_aaaRefundGenerationAsyncJob", alwaysRun = true)
	public void queue13_ledgerStatusUpdateJob(@Optional("") String state) {
		executeBatchTest(ledgerStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue14_aaaRefundDisbursementAsyncJob"}, dependsOnMethods = "queue13_ledgerStatusUpdateJob", alwaysRun = true)
	public void queue14_aaaRefundDisbursementAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRefundDisbursementAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue13_preRenewalReminderGenerationAsyncJob"}, dependsOnMethods = "queue12_aaaRefundGenerationAsyncJob", alwaysRun = true)
	public void queue13_preRenewalReminderGenerationAsyncJob(@Optional("") String state) {
		executeBatchTest(preRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue13_aaaRenewalNoticeBillAsyncJob"}, dependsOnMethods = "queue12_aaaRefundGenerationAsyncJob", alwaysRun = true)
	public void queue13_aaaRenewalNoticeBillAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue13_aaaMortgageeRenewalReminderAndExpNoticeAsyncJob"}, dependsOnMethods = "queue12_aaaRefundGenerationAsyncJob", alwaysRun = true)
	public void queue13_aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue14_aaaDelayTriggerTOINoticeAsyncJob"}, dependsOnMethods = "queue13_aaaMortgageeRenewalReminderAndExpNoticeAsyncJob", alwaysRun = true)
	public void queue14_aaaDelayTriggerTOINoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue14_policyDoNotRenewAsyncJob"}, dependsOnMethods = "queue13_aaaMortgageeRenewalReminderAndExpNoticeAsyncJob", alwaysRun = true)
	public void queue14_policyDoNotRenewAsyncJob(@Optional("") String state) {
		executeBatchTest(policyDoNotRenewAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue14_renewalOfferAsyncTaskJob"}, dependsOnMethods = "queue13_aaaMortgageeRenewalReminderAndExpNoticeAsyncJob", alwaysRun = true)
	public void queue14_renewalOfferAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(renewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue15_policyLapsedRenewalProcessAsyncJob"}, dependsOnMethods = "queue14_renewalOfferAsyncTaskJob", alwaysRun = true)
	public void queue15_policyLapsedRenewalProcessAsyncJob(@Optional("") String state) {
		executeBatchTest(policyLapsedRenewalProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue16_aaaRenewalReminderGenerationAsyncJob"}, dependsOnMethods = "queue15_policyLapsedRenewalProcessAsyncJob", alwaysRun = true)
	public void queue16_aaaRenewalReminderGenerationAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue17_aaaDataUpdateJob"}, dependsOnMethods = "queue16_aaaRenewalReminderGenerationAsyncJob", alwaysRun = true)
	public void queue17_aaaDataUpdateJob(@Optional("") String state) {
		executeBatchTest(aaaDataUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue17_aaaEscheatmentProcessAsyncJob"}, dependsOnMethods = "queue16_aaaRenewalReminderGenerationAsyncJob", alwaysRun = true)
	public void queue17_aaaEscheatmentProcessAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaEscheatmentProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"queue18_aaaGenerateEscheatmentReportJob"}, dependsOnMethods = "queue17_aaaEscheatmentProcessAsyncJob", alwaysRun = true)
	public void queue18_aaaGenerateEscheatmentReportJob(@Optional("") String state) {
		executeBatchTest(aaaGenerateEscheatmentReportJob);
	}


	@Parameters({"state"})
	@Test(groups = {"queue19_policyTransactionLedgerJob"}, dependsOnMethods = "queue18_aaaGenerateEscheatmentReportJob", alwaysRun = true)
	public void queue19_policyTransactionLedgerJob(@Optional("") String state) {
		executeBatchTest(policyTransactionLedgerJob);
	}

}
