/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.mywork;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.MyWorkConstants;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.IMyWork;
import aaa.main.modules.mywork.MyWorkType;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Andrey Shashenka
 * @name Test for Task Workflow (Create, Update, Assign and Complete)
 * @scenario
 * 1. Create Individual Customer
 * 2. Create Auto Quote
 * 3. Create Task linked with Quote (Reference ID = Quote Number)
 * 4. Filter tasks on MyWork and verify created Task
 * 5. Update Warning/Due Date for this Task
 * 6. Assign Task to General Queue
 * 7. Complete Task
 * @details
 */

public class TestTaskWorkflow extends BaseTest {

    private PolicyType policyType = PolicyType.AUTO_SS;
    private IPolicy policy = policyType.get();
    private TestData tdPolicy = testDataManager.policy.get(policyType);

    private MyWorkType myWorkType = MyWorkType.MY_WORK;
    private IMyWork myWork = myWorkType.get();

    private TestData tdMyWork = testDataManager.myWork.get(myWorkType);

    @Test(groups = {"All_CreateTask"})
    @TestInfo(component = "Platform.MyWork")
    public void testTeamMergeTasks() {
        mainApp().open();

        createCustomerIndividual();

        policy.createQuote(tdPolicy.getTestData("DataGather", "TestData"));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Create Task for Quote #" + policyNumber);

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());

        myWork.createTask().perform(tdMyWork.getTestData("CreateTask", "TestData").adjust(
                TestData.makeKeyPath(MyWorkMetaData.CreateTaskActionTab.class.getSimpleName(),
                        MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel()), policyNumber));

        myWork.filterTask().perform(tdMyWork.getTestData("FilterTask", "TestData").adjust(
                TestData.makeKeyPath(MyWorkMetaData.FilterTaskActionTab.class.getSimpleName(),
                        MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID.getLabel()), policyNumber));

        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).verify.value(policyNumber);

        String taskId = MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).getValue();

        log.info("TEST: Created Task with ID: " + taskId);

        log.info("TEST: Update Task with ID: " + taskId);
        myWork.updateTask().perform(1, tdMyWork.getTestData("UpdateTask", "TestData"));

        DateTime warningDate = new DateTime(tdMyWork.getTestData("UpdateTask", "TestData").getValue(
                MyWorkMetaData.UpdateTaskActionTab.class.getSimpleName(),
                MyWorkMetaData.UpdateTaskActionTab.WARNING_DATE_TIME.getLabel()), DateTime.MM_DD_YYYY);
        DateTime dueDate = new DateTime(tdMyWork.getTestData("UpdateTask", "TestData").getValue(
                MyWorkMetaData.UpdateTaskActionTab.class.getSimpleName(),
                MyWorkMetaData.UpdateTaskActionTab.DUE_DATE_TIME.getLabel()), DateTime.MM_DD_YYYY);

        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.WARNING_DATE).verify.contains(warningDate.toString());
        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.DUE_DATE).verify.contains(dueDate.toString());

        log.info("TEST: Assign to General Queue Task with ID: " + taskId);
        myWork.assignTaskTo().perform(1, tdMyWork.getTestData("AssignTaskTo", "TestData"));

        MyWorkSummaryPage.openAllQueuesSection();

        myWork.filterTask().perform(tdMyWork.getTestData("FilterTask", "TestData").adjust(
                TestData.makeKeyPath(MyWorkMetaData.FilterTaskActionTab.class.getSimpleName(),
                        MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID.getLabel()), policyNumber));

        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.QUEUE).verify.value(
                tdMyWork.getTestData("AssignTaskTo", "TestData").getValue(
                        MyWorkMetaData.AssignTaskToActionTab.class.getSimpleName(),
                        MyWorkMetaData.AssignTaskToActionTab.QUEUE.getLabel()));

        log.info("TEST: Complete Task with ID: " + taskId);
        myWork.completeTask().perform(1, tdMyWork.getTestData("CompleteTask", "TestData"));

        MyWorkSummaryPage.tableTasks.getRow(1).getCell(1).verify.value("No records found.");
    }

}
