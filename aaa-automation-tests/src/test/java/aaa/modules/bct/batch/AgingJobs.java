package aaa.modules.bct.batch;

import static aaa.helpers.jobs.Jobs.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.policy.BackwardCompatibilityBaseTest;

public class AgingJobs extends BackwardCompatibilityBaseTest {

	protected static Logger log = LoggerFactory.getLogger(AgingJobs.class);

	@Parameters({"state"})
	@Test()
	public void createAgingJobs(@Optional("") String state) {
		SoapJobActions service = new SoapJobActions();

		for (Job job : getAgingJobsLogicalSequence()) {
			if (!service.isJobExist(JobGroup.fromSingleJob(job.getJobName()))) {
				log.info("{} was created", JobGroup.fromSingleJob(job.getJobName()));
				service.createJob(JobGroup.fromSingleJob(job.getJobName()));
			}else{
				log.info("Job exist :  {} ", JobGroup.fromSingleJob(job.getJobName()));
			}
		}
	}

	@Parameters({"state"})
	@Test()
	public void checkSchedulerEnabled(@Optional("") String state) {
		adminApp().open();
		GeneralSchedulerPage.open();
		GeneralSchedulerPage.enableScheduler();
		adminApp().close();
	}

	@Parameters({"state"})
	@Test(groups = Groups.PRECONDITION)
	public void RUN_01_AAABATCHMARKERJOB(@Optional("") String state) {
		executeAgingJob(aaaBatchMarkerJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_02_POLICYSTATUSUPDATEJOB(@Optional("") String state) {
		executeAgingJob(policyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_03_AAAPOLICYAUTOMATEDRENEWALASYNCTASKGENERATIONJOB(@Optional("") String state) {
		executeAgingJob(policyAutomatedRenewalAsyncTaskGenerationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_04_RENEWALVALIDATIONASYNCTASKJOB(@Optional("") String state) {
		executeAgingJob(renewalValidationAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_05_RENEWALIMAGERATINGASYNCTASKJOB(@Optional("") String state) {
		executeAgingJob(renewalImageRatingAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_06_AAAREMITTANCEFEEDASYNCBATCHRECEIVEJOB(@Optional("") String state) {
		executeAgingJob(aaaRemittanceFeedAsyncBatchReceiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_07_AAARECURRINGPAYMENTSASYNCPROCESSJOB(@Optional("") String state) {
		executeAgingJob(aaaRecurringPaymentsProcessingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_08_BOFARECURRINGPAYMENTJOB(@Optional("") String state) {
		executeAgingJob(bofaRecurringPaymentJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_09_PREMIUMRECEIVABLESONPOLICYEFFECTIVEJOB(@Optional("") String state) {
		executeAgingJob(premiumReceivablesOnPolicyEffectiveJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_10_CHANGECANCELLATIONPENDINGPOLICIESSTATUSJOB(@Optional("") String state) {
		executeAgingJob(changeCancellationPendingPoliciesStatus);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_11_AAACANCELLATIONNOTICEASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaCancellationNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_12_AAACANCELLATIONCONFIRMATIONASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaCancellationConfirmationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_13_AAACOLLECTIONCANCELLDEBTBATCHASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaCollectionCancelDebtBatchAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_14_COLLECTIONFEEDBATCHORDERJOB(@Optional("") String state) {
		executeAgingJob(collectionFeedBatchorderJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_15_EARNEDPREMIUMWRITEOFFPROCESSINGJOB(@Optional("") String state) {
		executeAgingJob(earnedPremiumWriteoffProcessingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_16_AAAOFFCYCLEBILLINGINVOICEASYNCJOB(@Optional("") String state) {
		executeAgingJob(offCycleBillingInvoiceAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_17_AAABILLINGINVOICEASYNCTASKJOB(@Optional("") String state) {
		executeAgingJob(aaaBillingInvoiceAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_18_AAAREFUNDGENERATIONASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaRefundGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_19_PRERENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
		executeAgingJob(preRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_20_AAARENEWALNOTICEBILLASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaRenewalNoticeBillAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_21_AAAMORTGAGEERENEWALREMINDERANDEXPNOTICEASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_22_RENEWALOFFERASYNCTASKJOB(@Optional("") String state) {
		executeAgingJob(renewalOfferAsyncTaskJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_23_AAADELAYTRIGGERTOINOTICEASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaDelayTriggerTOINoticeAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_24_POLICYLAPSEDRENEWALPROCESSASYNCJOB(@Optional("") String state) {
		executeAgingJob(policyLapsedRenewalProcessAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_25_AAARENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
		executeAgingJob(aaaRenewalReminderGenerationAsyncJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_26_POLICYTRANSACTIONLEDGERJOB(@Optional("") String state) {
		executeAgingJob(policyTransactionLedgerJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_27_AAADATAUPDATEJOB(@Optional("") String state) {
		executeAgingJob(aaaDataUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_28_ACTIVITYTIMEOUTJOB(@Optional("") String state) {
		executeAgingJob(activityTimeoutJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_29_ACTIVITYHISTORYJOB(@Optional("") String state) {
		executeAgingJob(activityHistoryJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_30_ACTIVITYSUMMARIZATIONJOB(@Optional("") String state) {
		executeAgingJob(activitySummarizationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_31_AAAAUTOMATEDPROCESSINGINITIATIONJOB(@Optional("") String state) {
		executeAgingJob(aaaAutomatedProcessingInitiationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_32_AUTOMATEDPROCESSINGRUNREPORTSSERVICESJOB(@Optional("") String state) {
		executeAgingJob(automatedProcessingRunReportsServicesJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_33_AUTOMATEDPROCESSINGRATINGJOB(@Optional("") String state) {
		executeAgingJob(automatedProcessingRatingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_34_AUTOMATEDPROCESSINGISSUINGORPROPOSINGJOB(@Optional("") String state) {
		executeAgingJob(automatedProcessingIssuingOrProposingJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_35_AUTOMATEDPROCESSINGSTRATEGYSTATUSUPDATEJOB(@Optional("") String state) {
		executeAgingJob(automatedProcessingStrategyStatusUpdateJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_36_AUTOMATEDPROCESSINGBYPASSINGANDERRORSREPORT(@Optional("") String state) {
		executeAgingJob(automatedProcessingBypassingAndErrorsReportGenerationJob);
	}

	@Parameters({"state"})
	@Test()
	public void RUN_37_LEDGERSTATUSUPDATEJOB(@Optional("") String state) {
		executeAgingJob(ledgerStatusUpdateJob);
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
		list.add(changeCancellationPendingPoliciesStatus);
		list.add(aaaCancellationNoticeAsyncJob);
		list.add(aaaCancellationConfirmationAsyncJob);
		list.add(aaaCollectionCancelDebtBatchAsyncJob);
		list.add(collectionFeedBatchorderJob);
		list.add(earnedPremiumWriteoffProcessingJob);
		list.add(offCycleBillingInvoiceAsyncJob);
		list.add(aaaBillingInvoiceAsyncTaskJob);
		list.add(aaaRefundGenerationAsyncJob);
		list.add(preRenewalReminderGenerationAsyncJob);
		list.add(aaaRenewalNoticeBillAsyncJob);
		list.add(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
		list.add(renewalOfferAsyncTaskJob);
		list.add(aaaDelayTriggerTOINoticeAsyncJob);
		list.add(policyLapsedRenewalProcessAsyncJob);
		list.add(aaaRenewalReminderGenerationAsyncJob);
		list.add(policyTransactionLedgerJob);
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
