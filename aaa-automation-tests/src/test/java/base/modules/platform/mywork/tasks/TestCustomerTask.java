/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.mywork.tasks;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage.QuickSearch;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.defaulttabs.MyWorkTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olegs Tolstovs
 * @name Test for Customer Task
 * @scenario
 * 1. Create Individual Customer
 * 2. Create Customer Task linked with Customer (Reference ID = Customer Number)
 * 3. Filter tasks on MyWork and verify task details
 * 4. Open task and verify task details
 * 5. Initiate task update and cancel update
 * 6. Verify task details after cancelled update
 * 7. Initiate update and update some task fields
 * 8. Verify updated task details after task update
 * 9. Assign task to QA user
 * 10. Verify updated task details after task assignment
 * 11. Open Customer consolidated view and check task details
 * 12. Assign task to General queue from consolidated view
 * 13. Complete task from consolidated view
 * 14. Check completed task details in MyWork page
 * 15. Create new task from consolidated view
 * @details
 */
public class TestCustomerTask extends TaskBaseTest {

    String testData = "TestData_Customer";

    @Test(groups = {"All_CreateTask", "7.2_All_UC_ViewTaskDetails", "7.2_All_UC_UpdateTask", "All_UC_AssignTask", "7.2_All_UC_TaskConsolidatedView"})
    @TestInfo(component = "Platform.MyWork")
    public void test_customerTask() {
        mainApp().open();

        String user = Tab.labelLoggedUser.getValue();

        customer.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Customer Created: " + customerNumber);

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        //Task creation
        TestData customerData = prepareData(customerNumber, testData);

        String taskID = taskCreation(customerData, true);

        log.info("TEST: Created Customer Task with ID: " + taskID);

        //View task details
        verifyTask(user, customerName, taskID, "General", customerData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyTaskDetails(user, customerName, taskID, "General", customerData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Update task cancelled
        updateCanceled(testData);

        verifyUpdateCanceledTaskDetails(user, customerName, taskID, "General", customerData);

        TaskDetailsSummaryPage.buttonCancel.click();

        verifyTask(user, customerName, taskID, "General", customerData);

        //Update task
        customerData = updateTaskAndData(customerData, testData);

        verifyTask(user, customerName, taskID, "General", customerData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyUpdatedTaskDetails(user, customerName, taskID, "General", customerData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Assign task from MyWork
        customerData = assignTaskAndUpdateData(customerData, "TestData_QA", true, false);

        verifyTask(user, customerName, taskID, customerData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyAssignedTaskDetails(user, customerName, taskID, customerData, " to qa assignee from General queue");

        TaskDetailsSummaryPage.buttonCancel.click();

        //Task Consolidated view
        QuickSearch.search(customerNumber);

        CustomerSummaryPage.buttonTasks.click();

        MyWorkSummaryPage.openMyInboxSection();

        verifyTask(user, customerName, taskID, customerData);

        //Assign task from Customer consolidated view from Task Details
        customerData = assignTaskAndUpdateData(customerData, "TestData_General", false, true);

        verifyTask(user, customerName, taskID, "General", customerData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyAssignedTaskDetails(user, customerName, taskID, "General", customerData, " to General queue from qa assignee");

        TaskDetailsSummaryPage.buttonCancel.click();

        MyWorkSummaryPage.openAllQueuesSection();

        //Complete task from Customer consolidated view
        customerData = completeTaskAndUpdateData(customerData, "TestData_Complete");

        //Go to MyWork Page and validate task Completed task details
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());

        filterTaskSeveralCriteria(true, MyWorkMetaData.FilterTaskActionTab.STATUS.getLabel(), "Completed", MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID.getLabel(), customerNumber);

        verifyTask(user, customerName, taskID, "General", customerData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyCompletedTaskDetails(user, customerName, taskID, "General", customerData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Create new task from Customer consolidated view
        MyWorkSummaryPage.openReferenceID(1);

        String taskID_2 = taskCreationFromView(customerName, customerData, true);

        verifyTask(user, customerName, taskID_2, "General", customerData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyTaskDetails(user, customerName, taskID_2, "General", customerData);

        TaskDetailsSummaryPage.buttonCancel.click();

        MyWorkTab.buttonTopCancel.click();

        CustomerSummaryPage.labelCustomerNumber.isVisible();

    }

}
