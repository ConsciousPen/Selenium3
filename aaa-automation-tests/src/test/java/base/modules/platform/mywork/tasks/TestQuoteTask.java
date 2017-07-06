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
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olegs Tolstovs
 * @name Test for Quote Task
 * @scenario
 * 1. Create Quote
 * 2. Create Quote Task linked with Quote (Reference ID = Quote Number)
 * 3. Filter tasks on MyWork and verify task details
 * 4. Open task and verify task details
 * 5. Initiate task update and cancel update
 * 6. Verify task details after cancelled update
 * 7. Initiate update and update some task fields
 * 8. Verify updated task details after task update
 * 9. Assign task to General queue
 * 10. Verify updated task details after task assignment
 * 11. Open Quote consolidated view and check task details
 * 12. Assign task to QA user from consolidated view
 * 13. Complete task from consolidated view
 * 14. Check completed task details in MyWork page
 * 15. Create new task from consolidated view
 * @details
 */
public class TestQuoteTask extends TaskBaseTest {

    String testData = "TestData_Quote";

    @Test(groups = {"All_CreateTask", "7.2_All_UC_ViewTaskDetails", "7.2_All_UC_UpdateTask", "All_UC_AssignTask", "7.2_All_UC_TaskConsolidatedView"})
    @TestInfo(component = "Platform.MyWork")
    public void test_1_CreateQuoteTask() {
        mainApp().open();

        String user = Tab.labelLoggedUser.getValue();

        createCustomerIndividual();

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        createQuote();

        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Quote Created: " + quoteNumber);

        //Task creation
        TestData quoteData = prepareData(quoteNumber, testData);

        String taskID = taskCreation(quoteData);

        log.info("TEST: Created Quote Task with ID: " + taskID);

        //View task details
        verifyTask(user, customerName, taskID, quoteData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyTaskDetails(user, customerName, taskID, quoteData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Update task cancelled
        updateCanceled(testData);

        verifyUpdateCanceledTaskDetails(user, customerName, taskID, quoteData);

        TaskDetailsSummaryPage.buttonCancel.click();

        verifyTask(user, customerName, taskID, quoteData);

        //Update task
        quoteData = updateTaskAndData(quoteData, testData);

        verifyTask(user, customerName, taskID, quoteData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyUpdatedTaskDetails(user, customerName, taskID, quoteData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Assign task from MyWork
        quoteData = assignTaskAndUpdateData(quoteData, "TestData_General", false, false);

        verifyTask(user, customerName, taskID, "General", quoteData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyAssignedTaskDetails(user, customerName, taskID, "General", quoteData, " to General queue from qa assignee");

        TaskDetailsSummaryPage.buttonCancel.click();

        //Task Consolidated view
        QuickSearch.search(quoteNumber);

        PolicySummaryPage.buttonTasks.click();

        MyWorkSummaryPage.openAllQueuesSection();

        verifyTask(user, customerName, taskID, "General", quoteData);

        //Assign task from Quote consolidated view from Task Details
        quoteData = assignTaskAndUpdateData(quoteData, "TestData_QA", true, true);

        verifyTask(user, customerName, taskID, quoteData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyAssignedTaskDetails(user, customerName, taskID, quoteData, " to qa assignee from General queue");

        TaskDetailsSummaryPage.buttonCancel.click();

        MyWorkSummaryPage.openMyInboxSection();

        //Complete task from Quote consolidated view
        quoteData = completeTaskAndUpdateData(quoteData, "TestData_Complete");

        //Go to MyWork Page and validate task Completed task details
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());

        filterTaskSeveralCriteria(false, MyWorkMetaData.FilterTaskActionTab.STATUS.getLabel(), "Completed", MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID.getLabel(), quoteNumber);

        verifyTask(user, customerName, taskID, quoteData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyCompletedTaskDetails(user, customerName, taskID, quoteData);

        TaskDetailsSummaryPage.buttonCancel.click();

        //Create new task from Quote consolidated view
        MyWorkSummaryPage.openReferenceID(1);

        String taskID_2 = taskCreationFromView(customerName, quoteData, false);

        verifyTask(user, customerName, taskID_2, quoteData);

        MyWorkSummaryPage.openTaskDetails(1);

        verifyTaskDetails(user, customerName, taskID_2, quoteData);

        TaskDetailsSummaryPage.buttonCancel.click();

        MyWorkTab.buttonTopCancel.click();

        PolicySummaryPage.labelPolicyNumber.isVisible();
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

}
