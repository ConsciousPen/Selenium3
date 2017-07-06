/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.workflow.processmanagement;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.controls.ClickComboBox;

import aaa.admin.metadata.workflow.WorkFlowMetadata;
import aaa.admin.metadata.workflow.WorkFlowMetadata.CreateManualTaskDefinitionTab;
import aaa.admin.metadata.workflow.WorkFlowMetadata.TaskSearchByField;
import aaa.admin.modules.workflow.processmanagement.ITask;
import aaa.admin.modules.workflow.processmanagement.TaskType;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Nikita Bespalov
 * @name Test Manual Task Visibility On Type Switching
 * @scenario
 * 1. Add new Manual Task Definition in Admin application (Type = Account)
 * 2. Edit created Task Definition (change Type to Policy)
 * 3. Open Main - MyWork - Create Task screen
 * 4. Check that created Task Definition unavailable for Type = Account
 * 5. Check that created Task Definition available for Type = Policy
 * @details
 */
public class TestManualTaskVisibilityOnTypeSwitching extends BaseTest {

    private TaskType taskType = TaskType.PROCESS_MANAGEMENT;
    private ITask task = taskType.get();

    private TestData tdTaskDefinition = testDataManager.task.get(taskType);

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testManualTaskVisibilityOnTypeSwitching() {

        adminApp().open();

        log.info("TEST: create 'Manual Task Definition' entity");
        String entityType = tdTaskDefinition.getValue("DataGather", "TestData", WorkFlowMetadata.CreateManualTaskDefinitionTab.class.getSimpleName(), CreateManualTaskDefinitionTab.ENTITY_TYPE.getLabel());
        String taskName = tdTaskDefinition.getValue("DataGather", "TestData", WorkFlowMetadata.CreateManualTaskDefinitionTab.class.getSimpleName(), CreateManualTaskDefinitionTab.TASK_NAME.getLabel());

        task.create(tdTaskDefinition.getTestData("DataGather", "TestData"));

        log.info("TEST: search and update created 'Manual Task Definition' entity");
        task.search(tdTaskDefinition.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                TaskSearchByField.class.getSimpleName(), TaskSearchByField.TASK_NAME.getLabel()), taskName));

        task.update(tdTaskDefinition.getTestData("Update", "TestData"));

        mainApp().open();

        MyWorkSummaryPage.buttonCreateTask.click();
        MyWorkSummaryPage.assetListCreateTask.getControl(MyWorkMetaData.CreateTaskActionTab.TYPE.getLabel(), ClickComboBox.class).setValue(entityType);
        MyWorkSummaryPage.assetListCreateTask.getControl(MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel(), ClickComboBox.class).verify.noOption(taskName);

        MyWorkSummaryPage.assetListCreateTask.getControl(MyWorkMetaData.CreateTaskActionTab.TYPE.getLabel(), ClickComboBox.class).setValue(
                tdTaskDefinition.getValue("Update", "TestData", WorkFlowMetadata.CreateManualTaskDefinitionTab.class.getSimpleName(),
                        CreateManualTaskDefinitionTab.ENTITY_TYPE.getLabel()));
        MyWorkSummaryPage.assetListCreateTask.getControl(MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel(), ClickComboBox.class).verify.option(taskName);
    }
}
