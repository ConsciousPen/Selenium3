/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.general;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.base.app.Application;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import aaa.admin.pages.AdminPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.utils.JobRunner;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class GeneralSchedulerPage extends AdminPage {

	public static Table tableScheduledJobs = new Table(By.xpath("//div[@id='jobs:jobsTable']//table"));
	private static final int MAX_JOB_RUN_RETRIES = 500;
	private static final int MAX_JOB_RUN_TIMEOUT = 1200000;
	private static final int JOB_RUN_RETRIES_SLEEP = 5;
	private static final String JOB_RESULT_XPATH_TEMPLATE = "//table[@id='jobs:jobsTable']/tbody/tr[td[position()=1 and normalize-space(.)='%s']]/td[2]/table";

	public static Button buttonSaveJob = new Button(By.id("jobsForm:saveButton_footer"));
	public static Button buttonAddNewGroup = new Button(By.id("jobs:new"));
	public static Button buttonAddJobButton = new Button(By.id("jobsForm:addJobButton"));
	public static TextBox textBoxGroupName = new TextBox(By.id("jobsForm:groupName"));
	public static ComboBox comboBoxJobBatch = new ComboBox(By.id("jobsForm:job_0_class"));

	public enum Job {
		AAA_BATCH_MARKER_JOB("aaaBatchMarkerJob"),
		AAA_AUTOMATED_PROCESSING_INITIATION_JOB("aaaAutomatedProcessingInitiationJob"),
		AUTOMATED_PROCESSING_INITIATION_JOB("automatedProcessingInitiationJob"),
		AUTOMATED_PROCESSING_ISSUING_OR_PROPOSING_JOB("automatedProcessingIssuingOrProposingJob"),
		AUTOMATED_PROCESSING_RATING_JOB("automatedProcessingRatingJob"),
		BENEFITS_BILLING_INVOICE_JOB("benefits.billingInvoiceJob"),
		BILLING_INVOICE_GENERATION_JOB("billingInvoiceGenerationJob"),
		CANCELLATION_CONFIRMATION_GENERATION_JOB("cancellationConfirmationGenerationJob"),
		CANCELLATION_NOTICE_GENERATION_JOB("cancellationNoticeGenerationJob"),
		CASCADING_TRANSACTION_CERTIFICATE_PROCESSING_DISPATCHING_JOB("cascadingTransactionCertificateProcessingDispatchingJob"),
		CASCADING_TRANSACTION_COMPLETION_DTECTION_JOB("cascadingTransactionCompletionDetectionJob"),
		CASCADING_TRANSACTION_PROCESSING_INITIATION_JOB("cascadingTransactionProcessingInitiationJob"),
		EARNED_PREMIUM_WRITEOFF_PROCESSING_JOB("earnedPremiumWriteoffProcessingJob"),
		POLICY_AUTOMATED_RENEWAL_ASYNC_TASK_GENERATION_JOB("policyAutomatedRenewalAsyncTaskGenerationJob"),
		POLICY_BOR_TRANSFER_JOB("policyBORTransferJob"),
		POLICY_STATUS_UPDATE_JOB("policyStatusUpdateJob"),
		REFUND_GENERATION_JOB("refundGenerationJob"),
		RENEWAL_PROPOSING_JOB("renewalProposingJob"),
		RENEWAL_RATING_JOB("renewalRatingJob"),
		PENDING_UPDATE_JOB("pendingUpdateJob"),
		CFT_DCS_EOD_JOB("cftDcsEodJob"),
		AAA_REFUND_DISBURSEMENT_ASYNC_JOB("aaaRefundDisbursementAsyncJob"),
		AAA_REFUND_DISBURSEMENT_RECEIVE_INFO_JOB("aaaRefundsDisbursementReceiveInfoAsyncJob"),
		AAA_REFUND_CANCELLATION_ASYNC_JOB("aaaRefundCancellationAsyncJob"),
		AAA_REFUND_GENERATION_ASYNC_JOB("aaaRefundGenerationAsyncJob"),
		RENEWAL_OFFER_GENERATION_PART_1("Renewal_Offer_Generation_Part1"),
		RENEWAL_OFFER_GENERATION_PART_2("Renewal_Offer_Generation_Part2"),
		AUTOMATED_PROCESSING_RUN_REPORTS_SERVICES_JOB("automatedProcessingRunReportsServicesJob"),
		AUTOMATED_PROCESSING_STRATEGY_STATUS_UPDATE_JOB("automatedProcessingStrategyStatusUpdateJob"),
		AUTOMATED_PROCESSING_BYPASSING_AND_ERRORS_REPORT_GENERATION_JOB("automatedProcessingBypassingAndErrorsReportGenerationJob"),
		AAA_MEMBERSHIP_RENEWAL_BATCH_RECEIVE_ASYNC_JOB("aaaMembershipRenewalBatchReceiveAsyncJob"),
		AAA_MEMBERSHIP_RENEWAL_BATCH_ORDER_ASYNC_JOB("aaaMembershipRenewalBatchOrderAsyncJob"),
		RENEWAL_IMAGE_RATING_ASYNC_TASK_JOB("renewalImageRatingAsyncTaskJob"),
		AAA_REFUNDS_DISBURSMENT_REJECTIONS_ASYNC_JOB("aaaRefundsDisbursementRejectionsAsyncJob"),
		AAA_PAYMENT_CENTRAL_REJECT_FEED_ASYNC_JOB("aaaPaymentCentralRejectFeedAsyncJob"),
		AAA_RECURRING_PAYMENTS_RESPONSE_PROCESS_ASYNC_JOB("aaaRecurringPaymentsResponseProcessAsyncJob");
		String id;

		Job(String id) {
			this.id = id;
		}

		public String get() {
			return id;
		}
	}

	public static void runJob(Job jobName) {
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		log.info("[JOBS] Job " + jobName.get() + " will be executed");

		if (createJob(jobName)) {
			reopenGeneralScheduler();
			verifyJobInformationPresence(jobName);
			recallNewlyCreatedJob(true, jobName);
		}
		verifyScheduledJobsPresence();

		long startTime = System.currentTimeMillis();
		int counterBefore = getJobCounter(jobName);

		executeJob(jobName);
		reopenGeneralScheduler();

		for (int i = 0; i <= MAX_JOB_RUN_RETRIES; i++) {
			verifyJobInformationPresence(jobName);
			waitForJob();

			int counterAfter = getJobCounter(jobName);
			if (getJobStatus(jobName).equals("Idle") & counterAfter > counterBefore) {
				if (getJobResult(jobName).equals("Success")) {
					log.info(String.format("[JOBS] Job %s running time: %s seconds", jobName.get(), (System.currentTimeMillis() - startTime) / 1000));
					return;
				}
				throw new IstfException(String.format("[JOBS] Job %s was completed with result %s after rerun. Job running time: %s seconds",
						jobName.get(), getJobResult(jobName), (System.currentTimeMillis() - startTime) / 1000));
			}

			if (System.currentTimeMillis() - startTime > MAX_JOB_RUN_TIMEOUT) {
				throw new IstfException(String.format("[JOBS] Job %s was timed out. Job running time is more than %s seconds",
						jobName.get(), MAX_JOB_RUN_TIMEOUT / 1000));
			}

			reopenGeneralScheduler();
		}

		throw new IstfException(String.format("[JOBS] Job %s was not completed. Job running time: %s seconds",
				jobName.get(), (System.currentTimeMillis() - startTime) / 1000));
	}

	public static boolean createJob(Job jobName) {
		/**
		 * This is needed, because when job was executed at least once, the text "Completed in.." present in the td with jobname
		 * old tableScheduledJobs.getRow(1, jobName.get()).isPresent() verification doesn't work in this case.
		 * also, performance wasn't affected, getRow - call getRows inside also, and current implementation even faster :
		 * anyMatch : May not evaluate the predicate on all elements if not * necessary for determining the result.
		 */
		boolean jobPresence = tableScheduledJobs.getRows().stream().anyMatch(row -> row.getCell(1).getValue().contains(jobName.get()));
		if (!jobPresence) {
			buttonAddNewGroup.click();
			buttonAddJobButton.click();
			textBoxGroupName.setValue(jobName.get());
			comboBoxJobBatch.setValue(jobName.get());
			buttonSaveJob.click();
			log.info("[JOBS] Job {} was created", jobName.get());
			return true;
		} else if (jobPresence) {
			log.info("{} is present", jobName.get());
			return true;
		}
		return false;
	}

	public static String getJobStatus(Job jobName) {
		return new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName.get()))).getRow(1).getCell(2).getValue().split(":")[1];
	}

	public static int getJobCounter(Job jobName) {
		return Integer.parseInt(new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName.get()))).getRow(1).getCell(1).getValue().split(":")[1]);
	}

	public static String getJobResult(Job jobName) {
		return new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName.get()))).getRow(2).getCell(2).getValue().split(":")[1];
	}

	private static void executeJob(Job jobName) {
		new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName.get()))).getRow(1).getCell(4).controls.links.getFirst().click();
	}

	public static void eliminateJob(Job jobName) {
		new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName.get()))).getRow(3).getCell(4).controls.links.getFirst().click();
		Page.dialogConfirmation.confirm();
	}

	//TODO(vmarkouski): workaround for EISDEV-119304
	public static void reopenGeneralScheduler() {
		CSAAApplicationFactory.get().adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
	}

	//TODO(vmarkouski): workaround for EISDEV-119304
	private static void verifyScheduledJobsPresence() {
		if (!tableScheduledJobs.isPresent()) {
			log.error("Scheduled Jobs table is not visible. Retrying...");
			reopenGeneralScheduler();
		}
	}

	//TODO(vmarkouski): workaround for EISDEV-119304
	private static void verifyJobInformationPresence(Job jobName) {
		if (!new Table(By.xpath(String.format(JOB_RESULT_XPATH_TEMPLATE, jobName.get()))).isPresent()) {
			log.error("General Scheduler table is not visible. Retrying...");
			reopenGeneralScheduler();
		}
	}

	private static void waitForJob() {
		Application.wait(JOB_RUN_RETRIES_SLEEP);
		try {
			NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		} catch (Exception e) {
			log.error("Cannot click Scheduler link: " + e.getMessage() + ". Retrying ...", e);
		}
	}

	//TODO(pkaziuchyts): workaround for rerun of a newly created job to support execution
	private static void recallNewlyCreatedJob(boolean isJobNewlyCreated, Job jobName) {
		if (isJobNewlyCreated) {
			reopenGeneralScheduler();
			executeJob(jobName);
			reopenGeneralScheduler();
		} else {
			JobRunner.restartAsyncManager();
		}
	}
}
