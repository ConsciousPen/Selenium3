package aaa.modules.bct.batch;

import static aaa.helpers.jobs.BatchJob.*;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class BatchTestParallel extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(BatchTestParallel.class);

	/**
	 * Scheme of dependencies can be found by link below
	 * https://csaaig.atlassian.net/wiki/spaces/PAS/pages/1113391525/BCT+Batch+Jobs+runbook
	 */

	@Test(groups = {queue1}, priority = 1)
	public void queue1_aaaBatchMarkerJob() {
		executeBatchTest(aaaBatchMarkerJob);
	}

	@Test(groups = {queue2}, dependsOnGroups = {queue1}, alwaysRun = true, priority = 2)
	public void queue2_aaaMvrRenewBatchOrderAsyncJob() {
		executeBatchTest(aaaMvrRenewBatchOrderAsyncJob);
	}

	@Test(groups = {queue2}, dependsOnGroups = {queue1}, alwaysRun = true, priority = 2)
	public void queue2_renewalClaimOrderAsyncJob() {
		executeBatchTest(renewalClaimOrderAsyncJob);
	}

	@Test(groups = {queue2}, dependsOnGroups = {queue1}, alwaysRun = true, priority = 2)
	public void queue2_aaaMembershipRenewalBatchOrderAsyncJob() {
		executeBatchTest(aaaMembershipRenewalBatchOrderAsyncJob);
	}

	@Test(groups = {queue3}, dependsOnGroups = {queue2}, alwaysRun = true, priority = 3)
	public void queue3_policyStatusUpdateJob() {
		executeBatchTest(policyStatusUpdateJob);
	}

	@Test(groups = {queue4}, dependsOnGroups = {queue3}, alwaysRun = true, priority = 4)
	public void queue4_aaaPolicyAutomatedRenewalAsyncTaskGenerationJob() {
		executeBatchTest(aaaPolicyAutomatedRenewalAsyncTaskGenerationJob.setJobParameters(Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Test(groups = {queue4}, dependsOnGroups = {queue3}, alwaysRun = true, priority = 4)
	public void queue4_aaaRemittanceFeedAsyncBatchReceiveJob() {
		executeBatchTest(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Test(groups = {queue4}, dependsOnGroups = {queue3}, alwaysRun = true, priority = 4)
	public void queue4_aaaRecurringPaymentsAsyncProcessJob() {
		executeBatchTest(aaaRecurringPaymentsAsyncProcessJob);
	}

	@Test(groups = {queue4}, dependsOnGroups = {queue3}, alwaysRun = true, priority = 4)
	public void queue4_aaaAutomatedProcessingInitiationJob() {
		executeBatchTest(aaaAutomatedProcessingInitiationJob);
	}

	@Test(groups = {queue5}, dependsOnGroups = {queue4}, alwaysRun = true, priority = 5)
	public void queue5_renewalValidationAsyncTaskJob() {
		executeBatchTest(renewalValidationAsyncTaskJob);
	}

	@Test(groups = {queue5}, dependsOnGroups = {queue4}, alwaysRun = true, priority = 5)
	public void queue5_bofaRecurringPaymentJob() {
		executeBatchTest(bofaRecurringPaymentJob);
	}

	@Test(groups = {queue5}, dependsOnGroups = {queue4}, alwaysRun = true, priority = 5)
	public void queue5_automatedProcessingRunReportsServicesJob() {
		executeBatchTest(automatedProcessingRunReportsServicesJob);
	}

	@Test(groups = {queue6}, dependsOnGroups = {queue5}, alwaysRun = true, priority = 6)
	public void queue6_aaaRenewalDataRefreshAsyncJob() {
		executeBatchTest(aaaRenewalDataRefreshAsyncJob);
	}

	@Test(groups = {queue6}, dependsOnGroups = {queue5}, alwaysRun = true, priority = 6)
	public void queue6_automatedProcessingRatingJob() {
		executeBatchTest(automatedProcessingRatingJob);
	}

	@Test(groups = {queue6}, dependsOnGroups = {queue5}, alwaysRun = true, priority = 6)
	public void queue6_premiumReceivablesOnPolicyEffectiveJob() {
		executeBatchTest(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_aaaPreRenewalNoticeAsyncJob() {
		executeBatchTest(aaaPreRenewalNoticeAsyncJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_automatedProcessingIssuingOrProposingJob() {
		executeBatchTest(automatedProcessingIssuingOrProposingJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_changeCancellationPendingPoliciesStatusJob() {
		executeBatchTest(changeCancellationPendingPoliciesStatusJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_aaaCancellationNoticeAsyncJob() {
		executeBatchTest(aaaCancellationNoticeAsyncJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_aaaCancellationConfirmationAsyncJob() {
		executeBatchTest(aaaCancellationConfirmationAsyncJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_activityTimeoutJob() {
		executeBatchTest(activityTimeoutJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_activityHistoryJob() {
		executeBatchTest(activityHistoryJob);
	}

	@Test(groups = {queue7}, dependsOnGroups = {queue6}, alwaysRun = true, priority = 7)
	public void queue7_activitySummarizationJob() {
		executeBatchTest(activitySummarizationJob);
	}

	@Test(groups = {queue8}, dependsOnGroups = {queue7}, alwaysRun = true, priority = 8)
	public void queue8_renewalImageRatingAsyncTaskJob() {
		executeBatchTest(renewalImageRatingAsyncTaskJob);
	}

	@Test(groups = {queue8}, dependsOnGroups = {queue7}, alwaysRun = true, priority = 8)
	public void queue8_automatedProcessingStrategyStatusUpdateJob() {
		executeBatchTest(automatedProcessingStrategyStatusUpdateJob);
	}

	@Test(groups = {queue8}, dependsOnGroups = {queue7}, alwaysRun = true, priority = 8)
	public void queue8_aaaCollectionCancellDebtBatchAsyncJob() {
		executeBatchTest(aaaCollectionCancellDebtBatchAsyncJob);
	}

	@Test(groups = {queue9}, dependsOnGroups = {queue8}, alwaysRun = true, priority = 9)
	public void queue9_automatedProcessingBypassingAndErrorsReportGenerationJob() {
		executeBatchTest(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Test(groups = {queue9}, dependsOnGroups = {queue8}, alwaysRun = true, priority = 9)
	public void queue9_collectionFeedBatchOrderJob() {
		executeBatchTest(collectionFeedBatchOrderJob);
	}

	@Test(groups = {queue10}, dependsOnGroups = {queue9}, alwaysRun = true, priority = 10)
	public void queue10_earnedPremiumWriteoffProcessingJob() {
		executeBatchTest(earnedPremiumWriteoffProcessingJob);
	}

	@Test(groups = {queue11}, dependsOnGroups = {queue10}, alwaysRun = true, priority = 11)
	public void queue11_aaaBillingInvoiceAsyncTaskJob() {
		executeBatchTest(aaaBillingInvoiceAsyncTaskJob);
	}

	@Test(groups = {queue11}, dependsOnGroups = {queue10}, alwaysRun = true, priority = 11)
	public void queue11_aaaOffCycleBillingInvoiceAsyncJob() {
		executeBatchTest(offCycleBillingInvoiceAsyncJob);
	}

	@Test(groups = {queue12}, dependsOnGroups = {queue11}, alwaysRun = true, priority = 12)
	public void queue12_aaaRefundGenerationAsyncJob() {
		executeBatchTest(aaaRefundGenerationAsyncJob);
	}

	@Test(groups = {queue13}, dependsOnGroups = {queue12}, alwaysRun = true, priority = 13)
	public void queue13_ledgerStatusUpdateJob() {
		executeBatchTest(ledgerStatusUpdateJob);
	}

	@Test(groups = {queue13}, dependsOnGroups = {queue12}, alwaysRun = true, priority = 13)
	public void queue13_preRenewalReminderGenerationAsyncJob() {
		executeBatchTest(preRenewalReminderGenerationAsyncJob);
	}

	@Test(groups = {queue13}, dependsOnGroups = {queue12}, alwaysRun = true, priority = 13)
	public void queue13_aaaRenewalNoticeBillAsyncJob() {
		executeBatchTest(aaaRenewalNoticeBillAsyncJob);
	}

	@Test(groups = {queue13}, dependsOnGroups = {queue12}, alwaysRun = true, priority = 13)
	public void queue13_aaaMortgageeRenewalReminderAndExpNoticeAsyncJob() {
		executeBatchTest(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Test(groups = {queue14}, dependsOnGroups = {queue13}, alwaysRun = true, priority = 14)
	public void queue14_aaaRefundDisbursementAsyncJob() {
		executeBatchTest(aaaRefundDisbursementAsyncJob);
	}

	@Test(groups = {queue14}, dependsOnGroups = {queue13}, alwaysRun = true, priority = 14)
	public void queue14_aaaDelayTriggerTOINoticeAsyncJob() {
		executeBatchTest(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Test(groups = {queue14}, dependsOnGroups = {queue13}, alwaysRun = true, priority = 14)
	public void queue14_policyDoNotRenewAsyncJob() {
		executeBatchTest(policyDoNotRenewAsyncJob);
	}

	@Test(groups = {queue14}, dependsOnGroups = {queue13}, alwaysRun = true, priority = 14)
	public void queue14_renewalOfferAsyncTaskJob() {
		executeBatchTest(renewalOfferAsyncTaskJob);
	}

	@Test(groups = {queue15}, dependsOnGroups = {queue14}, alwaysRun = true, priority = 15)
	public void queue15_policyLapsedRenewalProcessAsyncJob() {
		executeBatchTest(policyLapsedRenewalProcessAsyncJob);
	}

	@Test(groups = {queue16}, dependsOnGroups = {queue15}, alwaysRun = true, priority = 16)
	public void queue16_aaaRenewalReminderGenerationAsyncJob() {
		executeBatchTest(aaaRenewalReminderGenerationAsyncJob);
	}

	@Test(groups = {queue17}, dependsOnGroups = {queue16}, alwaysRun = true, priority = 17)
	public void queue17_aaaDataUpdateJob() {
		executeBatchTest(aaaDataUpdateJob);
	}

	@Test(groups = {queue17}, dependsOnGroups = {queue16}, alwaysRun = true, priority = 17)
	public void queue17_aaaEscheatmentProcessAsyncJob() {
		executeBatchTest(aaaEscheatmentProcessAsyncJob);
	}

	@Test(groups = {queue18}, dependsOnGroups = {queue17}, alwaysRun = true, priority = 17)
	public void queue18_aaaGenerateEscheatmentReportJob() {
		executeBatchTest(aaaGenerateEscheatmentReportJob);
	}

	@Test(groups = {queue19}, dependsOnGroups = {queue18}, alwaysRun = true, priority = 18)
	public void queue19_policyTransactionLedgerJob() {
		executeBatchTest(policyTransactionLedgerJob);
	}

	private static final String queue1 = "1";
	private static final String queue2 = "2";
	private static final String queue3 = "3";
	private static final String queue4 = "4";
	private static final String queue5 = "5";
	private static final String queue6 = "6";
	private static final String queue7 = "7";
	private static final String queue8 = "8";
	private static final String queue9 = "9";
	private static final String queue10 = "10";
	private static final String queue11 = "11";
	private static final String queue12 = "12";
	private static final String queue13 = "13";
	private static final String queue14 = "14";
	private static final String queue15 = "15";
	private static final String queue16 = "16";
	private static final String queue17 = "17";
	private static final String queue18 = "18";
	private static final String queue19 = "19";
}
