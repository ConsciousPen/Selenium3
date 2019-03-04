package aaa.modules.batch;

import aaa.admin.pages.general.GeneralAsyncTasksPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.waiters.Waiters;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static aaa.helpers.jobs.Jobs.*;

public class BatchMaintanance extends BaseTest {

    public static StaticElement schedulerStatus = new StaticElement(By.id("statistics:schedulerTable:0:en_status"));
    public static Link enableScheduler = new Link(By.id("statistics:schedulerTable:0:enable"));
    private static final String VERIFY_ASYNC_MANAGER_STATUS_SQL = "Select count(STATUS) from PASADM.ASYNCTASKMANAGER where Status != 'RUNNING'";

    // TODO: 3/4/2019 --- work with EM to get a service account to access PASADM Schema for Drop and update access to clean up DB locks/ Async hang instances

    //Clean Async Task--- Need Access to PASADM Schema to complete operation on BF
    @Parameters({"state"})
    @Test(groups = Groups.PRECONDITION)
    public void cleanAsynkTasks() {
        String date = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
        DBService.get().executeUpdate("delete from asynctaskproperty");
        DBService.get().executeUpdate("delete from asynctaskqueue");
        DBService.get().executeUpdate("delete from asynctask");
        DBService.get().executeUpdate("update ASYNCTASKLOCK set LOCKEDIND='0'," + " EXPIRES='" + date + " 12.00.00.000000000 AM'" + "where ID = '1'");
        log.info("DB update +++++ Clean Asynk Tasks is completed successfully ++++++\n");
    }

    //Remove all locked --- Need Access to PASADM Schema to complete operation on BF
    @Parameters({"state"})
    @Test(groups = Groups.PRECONDITION)
    public void removeError() {
        DBService.get().executeUpdate("UPDATE CM_NODE_LOCK_INFO SET PARENT_LOCK_ID=NULL,LOCK_IS_DEEP=NULL,LOCK_OWNER=NULL");
        log.info("DB update +++++ Clean parrent lock is completed successfully ++++++\n");
    }

    //validate scheduler is up
    @Parameters({"state"})
    @Test()
    public void checkSchedulerEnabled(@Optional("") String state) {
        adminApp().open();
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());

        int counter = 0;
        while (schedulerStatus.getValue().toString().equals("Disabled") && counter < 200) {
            enableScheduler.click();
            Waiters.SLEEP(3000).go();
            counter++;
            if (counter == 200) {
                log.info("Timed out after waiting for 10 minutes for status to become Submitted to Batch");
            }
        }

        adminApp().close();
    }

    //Validate Async is up
    @Parameters({"state"})
    @Test()
    public void checkAsyncManagerStatus(@Optional("") String state) {

        if (DBService.get().getValue("Select count(STATUS) from PASADM.ASYNCTASKMANAGER where Status = 'RUNNING'").get().equals("0")) {
            adminApp().open();
            NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_ASYNC_TASKS.get());
            if (!GeneralAsyncTasksPage.linkStopManager.isPresent()) {
                GeneralAsyncTasksPage.linkStartAllManager.click();
            }
            GeneralAsyncTasksPage.linkStopManager.click();
            GeneralAsyncTasksPage.linkStartAllManager.click();
        }
    }

    //pre Condition create job if does not exist
    @Parameters({"state"})
    @Test()
    public void createJobIfNotPresent(@Optional("") String state) {
        SoapJobActions service = new SoapJobActions();
        List<Job> list = new ArrayList<Job>();
        list.add(ledgerStatusUpdateJob);
        list.add(automatedProcessingBypassingAndErrorsReportGenerationJob);
        list.add(automatedProcessingStrategyStatusUpdateJob);
        list.add(automatedProcessingIssuingOrProposingJob);
        list.add(automatedProcessingRatingJob);
        list.add(automatedProcessingRunReportsServicesJob);
        list.add(aaaAutomatedProcessingInitiationJob);
        list.add(activitySummarizationJob);
        list.add(activityHistoryJob);
        list.add(activityTimeoutJob);
        list.add(aaaDataUpdateJob);
        list.add(policyTransactionLedgerJob);
        list.add(aaaRenewalReminderGenerationAsyncJob);
        list.add(policyLapsedRenewalProcessAsyncJob);
        list.add(aaaDelayTriggerTOINoticeAsyncJob);
        list.add(renewalOfferAsyncTaskJob);
        list.add(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
        list.add(aaaRenewalNoticeBillAsyncJob);
        list.add(preRenewalReminderGenerationAsyncJob);
        list.add(aaaRefundGenerationAsyncJob);
        list.add(aaaBillingInvoiceAsyncTaskJob);
        list.add(offCycleBillingInvoiceAsyncJob);
        list.add(earnedPremiumWriteoffProcessingJob);
        list.add(collectionFeedBatchorderJob);
        list.add(aaaCollectionCancelDebtBatchAsyncJob);
        list.add(aaaCancellationConfirmationAsyncJob);
        list.add(aaaCancellationNoticeAsyncJob);
        list.add(changeCancellationPendingPoliciesStatus);
        list.add(premiumreceivablesonpolicyeffectivejob);
        list.add(bofaRecurringPaymentJob);
        list.add(aaaRecurringPaymentsProcessingJob);
        list.add(aaaRemittanceFeedAsyncBatchReceiveJob);
        list.add(renewalImageRatingAsyncTaskJob);
        list.add(renewalValidationAsyncTaskJob);
        list.add(policyAutomatedRenewalAsyncTaskGenerationJob);
        list.add(policyStatusUpdateJob);
        list.add(aaaBatchMarkerJob);

        for (int i = 0; i < list.size(); i++) {
            log.info(JobGroup.fromSingleJob(list.get(i).getJobName()).toString());
            if (!service.isJobExist(JobGroup.fromSingleJob(list.get(i).getJobName()))) {
                service.createJob(JobGroup.fromSingleJob(list.get(i).getJobName()));
            }
        }
    }

    //Execution with dependencies

    @Parameters({"state"})
    @Test(groups = Groups.PRECONDITION)
    public void RUN_01_AAABATCHMARKERJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaBatchMarkerJob);
    }

    @Parameters({"state"})
    @Test()
    public void RUN_02_POLICYSTATUSUPDATEJOB(@Optional("") String state) {
        JobUtils.executeJob(policyStatusUpdateJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_02_POLICYSTATUSUPDATEJOB")
    public void RUN_03_AAAPOLICYAUTOMATEDRENEWALASYNCTASKGENERATIONJOB(@Optional("") String state) {
        JobUtils.executeJob(policyAutomatedRenewalAsyncTaskGenerationJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_03_AAAPOLICYAUTOMATEDRENEWALASYNCTASKGENERATIONJOB")
    public void RUN_04_RENEWALVALIDATIONASYNCTASKJOB(@Optional("") String state) {
        JobUtils.executeJob(renewalValidationAsyncTaskJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_04_RENEWALVALIDATIONASYNCTASKJOB")
    public void RUN_05_RENEWALIMAGERATINGASYNCTASKJOB(@Optional("") String state) {
        JobUtils.executeJob(renewalImageRatingAsyncTaskJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_05_RENEWALIMAGERATINGASYNCTASKJOB")
    public void RUN_06_AAAREMITTANCEFEEDASYNCBATCHRECEIVEJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaRemittanceFeedAsyncBatchReceiveJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_06_AAAREMITTANCEFEEDASYNCBATCHRECEIVEJOB")
    public void RUN_07_AAARECURRINGPAYMENTSASYNCPROCESSJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaRecurringPaymentsProcessingJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_07_AAARECURRINGPAYMENTSASYNCPROCESSJOB")
    public void RUN_08_BOFARECURRINGPAYMENTJOB(@Optional("") String state) {
        JobUtils.executeJob(bofaRecurringPaymentJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_08_BOFARECURRINGPAYMENTJOB")
    public void RUN_09_PREMIUMRECEIVABLESONPOLICYEFFECTIVEJOB(@Optional("") String state) {
        JobUtils.executeJob(premiumreceivablesonpolicyeffectivejob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_09_PREMIUMRECEIVABLESONPOLICYEFFECTIVEJOB")
    public void RUN_10_CHANGECANCELLATIONPENDINGPOLICIESSTATUSJOB(@Optional("") String state) {
        JobUtils.executeJob(changeCancellationPendingPoliciesStatus);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_10_CHANGECANCELLATIONPENDINGPOLICIESSTATUSJOB")
    public void RUN_11_AAACANCELLATIONNOTICEASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaCancellationNoticeAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_11_AAACANCELLATIONNOTICEASYNCJOB")
    public void RUN_12_AAACANCELLATIONCONFIRMATIONASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaCancellationConfirmationAsyncJob);
    }

    //Depends on removed and configured at Jenkins level dependsOnMethods="RUN_12_AAACANCELLATIONCONFIRMATIONASYNCJOB"
    @Parameters({"state"})
    @Test()
    public void RUN_13_AAACOLLECTIONCANCELLDEBTBATCHASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaCollectionCancelDebtBatchAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_13_AAACOLLECTIONCANCELLDEBTBATCHASYNCJOB")
    public void RUN_14_COLLECTIONFEEDBATCHORDERJOB(@Optional("") String state) {
        JobUtils.executeJob(collectionFeedBatchorderJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_14_COLLECTIONFEEDBATCHORDERJOB")
    public void RUN_15_EARNEDPREMIUMWRITEOFFPROCESSINGJOB(@Optional("") String state) {
        JobUtils.executeJob(earnedPremiumWriteoffProcessingJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_15_EARNEDPREMIUMWRITEOFFPROCESSINGJOB")
    public void RUN_16_AAAOFFCYCLEBILLINGINVOICEASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(offCycleBillingInvoiceAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_16_AAAOFFCYCLEBILLINGINVOICEASYNCJOB")
    public void RUN_17_AAABILLINGINVOICEASYNCTASKJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaBillingInvoiceAsyncTaskJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_17_AAABILLINGINVOICEASYNCTASKJOB")
    public void RUN_18_AAAREFUNDGENERATIONASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaRefundGenerationAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_18_AAAREFUNDGENERATIONASYNCJOB")
    public void RUN_19_PRERENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(preRenewalReminderGenerationAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_19_PRERENEWALREMINDERGENERATIONASYNCJOB")
    public void RUN_20_AAARENEWALNOTICEBILLASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaRenewalNoticeBillAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_20_AAARENEWALNOTICEBILLASYNCJOB")
    public void RUN_21_AAAMORTGAGEERENEWALREMINDERANDEXPNOTICEASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaMortgageeRenewalReminderAndExpNoticeAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_21_AAAMORTGAGEERENEWALREMINDERANDEXPNOTICEASYNCJOB")
    public void RUN_22_RENEWALOFFERASYNCTASKJOB(@Optional("") String state) {
        JobUtils.executeJob(renewalOfferAsyncTaskJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_22_RENEWALOFFERASYNCTASKJOB")
    public void RUN_23_AAADELAYTRIGGERTOINOTICEASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaDelayTriggerTOINoticeAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_23_AAADELAYTRIGGERTOINOTICEASYNCJOB")
    public void RUN_24_POLICYLAPSEDRENEWALPROCESSASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(policyLapsedRenewalProcessAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_24_POLICYLAPSEDRENEWALPROCESSASYNCJOB")
    public void RUN_25_AAARENEWALREMINDERGENERATIONASYNCJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaRenewalReminderGenerationAsyncJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_25_AAARENEWALREMINDERGENERATIONASYNCJOB")
    public void RUN_26_POLICYTRANSACTIONLEDGERJOB(@Optional("") String state) {
        JobUtils.executeJob(policyTransactionLedgerJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_26_POLICYTRANSACTIONLEDGERJOB")
    public void RUN_27_AAADATAUPDATEJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaDataUpdateJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_27_AAADATAUPDATEJOB")
    public void RUN_28_ACTIVITYTIMEOUTJOB(@Optional("") String state) {
        JobUtils.executeJob(activityTimeoutJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_28_ACTIVITYTIMEOUTJOB")
    public void RUN_29_ACTIVITYHISTORYJOB(@Optional("") String state) {
        JobUtils.executeJob(activityHistoryJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_29_ACTIVITYHISTORYJOB")
    public void RUN_30_ACTIVITYSUMMARIZATIONJOB(@Optional("") String state) {
        JobUtils.executeJob(activitySummarizationJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_30_ACTIVITYSUMMARIZATIONJOB")
    public void RUN_31_AAAAUTOMATEDPROCESSINGINITIATIONJOB(@Optional("") String state) {
        JobUtils.executeJob(aaaAutomatedProcessingInitiationJob);
    }

    //Depends on removed and configured at Jenkins level dependsOnMethods="RUN_31_AAAAUTOMATEDPROCESSINGINITIATIONJOB"
    @Parameters({"state"})
    @Test()
    public void RUN_32_AUTOMATEDPROCESSINGRUNREPORTSSERVICESJOB(@Optional("") String state) {
        JobUtils.executeJob(automatedProcessingRunReportsServicesJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_32_AUTOMATEDPROCESSINGRUNREPORTSSERVICESJOB")
    public void RUN_33_AUTOMATEDPROCESSINGRATINGJOB(@Optional("") String state) {
        JobUtils.executeJob(automatedProcessingRatingJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_33_AUTOMATEDPROCESSINGRATINGJOB")
    public void RUN_34_AUTOMATEDPROCESSINGISSUINGORPROPOSINGJOB(@Optional("") String state) {
        JobUtils.executeJob(automatedProcessingIssuingOrProposingJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_34_AUTOMATEDPROCESSINGISSUINGORPROPOSINGJOB")
    public void RUN_35_AUTOMATEDPROCESSINGSTRATEGYSTATUSUPDATEJOB(@Optional("") String state) {
        JobUtils.executeJob(automatedProcessingStrategyStatusUpdateJob);
    }

    @Parameters({"state"})
    @Test(dependsOnMethods = "RUN_35_AUTOMATEDPROCESSINGSTRATEGYSTATUSUPDATEJOB")
    public void RUN_36_AUTOMATEDPROCESSINGBYPASSINGANDERRORSREPORT(@Optional("") String state) {
        JobUtils.executeJob(automatedProcessingBypassingAndErrorsReportGenerationJob);
    }

    //Depends on removed and configured at Jenkins level dependsOnMethods="RUN_36_AUTOMATEDPROCESSINGBYPASSINGANDERRORSREPORT"
    @Parameters({"state"})
    @Test()
    public void RUN_37_LEDGERSTATUSUPDATEJOB(@Optional("") String state) {
        JobUtils.executeJob(ledgerStatusUpdateJob);
    }
}
