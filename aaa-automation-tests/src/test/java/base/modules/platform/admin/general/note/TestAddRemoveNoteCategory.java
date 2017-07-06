/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.general.note;

import org.testng.annotations.Test;

import aaa.admin.metadata.general.GeneralMetaData;
import aaa.admin.metadata.general.GeneralMetaData.AddNoteCategory;
import aaa.admin.metadata.general.GeneralMetaData.NoteSearchByField;
import aaa.admin.modules.general.note.INote;
import aaa.admin.modules.general.note.NoteType;
import aaa.admin.pages.general.NotePage;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.AdminConstants;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.customer.ICustomer;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Nikita Bespalov
 * @name Test Add/Remove Note Category
 * @scenario
 * 1. Add new Note Category in Admin application
 * 2. Check that new category can be selected on Customer-Account Notes/Alerts screen
 * 3. Delete created Note Category from Admin application
 * 4. Check that new category cannot be selected on Customer-Account Notes/Alerts screen
 * @details
 */
public class TestAddRemoveNoteCategory extends BaseTest {

    private CustomerType customerType = CustomerType.INDIVIDUAL;
    private TestData tdCustomer = testDataManager.customer.get(customerType);
    private ICustomer customer = customerType.get();

    private NoteType noteType = NoteType.NOTE;
    private INote note = noteType.get();

    private TestData tdNote = testDataManager.note.get(noteType);

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testAddRemoveNoteCategory() {

        adminApp().open();

        log.info("TEST: create 'Note Category' entity");
        String noteTitle = tdNote.getValue("DataGather", "TestData", GeneralMetaData.AddNoteCategory.class.getSimpleName(),
                AddNoteCategory.NEW_CATEGORY.getLabel());

        note.create(tdNote.getTestData("DataGather", "TestData"));

        tdNote.removeAdjustment(TestData.makeKeyPath("DataGather", "TestData", GeneralMetaData.AddNoteCategory.class.getSimpleName(),
                AddNoteCategory.NEW_CATEGORY.getLabel()));

        log.info("TEST: search and verify created 'Note Category' entity");
        note.search(tdNote.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                NoteSearchByField.class.getSimpleName(), NoteSearchByField.CATEGORY.getLabel()), noteTitle));

        NotePage.tableSearchResults.verify.rowsCount(1);

        mainApp().open();

        customer.create(tdCustomer.getTestData("DataGather", "TestData"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        NotesAndAlertsSummaryPage.open();
        NotesAndAlertsSummaryPage.tableFilterNoteOrAlert.getRow(1).getCell(AdminConstants.AdminFilterNoteOrAlertTable.CATEGORY).controls.comboBoxes.getFirst().verify.option(noteTitle);

        adminApp().open();

        note.navigate();

        log.info("TEST: delete 'Note Category' entity");
        note.search(tdNote.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                NoteSearchByField.class.getSimpleName(), NoteSearchByField.CATEGORY.getLabel()), noteTitle));
        note.delete().perform(NotePage.tableSearchResults.getRowsCount());

        mainApp().open();

        MainPage.QuickSearch.search(customerNumber);

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        NotesAndAlertsSummaryPage.open();
        NotesAndAlertsSummaryPage.tableFilterNoteOrAlert.getRow(1).getCell(AdminConstants.AdminFilterNoteOrAlertTable.CATEGORY).controls.comboBoxes.getFirst().verify.noOption(noteTitle);
    }
}
