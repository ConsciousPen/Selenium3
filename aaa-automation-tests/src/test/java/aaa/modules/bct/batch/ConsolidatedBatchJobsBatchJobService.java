package aaa.modules.bct.batch;

import static aaa.helpers.jobs.BatchJob.*;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class ConsolidatedBatchJobsBatchJobService extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(ConsolidatedBatchJobsBatchJobService.class);

	@Parameters({"state"})
	@Test(groups = Groups.PRECONDITION)
	public void aaaBatchMarkerJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test()
	public void renewalClaimOrderAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(renewalClaimOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaMvrRenewBatchOrderAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaMvrRenewBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaMembershipRenewalBatchOrderAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaMembershipRenewalBatchOrderAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyStatusUpdateJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(policyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaPolicyAutomatedRenewalAsyncTaskGenerationJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(new Job("aaaPolicyAutomatedRenewalAsyncTaskGenerationJob", Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Parameters({"state"})
	@Test()
	public void renewalValidationAsyncTaskJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(renewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void renewalImageRatingAsyncTaskJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(renewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRemittanceFeedAsyncBatchReceiveJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRecurringPaymentsProcessingJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(new Job("aaaRecurringPaymentsAsyncProcessJob"));
	}

	@Parameters({"state"})
	@Test()
	public void bofaRecurringPaymentJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(bofaRecurringPaymentJob);
	}

	@Parameters({"state"})
	@Test()
	public void premiumReceivablesOnPolicyEffectiveJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void changeCancellationPendingPoliciesStatus(@Optional("") String state) {
		executeBatchUsingBatchJobService(new Job("changeCancellationPendingPoliciesStatusJob"));
	}

	@Parameters({"state"})
	@Test()
	public void aaaCancellationNoticeAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaCancellationNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaCancellationConfirmationAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaCancellationConfirmationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaCollectionCancellDebtBatchAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(new Job("aaaCollectionCancellDebtBatchAsyncJob"));
	}

	@Parameters({"state"})
	@Test()
	public void collectionFeedBatchorderJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(collectionFeedBatchOrderJob);
	}

	@Parameters({"state"})
	@Test()
	public void earnedPremiumWriteoffProcessingJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(earnedPremiumWriteoffProcessingJob);
	}

	@Parameters({"state"})
	@Test()
	public void offCycleBillingInvoiceAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(offCycleBillingInvoiceAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaBillingInvoiceAsyncTaskJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaBillingInvoiceAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyDoNotRenewAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(policyDoNotRenewAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRefundGenerationAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaRefundGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void preRenewalReminderGenerationAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(preRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRenewalNoticeBillAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRenewalDataRefreshAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaRenewalDataRefreshAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaPreRenewalNoticeAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaPreRenewalNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaMortgageeRenewalReminderAndExpNoticeAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void renewalOfferAsyncTaskJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(renewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaDelayTriggerTOINoticeAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyLapsedRenewalProcessAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(policyLapsedRenewalProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRenewalReminderGenerationAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void policyTransactionLedgerJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(policyTransactionLedgerJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaDataUpdateJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaDataUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void activityTimeoutJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(activityTimeoutJob);
	}

	@Parameters({"state"})
	@Test()
	public void activityHistoryJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(activityHistoryJob);
	}

	@Parameters({"state"})
	@Test()
	public void activitySummarizationJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(activitySummarizationJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaAutomatedProcessingInitiationJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingRunReportsServicesJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(automatedProcessingRunReportsServicesJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingRatingJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(automatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingIssuingOrProposingJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(automatedProcessingIssuingOrProposingJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingStrategyStatusUpdateJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(automatedProcessingStrategyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void automatedProcessingBypassingAndErrorsReportGenerationJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Parameters({"state"})
	@Test()
	public void ledgerStatusUpdateJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(ledgerStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaRefundDisbursementAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaRefundDisbursementAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaEscheatmentProcessAsyncJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaEscheatmentProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void aaaGenerateEscheatmentReportJob(@Optional("") String state) {
		executeBatchUsingBatchJobService(aaaGenerateEscheatmentReportJob);
	}


}
