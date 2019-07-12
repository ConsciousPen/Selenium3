//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.exigen.ipb.eisa.controls;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import aaa.main.enums.ActivitiesAndUserNotesConstants;
import toolkit.utils.datetime.DateTimeUtils;
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

    @Override
    public Header getHeader() {
        this.expand();
        return super.getHeader();
    }

    @Override
    public Column getColumn(String columnName) {
        this.expand();
        return super.getColumn(columnName);
    }

    @Override
    public Column getColumn(int columnIndex) {
        this.expand();
        return super.getColumn(columnIndex);
    }

    @Override
    public Row getRow(int rowIndex) {
        this.expand();
        return super.getRow(rowIndex);
    }

    @Override
    public int getRowsCount() {
        this.expand();
        return super.getRowsCount();
    }

    @Override
    public int getColumnsCount() {
        this.expand();
        return super.getColumnsCount();
    }

    @Override
    public Row getRow(String columnName, String cellValueInColumn) {
        this.expand();
        return super.getRow(columnName, cellValueInColumn);
    }

    @Override
    public Row getRowContains(String columnName, String cellValueInColumn) {
        this.expand();
        return super.getRowContains(columnName, cellValueInColumn);
    }

    @Override
    public Row getRow(Integer index, String cellValueInColumn) {
        this.expand();
        return super.getRow(index, cellValueInColumn);
    }

    @Override
    public Row getRowContains(Integer index, String cellValueInColumn) {
        this.expand();
        return super.getRowContains(index, cellValueInColumn);
    }

    @Override
    public Row getRow(Map<String, String> query) {
        this.expand();
        return super.getRow(query);
    }

    @Override
    public Row getRowContains(Map<String, String> query) {
        this.expand();
        return super.getRowContains(query);
    }

    @Override
    public List<Row> getRows(Map<String, String> query) {
        this.expand();
        return super.getRows(query);
    }

    @Override
    public List<Row> getRows() {
        this.expand();
        return super.getRows();
    }

	public class Verify {
        public Verify() {}

        public void description(int rowIndex, String expectedDescription) {
			assertThat(ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION)).hasValue(expectedDescription);
        }

        public void descriptionContains(int rowIndex, String expectedDescription) {
			assertThat(ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION)).valueContains(expectedDescription);
        }

        public void descriptionContains(LocalDateTime date, String expectedDescription) {
            Map<String, String> values = new HashMap<>();
			values.put(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DATE_TIME, date.format(DateTimeUtils.MM_DD_YYYY));
			values.put(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION, expectedDescription);
            assertThat(ActivitiesAndUserNotes.this.getRowContains(values)).exists();
        }

        public void descriptionByRegex(int rowIndex, String expectedDescription) {
			assertThat(ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION)).valueMatches(expectedDescription);
        }

        public void status(int rowIndex, String expectedStatus) {
			assertThat(ActivitiesAndUserNotes.this.getRow(rowIndex).getCell(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.STATUS)).hasValue(expectedStatus);
        }

        public void descriptionExist(String expectedDescription) {
            assertThat(ActivitiesAndUserNotes.this.getColumn("Description").getValue()).as("Description contains record " + expectedDescription).contains(expectedDescription);
        }

        public void descriptionNotExist(String expectedDescription) {
            assertThat(ActivitiesAndUserNotes.this.getColumn("Description").getValue()).as("Description doesn't contains record " + expectedDescription).doesNotContain(expectedDescription);
        }

        public void descriptionExist(String expectedDescription, int expectedCount) {
            assertThat(Collections.frequency(ActivitiesAndUserNotes.this.getColumn("Description").getValue(), expectedDescription))
                    .as("Description contains record " + expectedDescription).isEqualTo(expectedCount);
        }
    }
}
