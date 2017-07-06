/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.mywork.tasks;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.mywork.MyWorkTasksVerifier;
import aaa.main.enums.MyWorkConstants;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.IMyWork;
import aaa.main.modules.mywork.MyWorkType;
import aaa.main.modules.mywork.actiontabs.CreateTaskActionTab;
import aaa.main.modules.mywork.actiontabs.UpdateTaskActionTab;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class TaskBaseTest extends BaseTest {

    private MyWorkType myWorkType = MyWorkType.MY_WORK;
    private IMyWork myWork = myWorkType.get();
    private TestData tdMyWork = testDataManager.myWork.get(myWorkType);

    public String taskCreation(TestData testData) {
        return taskCreation(testData, false);
    }

    public String taskCreation(TestData testData, Boolean all) {

        String referenceId = testData.getValue(MyWorkMetaData.CreateTaskActionTab.class.getSimpleName(),
                MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel());

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());

        myWork.createTask().perform(testData);

        filterTask(MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID.getLabel(), referenceId, all);

        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).verify.value(referenceId);

        return MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).getValue();
    }

    public String taskCreationFromView(String customerName, TestData testData, Boolean all) {

        String referenceId = testData.getValue(MyWorkMetaData.CreateTaskActionTab.class.getSimpleName(),
                MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel());

        CustomAssert.enableSoftMode();

        CreateTaskActionTab.buttonTasks.click();

        myWork.createTask().start();

        Tab createTaskActionTab = myWork.createTask().getView().getTab(CreateTaskActionTab.class);

        createTaskActionTab.verifyFieldHasValue(MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel(), referenceId);
        createTaskActionTab.verifyFieldHasValue(MyWorkMetaData.CreateTaskActionTab.CUSTOMER.getLabel(), customerName);

        myWork.createTask().getView().fill(testData);
        myWork.createTask().submit();

        if (all) {
            MyWorkSummaryPage.openAllQueuesSection();
        } else {
            MyWorkSummaryPage.openMyInboxSection();
        }

        MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).verify.value(referenceId);

        CustomAssert.disableSoftMode();

        return MyWorkSummaryPage.tableTasks.getRow(1).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).getValue();
    }

    public void filterTask(String key, String value, Boolean all) {
        myWork.filterTask().perform(tdMyWork.getTestData("FilterTask", "TestData").adjust(
                TestData.makeKeyPath(MyWorkMetaData.FilterTaskActionTab.class.getSimpleName(), key), value));

        if (all) {
            MyWorkSummaryPage.openAllQueuesSection();
        } else {
            MyWorkSummaryPage.openMyInboxSection();
        }
    }
    
    /**
     * Used when a several criteria are required for Task filter
     * parameter keysValues must include the both key and value: key, value, key, value, key, value ...
     */
    public void filterTaskSeveralCriteria(Boolean all, String... keysValues) {
    
    	for (int i=0; i<keysValues.length-1; i++ ) {
    		myWork.filterTask().perform(tdMyWork.getTestData("FilterTask", "TestData_empty").adjust(
    				TestData.makeKeyPath(MyWorkMetaData.FilterTaskActionTab.class.getSimpleName(), keysValues[i]), keysValues[i+1]));
    	}
        if (all) {
            MyWorkSummaryPage.openAllQueuesSection();
        } else {
            MyWorkSummaryPage.openMyInboxSection();
        }
    }

    public void verifyTask(String user, String customerName, String taskID, TestData testData) {
        verifyTask(user, customerName, taskID, "", testData);
    }

    public void verifyTask(String user, String customerName, String taskID, String queue, TestData testData) {

        CustomAssert.enableSoftMode();

        new MyWorkTasksVerifier()
                .setTaskName(testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()))
                .setPriority(testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.PRIORITY.getLabel()))
                .setWarningDate(testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.WARNING_DATE_TIME.getLabel()))
                .setDueDate(testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.DUE_DATE_TIME.getLabel()))
                .setReferenceID(testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel()))
                .setCustomer(customerName)
                .setTaskID(taskID)
                .setLastPerformer(user)
                .setQueue(queue)
                .verify(1);

        CustomAssert.disableSoftMode();
    }

    public void verifyTaskDetails(String user, String customerName, String taskID, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Active", "", testData, "Task Created ", "");
    }

    public void verifyTaskDetails(String user, String customerName, String taskID, String queue, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Active", queue, testData, "Task Created ", "");
    }

    public void verifyUpdateCanceledTaskDetails(String user, String customerName, String taskID, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Active", "", testData, "Task Created ", "");

        CustomAssert.enableSoftMode();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1,
                "Update Task " + testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()) + ", " + taskID);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Canceled");

        CustomAssert.disableSoftMode();
    }

    public void verifyUpdateCanceledTaskDetails(String user, String customerName, String taskID, String queue, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Active", queue, testData, "Task Created ", "");

        CustomAssert.enableSoftMode();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1,
                "Update Task " + testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()) + ", " + taskID);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Canceled");

        CustomAssert.disableSoftMode();
    }

    public void verifyAssignedTaskDetails(String user, String customerName, String taskID, TestData testData, String activitiesPostfix) {
        verifyTaskDetails(user, customerName, taskID, "Active", "", testData, "Assign Task ", activitiesPostfix);
    }

    public void verifyAssignedTaskDetails(String user, String customerName, String taskID, String queue, TestData testData, String activitiesPostfix) {
        verifyTaskDetails(user, customerName, taskID, "Active", queue, testData, "Assign Task ", activitiesPostfix);
    }

    public void verifyUpdatedTaskDetails(String user, String customerName, String taskID, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Active", "", testData, "Update Task ", "");
    }

    public void verifyUpdatedTaskDetails(String user, String customerName, String taskID, String queue, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Active", queue, testData, "Update Task ", "");
    }

    public void verifyCompletedTaskDetails(String user, String customerName, String taskID, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Completed", "", testData, "Complete Task ", "");
    }

    public void verifyCompletedTaskDetails(String user, String customerName, String taskID, String queue, TestData testData) {
        verifyTaskDetails(user, customerName, taskID, "Completed", queue, testData, "Complete Task ", "");
    }

    public void verifyTaskDetails(String user, String customerName, String taskID, String status, String queue, TestData testData, String activitiesPrefix, String activitiesPostfix) {

        CustomAssert.enableSoftMode();

        TaskDetailsSummaryPage.customerName.verify.value(customerName);
        TaskDetailsSummaryPage.taskName.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()));
        TaskDetailsSummaryPage.referanceId.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel()));
        TaskDetailsSummaryPage.type.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TYPE.getLabel()));
        TaskDetailsSummaryPage.taskID.verify.value(taskID);
        TaskDetailsSummaryPage.taskStatus.verify.value(status);
        TaskDetailsSummaryPage.priority.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.PRIORITY.getLabel()));
        TaskDetailsSummaryPage.queueName.verify.value(queue);
        TaskDetailsSummaryPage.warningDate.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.WARNING_DATE_TIME.getLabel()));
        TaskDetailsSummaryPage.dueDate.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.DUE_DATE_TIME.getLabel()));

        TaskDetailsSummaryPage.lastPerformer.verify.value(user);
        TaskDetailsSummaryPage.createdBy.verify.value(user);
        //TaskDetailsSummaryPage.assignedTo.verify.value(user);

        TaskDetailsSummaryPage.taskDescription.verify.value(
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.DESCRIPTION.getLabel()));

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(MyWorkConstants.MyWorkActivitiesAndUserNotesTable.STATUS, "Committed").getCell(MyWorkConstants.MyWorkActivitiesAndUserNotesTable.DESCRIPTION).verify
                .value(testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.NOTE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(MyWorkConstants.MyWorkActivitiesAndUserNotesTable.STATUS, "Finished").getCell(MyWorkConstants.MyWorkActivitiesAndUserNotesTable.DESCRIPTION).verify.value(
                activitiesPrefix + testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()) + ", " + taskID + activitiesPostfix);

        TaskDetailsSummaryPage.processNotes.expand();
        TaskDetailsSummaryPage.processNotes.verify.taskId(1, taskID);
        TaskDetailsSummaryPage.processNotes.verify.taskName(1,
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()));
        TaskDetailsSummaryPage.processNotes.verify.description(1,
                testData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.NOTE.getLabel()));

        CustomAssert.disableSoftMode();
    }

    public TestData prepareData(String referenceId, String testData) {
        TestData preparedData = tdMyWork.getTestData("CreateTask", testData).adjust(
                TestData.makeKeyPath(MyWorkMetaData.CreateTaskActionTab.class.getSimpleName(),
                        MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel()), referenceId);

        preparedData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.WARNING_DATE_TIME.getLabel());
        preparedData.getValue(CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.DUE_DATE_TIME.getLabel());

        return preparedData;
    }

    public void updateCanceled(String updateData) {
        TestData temp = tdMyWork.getTestData("UpdateTask", updateData);

        myWork.updateTask().start(1);
        myWork.updateTask().getView().fill(temp);

        UpdateTaskActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }

    public TestData updateTaskAndData(TestData creationData, String updateData) {
        TestData temp = tdMyWork.getTestData("UpdateTask", updateData);

        temp.getValue(UpdateTaskActionTab.class.getSimpleName(), MyWorkMetaData.UpdateTaskActionTab.WARNING_DATE_TIME.getLabel());
        temp.getValue(UpdateTaskActionTab.class.getSimpleName(), MyWorkMetaData.UpdateTaskActionTab.DUE_DATE_TIME.getLabel());

        myWork.updateTask().perform(1, temp);

        creationData.adjust(CreateTaskActionTab.class.getSimpleName(),
                creationData.getTestData(CreateTaskActionTab.class.getSimpleName()).adjust(temp.getTestData(UpdateTaskActionTab.class.getSimpleName()).resolveLinks()));

        return creationData;
    }

    public TestData assignTaskAndUpdateData(TestData creationData, String testData, Boolean all, Boolean fromView) {

        TestData temp = tdMyWork.getTestData("AssignTaskTo", testData);

        CustomAssert.enableSoftMode();

        MyWorkSummaryPage.tableTasks.verify.rowsCount(1);

        MyWorkSummaryPage.buttonAssign.verify.enabled(false);
        MyWorkSummaryPage.buttonComplete.verify.enabled(false);

        MyWorkSummaryPage.selectTask(1);

        MyWorkSummaryPage.buttonAssign.verify.enabled();
        MyWorkSummaryPage.buttonComplete.verify.enabled();

        if (fromView) {
            myWork.assignTaskTo().performFromView(1, temp);

            if (all) {
                MyWorkSummaryPage.openAllQueuesSection();
            } else {
                MyWorkSummaryPage.openMyInboxSection();
            }
        } else {
            myWork.assignTaskTo().perform(1, temp);
        }

        MyWorkSummaryPage.tableTasks.verify.rowsCount(0);

        if (!all) {
            MyWorkSummaryPage.openAllQueuesSection();
        } else {
            MyWorkSummaryPage.openMyInboxSection();
        }

        MyWorkSummaryPage.tableTasks.verify.rowsCount(1);

        CustomAssert.disableSoftMode();

        creationData.adjust(TestData.makeKeyPath(MyWorkMetaData.CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.NOTE.getLabel()),
                temp.getValue(MyWorkMetaData.AssignTaskToActionTab.class.getSimpleName(), MyWorkMetaData.AssignTaskToActionTab.NOTE.getLabel()));

        return creationData;

    }

    public TestData completeTaskAndUpdateData(TestData creationData, String completeData) {

        TestData temp = tdMyWork.getTestData("CompleteTask", completeData);

        CustomAssert.enableSoftMode();

        MyWorkSummaryPage.tableTasks.verify.rowsCount(1);

        myWork.completeTask().perform(1, temp);

        MyWorkSummaryPage.openAllQueuesSection();

        MyWorkSummaryPage.tableTasks.verify.rowsCount(0);

        MyWorkSummaryPage.openMyInboxSection();

        MyWorkSummaryPage.tableTasks.verify.rowsCount(0);

        CustomAssert.disableSoftMode();

        creationData.adjust(TestData.makeKeyPath(MyWorkMetaData.CreateTaskActionTab.class.getSimpleName(), MyWorkMetaData.CreateTaskActionTab.NOTE.getLabel()),
                temp.getValue(MyWorkMetaData.CompleteTaskActionTab.class.getSimpleName(), MyWorkMetaData.CompleteTaskActionTab.NOTE.getLabel()));

        return creationData;
    }

}
