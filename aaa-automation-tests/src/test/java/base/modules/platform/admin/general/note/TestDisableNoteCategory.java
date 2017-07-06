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
import aaa.common.pages.NavigationPage;
import aaa.main.enums.AdminConstants;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.customer.ICustomer;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Nikita Bespalov
 * @name Test Disable Note Category
 * @scenario
 * 1. Add new Note Category in Admin application
 * 2. Disable created category
 * 3. Check that new category cannot be selected on Customer-Account Notes/Alerts screen
 * @details
 */
public class TestDisableNoteCategory extends BaseTest {

    private CustomerType customerType = CustomerType.INDIVIDUAL;
    private TestData tdCustomer = testDataManager.customer.get(customerType);
    private ICustomer customer = customerType.get();

    private NoteType noteType = NoteType.NOTE;
    private INote note = noteType.get();

    private TestData tdNote = testDataManager.note.get(noteType);

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testDisableNoteCategory() {

        adminApp().open();

        log.info("TEST: create 'Note Category' entity");
        String noteTitle = tdNote.getValue("DataGather", "TestData", GeneralMetaData.AddNoteCategory.class.getSimpleName(),
                AddNoteCategory.NEW_CATEGORY.getLabel());

        note.create(tdNote.getTestData("DataGather", "TestData"));

        tdNote.removeAdjustment(TestData.makeKeyPath("DataGather", "TestData", GeneralMetaData.AddNoteCategory.class.getSimpleName(),
                AddNoteCategory.NEW_CATEGORY.getLabel()));

        log.info("TEST: search and disable created 'Note Category' entity");
        note.search(tdNote.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                NoteSearchByField.class.getSimpleName(), NoteSearchByField.CATEGORY.getLabel()), noteTitle));
        note.disable().perform(NotePage.tableSearchResults.getRowsCount());

        mainApp().open();

        customer.create(tdCustomer.getTestData("DataGather", "TestData"));

        log.info("TEST: check that disabled category cannot be selected");
        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        NotesAndAlertsSummaryPage.open();
        NotesAndAlertsSummaryPage.tableFilterNoteOrAlert.getRow(1).getCell(AdminConstants.AdminFilterNoteOrAlertTable.CATEGORY).controls.comboBoxes.getFirst().verify.noOption(noteTitle);

        adminApp().open();

        note.navigate();

        log.info("TEST: delete 'Note Category' entity");
        note.search(tdNote.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                NoteSearchByField.class.getSimpleName(), NoteSearchByField.CATEGORY.getLabel()), noteTitle));
        note.delete().perform(NotePage.tableSearchResults.getRowsCount());
    }
}
