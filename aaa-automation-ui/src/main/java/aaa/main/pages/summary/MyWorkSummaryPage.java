/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import aaa.main.enums.MyWorkConstants.MyWorkTasksTable;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class MyWorkSummaryPage extends SummaryPage {

    public static Table tableTasks = new Table(By.xpath("//div[@id='taskListForm:customTaskTable:workTasks']//table")).applyConfiguration("NoRecordsFound");

    public static Button buttonShowFilter = new Button(By.id("taskManagingForm:filterDisplayBtn"));
    public static Button buttonFilterGo = new Button(By.id("filterContainerForm:filterGoBtn"));

    public static Button buttonAssign = new Button(By.id("taskListForm:bulkTaskAssign"));
    public static Button buttonComplete = new Button(By.id("taskListForm:bulkTaskComplete"));
    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));

    public static Link linkAllQueues = new Link(By.xpath("//a[@href='#taskViewContainerForm:taskViewTabs:allQueuesTasksViewService']"));
    public static Link linkMyInbox = new Link(By.xpath("//a[@href='#taskViewContainerForm:taskViewTabs:myInboxTasksViewService']"));
    
    /** Button to exit from ActiveTasks view*/ 
    public static Button buttonCancel = new Button(By.id("topCancelLink"));


    public static void selectTaskById(String taskId) {
        tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId).getCell(1).controls.checkBoxes.getFirst().setValue(true);
    }

    public static void selectTaskByName(String taskName) {
        tableTasks.getRow(MyWorkTasksTable.TASK_NAME, taskName).getCell(1).controls.checkBoxes.getFirst().setValue(true);
    }

    public static void openTaskDetailsByName(String taskName) {
        tableTasks.getRow(MyWorkTasksTable.TASK_NAME, taskName).getCell(MyWorkTasksTable.TASK_ID).controls.links.getFirst().click();
    }

    public static void openTaskDetailsById(String taskId) {
        tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId).getCell(MyWorkTasksTable.TASK_ID).controls.links.getFirst().click();
    }

    public static String getTaskIdNyName(String taskName) {
    	return tableTasks.getRow(MyWorkTasksTable.TASK_NAME, taskName).getCell(MyWorkTasksTable.TASK_ID).getValue();
    }

    public static void showFilter() {
        if (buttonShowFilter.getAttribute("class").contains("collapsable-collapsed")) {
            buttonShowFilter.click();
        }
    }

    public static void submitFilter() {
        buttonFilterGo.click();
    }

    public static void openAllQueuesSection() {
        linkAllQueues.click();
    }

    public static void openMyInboxSection() {
        linkMyInbox.click();
    }
}
