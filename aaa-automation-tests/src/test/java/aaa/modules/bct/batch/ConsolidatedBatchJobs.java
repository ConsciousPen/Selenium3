package aaa.modules.bct.batch;

import static aaa.helpers.jobs.BatchJob.*;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class ConsolidatedBatchJobs extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(ConsolidatedBatchJobs.class);

	@Parameters({"state"})
	@Test(groups = Groups.PRECONDITION)
	public void aaaBatchMarkerJob(@Optional("") String state) {
		executeBatchTest(aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test()
	public void renewalClaimOrderAsyncJob(@Optional("") String state) {
		executeBatchTest(renewalClaimOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaMvrRenewBatchOrderAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaMvrRenewBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaMembershipRenewalBatchOrderAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaMembershipRenewalBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyStatusUpdateJob(@Optional("") String state) {
		executeBatchTest(policyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(@Optional("") String state) {
		executeBatchTest(aaaPolicyAutomatedRenewalAsyncTaskGenerationJob.setJobParameters(Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Parameters({"state"})
	@Test()
	public void renewalValidationAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(renewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void renewalImageRatingAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(renewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRemittanceFeedAsyncBatchReceiveJob(@Optional("") String state) {
		executeBatchTest(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRecurringPaymentsProcessingJob(@Optional("") String state) {
		executeBatchTest(aaaRecurringPaymentsAsyncProcessJob);
	}

	@Parameters({"state"})
	@Test()
	public void bofaRecurringPaymentJob(@Optional("") String state) {
		executeBatchTest(bofaRecurringPaymentJob);
	}

	@Parameters({"state"})
	@Test()
	public void premiumReceivablesOnPolicyEffectiveJob(@Optional("") String state) {
		executeBatchTest(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void changeCancellationPendingPoliciesStatus(@Optional("") String state) {
		executeBatchTest(changeCancellationPendingPoliciesStatusJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaCancellationNoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaCancellationNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaCancellationConfirmationAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaCancellationConfirmationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaCollectionCancellDebtBatchAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaCollectionCancellDebtBatchAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void collectionFeedBatchorderJob(@Optional("") String state) {
		executeBatchTest(collectionFeedBatchOrderJob);
	}

	@Parameters({"state"})
	@Test()
	public void earnedPremiumWriteoffProcessingJob(@Optional("") String state) {
		executeBatchTest(earnedPremiumWriteoffProcessingJob);
	}

	@Parameters({"state"})
	@Test()
	public void offCycleBillingInvoiceAsyncJob(@Optional("") String state) {
		executeBatchTest(offCycleBillingInvoiceAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaBillingInvoiceAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(aaaBillingInvoiceAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyDoNotRenewAsyncJob(@Optional("") String state) {
		executeBatchTest(policyDoNotRenewAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRefundGenerationAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRefundGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void preRenewalReminderGenerationAsyncJob(@Optional("") String state) {
		executeBatchTest(preRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRenewalNoticeBillAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRenewalDataRefreshAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRenewalDataRefreshAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaPreRenewalNoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaPreRenewalNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void renewalOfferAsyncTaskJob(@Optional("") String state) {
		executeBatchTest(renewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaDelayTriggerTOINoticeAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyLapsedRenewalProcessAsyncJob(@Optional("") String state) {
		executeBatchTest(policyLapsedRenewalProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRenewalReminderGenerationAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyTransactionLedgerJob(@Optional("") String state) {
		executeBatchTest(policyTransactionLedgerJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaDataUpdateJob(@Optional("") String state) {
		executeBatchTest(aaaDataUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void activityTimeoutJob(@Optional("") String state) {
		executeBatchTest(activityTimeoutJob);
	}

	@Parameters({"state"})
	@Test()
	public void activityHistoryJob(@Optional("") String state) {
		executeBatchTest(activityHistoryJob);
	}

	@Parameters({"state"})
	@Test()
	public void activitySummarizationJob(@Optional("") String state) {
		executeBatchTest(activitySummarizationJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaAutomatedProcessingInitiationJob(@Optional("") String state) {
		executeBatchTest(aaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingRunReportsServicesJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingRunReportsServicesJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingRatingJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingIssuingOrProposingJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingIssuingOrProposingJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingStrategyStatusUpdateJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingStrategyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingBypassingAndErrorsReportGenerationJob(@Optional("") String state) {
		executeBatchTest(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Parameters({"state"})
	@Test()
	public void ledgerStatusUpdateJob(@Optional("") String state) {
		executeBatchTest(ledgerStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRefundDisbursementAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaRefundDisbursementAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaEscheatmentProcessAsyncJob(@Optional("") String state) {
		executeBatchTest(aaaEscheatmentProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaGenerateEscheatmentReportJob(@Optional("") String state) {
		executeBatchTest(aaaGenerateEscheatmentReportJob);
	}
}