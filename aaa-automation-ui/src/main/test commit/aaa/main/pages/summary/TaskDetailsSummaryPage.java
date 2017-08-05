/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import java.util.Collections;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.ActivitiesAndUserNotes;

import aaa.main.enums.MyWorkConstants;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class TaskDetailsSummaryPage extends SummaryPage {

    public static Button buttonAssign = new Button(By.id("taskDetailsCommandForm:assignTask_footer"));
    public static Button buttonComplete = new Button(By.id("taskDetailsCommandForm:completeTask_footer"));
    public static Button buttonUpdate = new Button(By.id("taskDetailsCommandForm:updateTask_footer"));
    public static Button buttonCancel = new Button(By.id("taskDetailsCommandForm:navigateBack_footer"));

    //HEADER
    public static StaticElement taskName = new StaticElement(By.xpath("//span[@id='taskHeaderForm:taskName']"));
    public static Link referanceId = new Link(By.xpath("//a[@id='taskHeaderForm:showEntityDetails']"));

    public static Link customerName = new Link(By.xpath("//span[@id='taskHeaderForm:customerOutputGroup']"));
    public static StaticElement workflow = new StaticElement(By.id("taskHeaderForm:taskProcessInstanceId"));
    public static StaticElement type = new StaticElement(By.id("taskHeaderForm:entityTypeOutput"));
    public static StaticElement taskID = new StaticElement(By.id("taskHeaderForm:taskIdOutput"));
    public static StaticElement taskStatus = new StaticElement(By.id("taskHeaderForm:status"));
    public static StaticElement priority = new StaticElement(By.id("taskHeaderForm:priority"));
    public static StaticElement queueName = new StaticElement(By.id("taskHeaderForm:workQueue"));
    public static StaticElement creationDate = new StaticElement(By.id("taskHeaderForm:creationDate"));
    public static StaticElement warningDate = new StaticElement(By.id("taskHeaderForm:warnDate"));
    public static StaticElement dueDate = new StaticElement(By.id("taskHeaderForm:dueDate"));
    public static StaticElement lastPerformer = new StaticElement(By.id("taskHeaderForm:lastPerformer"));
    public static StaticElement createdBy = new StaticElement(By.id("taskHeaderForm:createUser"));
    public static StaticElement assignedTo = new StaticElement(By.id("taskHeaderForm:assignedTo"));
    public static StaticElement completedBy = new StaticElement(By.id("taskHeaderForm:workCompletedBy"));

    //Process Notes
    public static ProcessNotes processNotes = new ProcessNotes(By.xpath("//div[@id='processNotesForm:processNotesTable']//table"));

    //Reference Information
    public static StaticElement taskDescription = new StaticElement(By.id("referenceInfoForm:taskReferenceInfo_1:0:taskDescription"));

    public static class ProcessNotes extends ActivitiesAndUserNotes {

        public Verify verify = this.new Verify();

        private Link linkOpen = new Link(By.xpath("//div[@id='processNotesForm:processNotesTogglePanel:header']//td[@class='rf-cp-lbl']"));

        public ProcessNotes(By locator) {
            super(locator);
        }

        public void expand() {
            if (!isOpened()) {
                linkOpen.click();
            }
        }

        public void collapse() {
            if (isOpened()) {
                linkOpen.click();
            }
        }

        public boolean isOpened() {
            return this.isPresent() && this.isVisible();
        }

        public class Verify extends Table.Verify {
            public void taskId(int rowIndex, String expectedtaskId) {
                getRow(rowIndex).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).verify.value(expectedtaskId);
            }

            public void taskName(int rowIndex, String expectedtaskName) {
                String temp = getRow(rowIndex).getCell(MyWorkConstants.MyWorkTasksTable.TASK_NAME).getValue();

                if (temp.endsWith("...")) {
                    CustomAssert.assertTrue(expectedtaskName.startsWith(temp.replace("...", "")));
                } else {
                    CustomAssert.assertEquals(expectedtaskName, temp);
                }
            }

            public void description(int rowIndex, String expectedDescription) {
                getRow(rowIndex).getCell(MyWorkConstants.MyWorkTasksTable.NOTE_DESCRIPTION).verify.value(expectedDescription);
            }

            public void descriptionContains(int rowIndex, String expectedDescription) {
                getRow(rowIndex).getCell(MyWorkConstants.MyWorkTasksTable.NOTE_DESCRIPTION).verify.contains(expectedDescription);
            }

            public void descriptionByRegex(int rowIndex, String expectedDescription) {
                getRow(rowIndex).getCell(MyWorkConstants.MyWorkTasksTable.NOTE_DESCRIPTION).verify.valueByRegex(expectedDescription);
            }

            public void descriptionExist(String expectedDescription) {
                CustomAssert.assertTrue("Description doesn't contains record " + expectedDescription,
                        getColumn(MyWorkConstants.MyWorkTasksTable.NOTE_DESCRIPTION).getValue().contains(expectedDescription));
            }

            public void descriptionExist(String expectedDescription, int expectedCount) {
                CustomAssert.assertEquals(expectedCount, Collections.frequency(getColumn(MyWorkConstants.MyWorkTasksTable.NOTE_DESCRIPTION).getValue(), expectedDescription));
            }
        }
    }

}
