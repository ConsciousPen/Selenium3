/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.mywork.tasks;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage.QuickSearch;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.defaulttabs.MyWorkTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olegs Tolstovs
 * @name Test for Claim Task
 * @scenario
 * 1. Open existing customer
 * 2. Open Billing tab and save Billing account number
 * 3. Create Billing Task linked with Billing account (Reference ID = Billing account Number)
 * 4. Filter tasks on MyWork and verify task details
 * 5. Open task and verify task details
 * 6. Initiate task update and cancel update
 * 7. Verify task details after cancelled update
 * 8. Initiate update and update some task fields
 * 9. Verify updated task details after task update
 * 10. Assign task to General queue
 * 11. Verify updated task details after task assignment
 * 12. Open Claim consolidated view and check task details
 * 13. Assign task to QA user from consolidated view
 * 14. Complete task from consolidated view
 * 15. Check completed task details in MyWork page
 * 16. Create new task from consolidated view
 * @details
 */
public class TestBillingTask extends TaskBaseTest {

    String testData = "TestData_Billing";

    @Test(groups = {"All_CreateTask", "7.2_All_UC_ViewTaskDetails", "7.2_All_UC_UpdateTask", "All_UC_AssignTask", "7.2_All_UC_TaskConsolidatedView"})
    @TestInfo(component = "Platform.MyWork")
    public void test_1_CreateBillingTask() {
        mainApp().open();

        String user = Tab.labelLoggedUser.getValue();

        createCustomerIndividual();

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        createPolicy();

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        if (!BillingSummaryPage.tableBillingAccounts.isPresent()) {
            NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        }

        String billingNumber = BillingSummaryPage.tableBillingAccounts.getRow(1).getCell(BillingConstants.BillingAccountsTable.BILLING_ACCOUNT).getValue();

        log.info("TEST: Billing Account Created: " + billingNumber);

        //Task creation
        TestData billingData = prepareData(billingNumber, testData);

        String taskID = taskCreation(billingData);

        log.info("TEST: Created Billing Task with ID: " + taskID);

        //View task details
        verifyTask(user, customerName, taskID, billingData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyTaskDetails(user, customerName, taskID, billingData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Update task cancelled
        updateCanceled(testData);

        verifyUpdateCanceledTaskDetails(user, customerName, taskID, billingData);

        TaskDetailsSummaryPage.buttonCancel.click();

        verifyTask(user, customerName, taskID, billingData);

        //Update task
        billingData = updateTaskAndData(billingData, testData);

        verifyTask(user, customerName, taskID, billingData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyUpdatedTaskDetails(user, customerName, taskID, billingData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Assign task from MyWork
        billingData = assignTaskAndUpdateData(billingData, "TestData_General", false, false);

        verifyTask(user, customerName, taskID, "General", billingData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyAssignedTaskDetails(user, customerName, taskID, "General", billingData, " to General queue from qa assignee");

        TaskDetailsSummaryPage.buttonCancel.click();

        //Task Consolidated view
        QuickSearch.search(billingNumber);

        BillingSummaryPage.buttonTasks.click();

        MyWorkSummaryPage.openAllQueuesSection();

        verifyTask(user, customerName, taskID, "General", billingData);

        //Assign task from Billing consolidated view from Task Details
        billingData = assignTaskAndUpdateData(billingData, "TestData_QA", true, true);

        verifyTask(user, customerName, taskID, billingData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyAssignedTaskDetails(user, customerName, taskID, billingData, " to qa assignee from General queue");

        TaskDetailsSummaryPage.buttonCancel.click();

        MyWorkSummaryPage.openMyInboxSection();

        //Complete task from Billing consolidated view
        billingData = completeTaskAndUpdateData(billingData, "TestData_Complete");

        //Go to MyWork Page and validate task Completed task details
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());

        filterTaskSeveralCriteria(false, MyWorkMetaData.FilterTaskActionTab.STATUS.getLabel(), "Completed", MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID.getLabel(), billingNumber);

        verifyTask(user, customerName, taskID, billingData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyCompletedTaskDetails(user, customerName, taskID, billingData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Create new task from Billing consolidated view
        MyWorkSummaryPage.openReferenceID(1);

        String taskID_2 = taskCreationFromView(customerName, billingData, false);

        verifyTask(user, customerName, taskID_2, billingData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyTaskDetails(user, customerName, taskID_2, billingData);

        TaskDetailsSummaryPage.buttonCancel.click();

        MyWorkTab.buttonTopCancel.click();

        BillingSummaryPage.labelBillingAccountNumber.isVisible();
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS;
    }

}
