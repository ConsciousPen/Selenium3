//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.exigen.ipb.etcsa.controls;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.*;

import aaa.main.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable;
import org.openqa.selenium.By;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Column;
import toolkit.webdriver.controls.composite.table.Header;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

public class ActivitiesAndUserNotes extends Table {
    public ActivitiesAndUserNotes.Verify verify = new ActivitiesAndUserNotes.Verify();
    private Link linkOpen = new Link(By.xpath("//div[@id='activityCommandsForm:activitiesAlertPlaceholderTogglePanel:header']//td[@class='rf-cp-lbl']"));
    private Link linkClose = new Link(By.xpath("//div[@id='activityCommandsForm:activitiesAlertPlaceholderTogglePanel:header']//td[@class='rf-cp-lbl']"));

    public ActivitiesAndUserNotes(By locator) {
        super(locator);
    }

    public void expand() {
        if (!this.isOpened()) {
            this.linkOpen.click();
        }

    }

    public void collapse() {
        if (this.isOpened()) {
            this.linkClose.click();
        }

    }

    public boolean isOpened() {
        return this.isPresent() && this.isVisible();
    }

    public Header getHeader() {
        this.expand();
        return super.getHeader();
    }

    public Column getColumn(String columnName) {
        this.expand();
        return super.getColumn(columnName);
    }

    public Column getColumn(int columnIndex) {
        this.expand();
        return super.getColumn(columnIndex);
    }

    public Row getRow(int rowIndex) {
        this.expand();
        return super.getRow(rowIndex);
    }

    public int getRowsCount() {
        this.expand();
        return super.getRowsCount();
    }

    public int getColumnsCount() {
        this.expand();
        return super.getColumnsCount();
    }

    public Row getRow(String columnName, String cellValueInColumn) {
        this.expand();
        return super.getRow(columnName, cellValueInColumn);
    }

    public Row getRowContains(String columnName, String cellValueInColumn) {
        this.expand();
        return super.getRowContains(columnName, cellValueInColumn);
    }

    public Row getRow(Integer index, String cellValueInColumn) {
        this.expand();
        return super.getRow(index, cellValueInColumn);
    }

    public Row getRowContains(Integer index, String cellValueInColumn) {
        this.expand();
        return super.getRowContains(index, cellValueInColumn);
    }

    public Row getRow(Map<String, String> query) {
        this.expand();
        return super.getRow(query);
    }

    public Row getRowContains(Map<String, String> query) {
        this.expand();
        return super.getRowContains(query);
    }

    public List<Row> getRows(Map<String, String> query) {
        this.expand();
        return super.getRows(query);
    }

    public List<Row> getRows() {
        this.expand();
        return super.getRows();
    }

    public class Verify extends toolkit.webdriver.controls.composite.table.Table.Verify {
        public Verify() {}

        public void description(int rowIndex, String expectedDescription) {
            assertThat(ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesTable.DESCRIPTION)).hasValue(expectedDescription);
        }

        public void descriptionContains(int rowIndex, String expectedDescription) {
            ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesTable.DESCRIPTION).verify.contains(expectedDescription);
        }

        public void descriptionContains(LocalDateTime date, String expectedDescription) {
            Map<String, String> values = new HashMap<>();
            values.put(ActivitiesAndUserNotesTable.DATE_TIME, date.format(DateTimeUtils.MM_DD_YYYY));
            values.put(ActivitiesAndUserNotesTable.DESCRIPTION, expectedDescription);
            assertThat(ActivitiesAndUserNotes.this.getRowContains(values)).exists();
        }

        public void descriptionByRegex(int rowIndex, String expectedDescription) {
            ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesTable.DESCRIPTION).verify.valueByRegex(expectedDescription);
        }

        public void status(int rowIndex, String expectedStatus) {
            assertThat(ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesTable.STATUS)).hasValue(expectedStatus);
        }

        public void descriptionExist(String expectedDescription) {
            CustomAssert.assertTrue("Description doesn't contains record " + expectedDescription, ((List<String>)ActivitiesAndUserNotes.this.getColumn("Description").getValue()).contains(expectedDescription));
        }

        public void descriptionNotExist(String expectedDescription) {
            CustomAssert.assertFalse("Description contains record " + expectedDescription, ((List<String>)ActivitiesAndUserNotes.this.getColumn("Description").getValue()).contains(expectedDescription));
        }

        public void descriptionExist(String expectedDescription, int expectedCount) {
            CustomAssert.assertEquals((long)expectedCount, (long)Collections.frequency((Collection<String>)ActivitiesAndUserNotes.this.getColumn("Description").getValue(), expectedDescription));
        }
    }
}
