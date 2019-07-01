package aaa.modules.bct.batch;

import static aaa.helpers.jobs.Jobs.*;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.jobs.Job;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class ParallelAgingJobs extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(ParallelAgingJobs.class);

	@Parameters({"state"})
	@Test(groups = {"aaaBatchMarkerJob"}, alwaysRun = true)
	public void RUN_01_AAABATCHMARKERJOB(@Optional("") String state) {
		executeBatchTest(aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test(groups = {"policyStatusUpdateJob"}, dependsOnGroups = {"aaaBatchMarkerJob"}, alwaysRun = true)
	public void RUN_02_POLICYSTATUSUPDATEJOB(@Optional("") String state) {
		executeBatchTest(policyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"policyAutomatedRenewalAsyncTaskGenerationJob"}, dependsOnGroups = {"policyStatusUpdateJob"}, alwaysRun = true)
	public void RUN_03_AAAPOLICYAUTOMATEDRENEWALASYNCTASKGENERATIONJOB(@Optional("") String state) {
		executeBatchTest(new Job("aaaPolicyAutomatedRenewalAsyncTaskGenerationJob", Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Parameters({"state"})
	@Test(groups = {"renewalValidationAsyncTaskJob"}, dependsOnGroups = {"policyAutomatedRenewalAsyncTaskGenerationJob"}, alwaysRun = true)
	public void RUN_04_RENEWALVALIDATIONASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(renewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"renewalImageRatingAsyncTaskJob"}, dependsOnGroups = {"renewalValidationAsyncTaskJob"}, alwaysRun = true)
	public void RUN_05_RENEWALIMAGERATINGASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(renewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaRemittanceFeedAsyncBatchReceiveJob"}, dependsOnGroups = {"policyStatusUpdateJob"}, alwaysRun = true)
	public void RUN_06_AAAREMITTANCEFEEDASYNCBATCHRECEIVEJOB(@Optional("") String state) {
		executeBatchTest(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaRecurringPaymentsAsyncProcessJob"}, dependsOnGroups = {"policyStatusUpdateJob"}, alwaysRun = true)
	public void RUN_07_AAARECURRINGPAYMENTSASYNCPROCESSJOB(@Optional("") String state) {
		executeBatchTest(new Job("aaaRecurringPaymentsAsyncProcessJob"));
	}

	@Parameters({"state"})
	@Test(groups = {"bofaRecurringPaymentJob"}, dependsOnGroups = {"aaaRecurringPaymentsAsyncProcessJob"}, alwaysRun = true)
	public void RUN_08_BOFARECURRINGPAYMENTJOB(@Optional("") String state) {
		executeBatchTest(bofaRecurringPaymentJob);
	}

	@Parameters({"state"})
	@Test(groups = {"premiumReceivablesOnPolicyEffectiveJob"}, dependsOnGroups = {"bofaRecurringPaymentJob"}, alwaysRun = true)
	public void RUN_09_PREMIUMRECEIVABLESONPOLICYEFFECTIVEJOB(@Optional("") String state) {
		executeBatchTest(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Parameters({"state"})
	@Test(groups = {"changeCancellationPendingPoliciesStatusJob"}, dependsOnGroups = {"premiumReceivablesOnPolicyEffectiveJob"}, alwaysRun = true)
	public void RUN_10_CHANGECANCELLATIONPENDINGPOLICIESSTATUSJOB(@Optional("") String state) {
		executeBatchTest(new Job("changeCancellationPendingPoliciesStatusJob"));
	}

	@Parameters({"state"})
	@Test(groups = {"aaaCancellationNoticeAsyncJob"}, dependsOnGroups = {"premiumReceivablesOnPolicyEffectiveJob"}, alwaysRun = true)
	public void RUN_11_AAACANCELLATIONNOTICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaCancellationNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaCancellationConfirmationAsyncJob"}, dependsOnGroups = {"premiumReceivablesOnPolicyEffectiveJob"}, alwaysRun = true)
	public void RUN_12_AAACANCELLATIONCONFIRMATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaCancellationConfirmationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaCollectionCancellDebtBatchAsyncJob"}, dependsOnGroups = {"aaaCancellationConfirmationAsyncJob"}, alwaysRun = true)
	public void RUN_13_AAACOLLECTIONCANCELLDEBTBATCHASYNCJOB(@Optional("") String state) {
		executeBatchTest(new Job("aaaCollectionCancellDebtBatchAsyncJob"));
	}

	@Parameters({"state"})
	@Test(groups = {"collectionFeedBatchOrderJob"}, dependsOnGroups = {"aaaCollectionCancellDebtBatchAsyncJob"}, alwaysRun = true)
	public void RUN_14_COLLECTIONFEEDBATCHORDERJOB(@Optional("") String state) {
		executeBatchTest(collectionFeedBatchorderJob);
	}

	@Parameters({"state"})
	@Test(groups = {"earnedPremiumWriteoffProcessingJob"}, dependsOnGroups = {"collectionFeedBatchOrderJob"}, alwaysRun = true)
	public void RUN_15_EARNEDPREMIUMWRITEOFFPROCESSINGJOB(@Optional("") String state) {
		executeBatchTest(earnedPremiumWriteoffProcessingJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaOffCycleBillingInvoiceAsyncJob"}, dependsOnGroups = {"earnedPremiumWriteoffProcessingJob"}, alwaysRun = true)
	public void RUN_16_AAAOFFCYCLEBILLINGINVOICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(offCycleBillingInvoiceAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaBillingInvoiceAsyncTaskJob"}, dependsOnGroups = {"earnedPremiumWriteoffProcessingJob"}, alwaysRun = true)
	public void RUN_17_AAABILLINGINVOICEASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(aaaBillingInvoiceAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaRefundGenerationAsyncJob"}, dependsOnGroups = {"aaaBillingInvoiceAsyncTaskJob"}, alwaysRun = true)
	public void RUN_18_AAAREFUNDGENERATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaRefundGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"preRenewalReminderGenerationAsyncJob"}, dependsOnGroups = {"aaaRefundGenerationAsyncJob"}, alwaysRun = true)
	public void RUN_19_PRERENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(preRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaRenewalNoticeBillAsyncJob"}, dependsOnGroups = {"aaaRefundGenerationAsyncJob"}, alwaysRun = true)
	public void RUN_20_AAARENEWALNOTICEBILLASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaMortgageeRenewalReminderAndExpNoticeAsyncJob"}, dependsOnGroups = {"aaaRefundGenerationAsyncJob"}, alwaysRun = true)
	public void RUN_21_AAAMORTGAGEERENEWALREMINDERANDEXPNOTICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"renewalOfferAsyncTaskJob"}, dependsOnGroups = {"aaaMortgageeRenewalReminderAndExpNoticeAsyncJob"}, alwaysRun = true)
	public void RUN_22_RENEWALOFFERASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(renewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaDelayTriggerTOINoticeAsyncJob"}, dependsOnGroups = {"aaaMortgageeRenewalReminderAndExpNoticeAsyncJob"}, alwaysRun = true)
	public void RUN_23_AAADELAYTRIGGERTOINOTICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"policyLapsedRenewalProcessAsyncJob"}, dependsOnGroups = {"renewalOfferAsyncTaskJob"}, alwaysRun = true)
	public void RUN_24_POLICYLAPSEDRENEWALPROCESSASYNCJOB(@Optional("") String state) {
		executeBatchTest(policyLapsedRenewalProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaRenewalReminderGenerationAsyncJob"}, dependsOnGroups = {"policyLapsedRenewalProcessAsyncJob"}, alwaysRun = true)
	public void RUN_25_AAARENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test(groups = {"policyTransactionLedgerJob"}, dependsOnGroups = {"aaaRenewalReminderGenerationAsyncJob"}, alwaysRun = true)
	public void RUN_26_POLICYTRANSACTIONLEDGERJOB(@Optional("") String state) {
		executeBatchTest(policyTransactionLedgerJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaDataUpdateJob"}, dependsOnGroups = {"aaaRenewalReminderGenerationAsyncJob"}, alwaysRun = true)
	public void RUN_27_AAADATAUPDATEJOB(@Optional("") String state) {
		executeBatchTest(aaaDataUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"activityTimeoutJob"}, dependsOnGroups = {"premiumReceivablesOnPolicyEffectiveJob"}, alwaysRun = true)
	public void RUN_28_ACTIVITYTIMEOUTJOB(@Optional("") String state) {
		executeBatchTest(activityTimeoutJob);
	}

	@Parameters({"state"})
	@Test(groups = {"activityHistoryJob"}, dependsOnGroups = {"premiumReceivablesOnPolicyEffectiveJob"}, alwaysRun = true)
	public void RUN_29_ACTIVITYHISTORYJOB(@Optional("") String state) {
		executeBatchTest(activityHistoryJob);
	}

	@Parameters({"state"})
	@Test(groups = {"activitySummarizationJob"}, dependsOnGroups = {"premiumReceivablesOnPolicyEffectiveJob"}, alwaysRun = true)
	public void RUN_30_ACTIVITYSUMMARIZATIONJOB(@Optional("") String state) {
		executeBatchTest(activitySummarizationJob);
	}

	@Parameters({"state"})
	@Test(groups = {"aaaAutomatedProcessingInitiationJob"}, dependsOnGroups = {"policyStatusUpdateJob"}, alwaysRun = true)
	public void RUN_31_AAAAUTOMATEDPROCESSINGINITIATIONJOB(@Optional("") String state) {
		executeBatchTest(aaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test(groups = {"automatedProcessingRunReportsServicesJob"}, dependsOnGroups = {"aaaAutomatedProcessingInitiationJob"}, alwaysRun = true)
	public void RUN_32_AUTOMATEDPROCESSINGRUNREPORTSSERVICESJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingRunReportsServicesJob);
	}

	@Parameters({"state"})
	@Test(groups = {"automatedProcessingRatingJob"}, dependsOnGroups = {"automatedProcessingRunReportsServicesJob"}, alwaysRun = true)
	public void RUN_33_AUTOMATEDPROCESSINGRATINGJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test(groups = {"automatedProcessingIssuingOrProposingJob"}, dependsOnGroups = {"automatedProcessingRatingJob"}, alwaysRun = true)
	public void RUN_34_AUTOMATEDPROCESSINGISSUINGORPROPOSINGJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingIssuingOrProposingJob);
	}

	@Parameters({"state"})
	@Test(groups = {"automatedProcessingStrategyStatusUpdateJob"}, dependsOnGroups = {"automatedProcessingIssuingOrProposingJob"}, alwaysRun = true)
	public void RUN_35_AUTOMATEDPROCESSINGSTRATEGYSTATUSUPDATEJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingStrategyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test(groups = {"automatedProcessingBypassingAndErrorsReportGenerationJob"}, dependsOnGroups = {"automatedProcessingStrategyStatusUpdateJob"}, alwaysRun = true)
	public void RUN_36_AUTOMATEDPROCESSINGBYPASSINGANDERRORSREPORT(@Optional("") String state) {
		executeBatchTest(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Parameters({"state"})
	@Test(groups = {"ledgerStatusUpdateJob"}, dependsOnGroups = {"aaaRefundGenerationAsyncJob"}, alwaysRun = true)
	public void RUN_37_LEDGERSTATUSUPDATEJOB(@Optional("") String state) {
		executeBatchTest(ledgerStatusUpdateJob);
	}

}
