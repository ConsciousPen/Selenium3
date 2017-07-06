/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import aaa.main.enums.MyWorkConstants;
import aaa.main.metadata.MyWorkMetaData;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class MyWorkSummaryPage extends SummaryPage {

    //TODO, needs to be moved or tests that uses it needs to be updated
    public static AssetList assetListCreateTask = new AssetList(By.id("taskCreateForm"), MyWorkMetaData.CreateTaskActionTab.class);

    public static Table tableTasks = new Table(By.xpath("//div[@id='taskListForm:customTaskTable:workTasks']//table")).applyConfiguration("NoRecordsFound");
    public static Table tableTaskDetails = new Table(By.xpath("//span[@id='taskListForm:customTaskTable']//table[@class='taskDetailsTable']"));

    public static Button buttonShowFilter = new Button(By.id("taskManagingForm:filterDisplayBtn"));
    public static Button buttonFilterGo = new Button(By.id("filterContainerForm:filterGoBtn"));

    public static Button buttonAssign = new Button(By.id("taskListForm:bulkTaskAssign"));
    public static Button buttonComplete = new Button(By.id("taskListForm:bulkTaskComplete"));
    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));

    public static Link linkAllQueues = new Link(By.xpath("//a[@href='#taskViewContainerForm:taskViewTabs:allQueuesTasksViewService']"));
    public static Link linkMyInbox = new Link(By.xpath("//a[@href='#taskViewContainerForm:taskViewTabs:myInboxTasksViewService']"));

    private static String linkAssignTemplate = "taskListForm:customTaskTable:workTasks:%s:assignTask";
    private static String linkUpdateTemplate = "taskListForm:customTaskTable:workTasks:%s:updateTask";
    private static String linkCompleteTemplate = "taskListForm:customTaskTable:workTasks:%s:completeTask";

    private static String linkExpandTemplate = "//tbody[@id='taskListForm:customTaskTable:workTasks_data']/tr[%s]/td[2]/div[@class='ui-row-toggler ui-icon ui-icon-circle-triangle-e']";

    public static void selectTask(String nameTask) {
        tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, nameTask).getCell(1).controls.checkBoxes.getFirst().setValue(true);
    }

    public static void selectTask(int rowNumber) {
        tableTasks.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
    }

    public static void expandTask(int rowNumber) {
        new Link(ByT.xpath(linkExpandTemplate).format(rowNumber)).click();
    }

    public static void assignLink(int rowNumber) {
        new Button(ByT.id(linkAssignTemplate).format(rowNumber - 1)).click();
    }

    public static void updateLink(int rowNumber) {
        new Button(ByT.id(linkUpdateTemplate).format(rowNumber - 1)).click();
    }

    public static void completeLink(int rowNumber) {
        new Button(ByT.id(linkCompleteTemplate).format(rowNumber - 1)).click();
    }

    public static void openTaskDetails(String nameTask) {
        tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, nameTask).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).controls.links.getFirst().click();
    }

    public static void openTaskDetails(int rowNumber) {
        tableTasks.getRow(rowNumber).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).controls.links.getFirst().click();
    }

    public static void openReferenceID(int rowNumber) {
        tableTasks.getRow(rowNumber).getCell(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID).controls.links.getFirst().click();
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
