/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.general;

import aaa.JobRunner;
import aaa.admin.pages.AdminPage;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import com.exigen.ipb.etcsa.base.app.Application;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.etcsa.base.app.LoginPage;
import org.openqa.selenium.By;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.exceptions.IstfException;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class GeneralSchedulerPage extends AdminPage {

	public static Table tableScheduledJobs = new Table(By.id("jobs:jobsTable"));
    private static final int MAX_JOB_RUN_RETRIES = 500;
    private static final int MAX_JOB_RUN_TIMEOUT = 1200000;
    private static final int JOB_RUN_RETRIES_SLEEP = 5;
    private static final String JOB_RESULT_XPATH_TEMPLATE = "//table[@id='jobs:jobsTable']/tbody/tr[td[position()=1 and normalize-space(.)='%s']]/td[2]/table";

    public enum Job {
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
        AAA_REFUND_GENERATION_ASYNC_JOB("aaaRefundGenerationAsyncJob");

        String id;

        Job(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }

    public static void runJob(Job jobName) {
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
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
            if ((getJobStatus(jobName).equals("Idle")) & (counterAfter > counterBefore)) {
                if (getJobResult(jobName).equals("Success")) {
                    log.info(String.format("[JOBS] Job %s running time: %s seconds", jobName.get(), (System.currentTimeMillis() - startTime) / 1000));
                    return;
                }
                throw new IstfException(String.format("[JOBS] Job %s was completed with result %s after rerun. Job running time: %s seconds",
                        jobName.get(), getJobResult(jobName), (System.currentTimeMillis() - startTime) / 1000));
            }

            if ((System.currentTimeMillis() - startTime) > MAX_JOB_RUN_TIMEOUT) {
                throw new IstfException(String.format("[JOBS] Job %s was timed out. Job running time is more than %s seconds",
                        jobName.get(), MAX_JOB_RUN_TIMEOUT / 1000));
            }

            reopenGeneralScheduler();
        }

        throw new IstfException(String.format("[JOBS] Job %s was not completed. Job running time: %s seconds",
                jobName.get(), (System.currentTimeMillis() - startTime) / 1000));
    }

    public static boolean createJob(Job jobName) {
        if (!tableScheduledJobs.getRow(1, jobName.get()).isPresent()) {
            new Button(By.id("jobs:createNewJob")).click();
            new Button(By.id("jobsForm:addJobButton")).click();
            new TextBox(By.id("jobsForm:groupName")).setValue(jobName.get());
            new ComboBox(By.id("jobsForm:job_0_class")).setValue(jobName.get());
            Tab.buttonSave.click();

            log.info(String.format("[JOBS] Job " + jobName.get() + " was created"));

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

    private static void waitForJob() {
        Application.wait(JOB_RUN_RETRIES_SLEEP);
        try {
            NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        } catch (Exception e) {
            log.error("Cannot click Scheduler link: " + e.getMessage() + ". Retrying ...", e);
        }
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

    //TODO(vmarkouski): workaround for EISDEV-119304
    public static void reopenGeneralScheduler() {
        CSAAApplicationFactory.get().adminApp(new LoginPage(
                PropertyProvider.getProperty(TestProperties.EU_USER),
                PropertyProvider.getProperty(TestProperties.EU_PASSWORD))).open();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
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
