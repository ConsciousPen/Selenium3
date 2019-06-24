package aaa.modules.bct.batch;

import static aaa.helpers.jobs.BatchJob.*;
import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class AgingJobs extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(AgingJobs.class);

	@Parameters({"state"})
	@Test(groups = Groups.PRECONDITION)
	public void RUN_01_AAABATCHMARKERJOB(@Optional("") String state) {
		executeBatchTest(aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_02_POLICYSTATUSUPDATEJOB(@Optional("") String state) {
		executeBatchTest(policyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_03_AAAPOLICYAUTOMATEDRENEWALASYNCTASKGENERATIONJOB(@Optional("") String state) {
		executeBatchTest(new Job("policyAutomatedRenewalAsyncTaskGenerationJob", Collections.singletonMap("JOB_UI_PARAMS", "t")));
	}

	@Parameters({"state"})
	@Test()
	public void RUN_04_RENEWALVALIDATIONASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(renewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_05_RENEWALIMAGERATINGASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(renewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_06_AAAREMITTANCEFEEDASYNCBATCHRECEIVEJOB(@Optional("") String state) {
		executeBatchTest(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_07_AAARECURRINGPAYMENTSASYNCPROCESSJOB(@Optional("") String state) {
		executeBatchTest(aaaRecurringPaymentsProcessingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_08_BOFARECURRINGPAYMENTJOB(@Optional("") String state) {
		executeBatchTest(bofaRecurringPaymentJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_09_PREMIUMRECEIVABLESONPOLICYEFFECTIVEJOB(@Optional("") String state) {
		executeBatchTest(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_10_CHANGECANCELLATIONPENDINGPOLICIESSTATUSJOB(@Optional("") String state) {
		executeBatchTest(changeCancellationPendingPoliciesStatusJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_11_AAACANCELLATIONNOTICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaCancellationNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_12_AAACANCELLATIONCONFIRMATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaCancellationConfirmationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_13_AAACOLLECTIONCANCELLDEBTBATCHASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaCollectionCancellDebtBatchAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_14_COLLECTIONFEEDBATCHORDERJOB(@Optional("") String state) {
		executeBatchTest(collectionFeedBatchOrderJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_15_EARNEDPREMIUMWRITEOFFPROCESSINGJOB(@Optional("") String state) {
		executeBatchTest(earnedPremiumWriteoffProcessingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_16_AAAOFFCYCLEBILLINGINVOICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(offCycleBillingInvoiceAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_17_AAABILLINGINVOICEASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(aaaBillingInvoiceAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_18_AAAREFUNDGENERATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaRefundGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_19_PRERENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(preRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_20_AAARENEWALNOTICEBILLASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_21_AAAMORTGAGEERENEWALREMINDERANDEXPNOTICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_22_RENEWALOFFERASYNCTASKJOB(@Optional("") String state) {
		executeBatchTest(renewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_23_AAADELAYTRIGGERTOINOTICEASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_24_POLICYLAPSEDRENEWALPROCESSASYNCJOB(@Optional("") String state) {
		executeBatchTest(lapsedRenewalProcessJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_25_AAARENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
		executeBatchTest(aaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_26_POLICYTRANSACTIONLEDGERJOB(@Optional("") String state) {
		executeBatchTest(policyTransactionLedgerJob_NonMonthly);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_27_AAADATAUPDATEJOB(@Optional("") String state) {
		executeBatchTest(aaaDataUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_28_ACTIVITYTIMEOUTJOB(@Optional("") String state) {
		executeBatchTest(activityTimeoutJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_29_ACTIVITYHISTORYJOB(@Optional("") String state) {
		executeBatchTest(activityHistoryJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_30_ACTIVITYSUMMARIZATIONJOB(@Optional("") String state) {
		executeBatchTest(activitySummarizationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_31_AAAAUTOMATEDPROCESSINGINITIATIONJOB(@Optional("") String state) {
		executeBatchTest(aaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_32_AUTOMATEDPROCESSINGRUNREPORTSSERVICESJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingRunReportsServicesJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_33_AUTOMATEDPROCESSINGRATINGJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_34_AUTOMATEDPROCESSINGISSUINGORPROPOSINGJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingIssuingOrProposingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_35_AUTOMATEDPROCESSINGSTRATEGYSTATUSUPDATEJOB(@Optional("") String state) {
		executeBatchTest(automatedProcessingStrategyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_36_AUTOMATEDPROCESSINGBYPASSINGANDERRORSREPORT(@Optional("") String state) {
		executeBatchTest(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_37_LEDGERSTATUSUPDATEJOB(@Optional("") String state) {
		executeBatchTest(ledgerStatusUpdateJob);
	}

	private ArrayList<Job> getAgingJobsLogicalSequence() {

		ArrayList<Job> list = new ArrayList<Job>();
		list.add(aaaBatchMarkerJob);
		list.add(policyStatusUpdateJob);
		list.add(policyAutomatedRenewalAsyncTaskGenerationJob);
		list.add(renewalValidationAsyncTaskJob);
		list.add(renewalImageRatingAsyncTaskJob);
		list.add(aaaRemittanceFeedAsyncBatchReceiveJob);
		list.add(aaaRecurringPaymentsProcessingJob);
		list.add(bofaRecurringPaymentJob);
		list.add(premiumReceivablesOnPolicyEffectiveJob);
		list.add(changeCancellationPendingPoliciesStatusJob);
		list.add(aaaCancellationNoticeAsyncJob);
		list.add(aaaCancellationConfirmationAsyncJob);
		list.add(aaaCollectionCancellDebtBatchAsyncJob);
		list.add(collectionFeedBatchOrderJob);
		list.add(earnedPremiumWriteoffProcessingJob);
		list.add(offCycleBillingInvoiceAsyncJob);
		list.add(aaaBillingInvoiceAsyncTaskJob);
		list.add(aaaRefundGenerationAsyncJob);
		list.add(preRenewalReminderGenerationAsyncJob);
		list.add(aaaRenewalNoticeBillAsyncJob);
		list.add(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
		list.add(renewalOfferAsyncTaskJob);
		list.add(aaaDelayTriggerTOINoticeAsyncJob);
		list.add(lapsedRenewalProcessJob);
		list.add(aaaRenewalReminderGenerationAsyncJob);
		list.add(policyTransactionLedgerJob_NonMonthly);
		list.add(aaaDataUpdateJob);
		list.add(activityTimeoutJob);
		list.add(activityHistoryJob);
		list.add(activitySummarizationJob);
		list.add(aaaAutomatedProcessingInitiationJob);
		list.add(automatedProcessingRunReportsServicesJob);
		list.add(automatedProcessingRatingJob);
		list.add(automatedProcessingIssuingOrProposingJob);
		list.add(automatedProcessingStrategyStatusUpdateJob);
		list.add(automatedProcessingBypassingAndErrorsReportGenerationJob);
		list.add(ledgerStatusUpdateJob);

		return list;
	}

}
