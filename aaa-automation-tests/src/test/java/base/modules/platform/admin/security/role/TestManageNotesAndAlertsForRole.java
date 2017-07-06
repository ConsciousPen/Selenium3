/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.role;

import org.testng.annotations.Test;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.pages.security.RolePage;
import aaa.common.metadata.NotesAndAlertsMetaData;
import aaa.common.metadata.NotesAndAlertsMetaData.NotesAndAlertsTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author N. Belakova
 * @name Test for Viewing BAM Activities for a Role or Manage Notes/Alerts
 * @scenario
 * 1. Open Admin app
 * 2. Create Role
 * 3. Search and Edit created Role
 * 4. Add Note: Verify BAM Note
 * 5. Update Note: Verify BAM Note
 * 6. Update Role
 * 7. Delete Note: Verify BAM Note
 * 8. Add Alert: Verify Alert label, BAM
 * 9. Update Alert: Verify Alert label, BAM
 * 10. Delete Alert: Verify BAM record
 * 11. Update Role
 * @details
 */

public class TestManageNotesAndAlertsForRole extends BaseTest {

    private RoleType securityRoleCorporateType = RoleType.CORPORATE;
    private IRole securityCorporateRole = securityRoleCorporateType.get();
    private TestData tdRoleCorporate = tdSpecific.getTestData("RolesCorporateDG");

    @Test(groups = {"7.2_All_UC_ViewingBAMActivitiesForARoleOrManageNotes/Alerts"})
    //UC2961278
    @TestInfo(component = "Platform.Admin")
    public void testManageNotesAndAlertsForRole() {
        adminApp().open();

        //store role name
        String roleName = tdRoleCorporate.getValue(GeneralRoleTab.class.getSimpleName(), RoleMetaData.GeneralRoleTab.ROLE_NAME.getLabel());

        securityCorporateRole.create(tdRoleCorporate);

        RolePage.change(tdSpecific.getTestData("RoleCorporateSearch").adjust("SearchByField|Role Name", roleName));

        CustomAssert.enableSoftMode();

        //add Note
        NotesAndAlertsSummaryPage.add(tdSpecific.getTestData("TestData_Note"));

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, tdSpecific.getTestData("TestData_Note").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Committed");

        //update note
        NotesAndAlertsSummaryPage.updateNoteByRow(tdSpecific.getTestData("NoteUpdate"), 1);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(2, tdSpecific.getTestData("NoteUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE_UPDATE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Note");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //update profile and reopen to check changes are saved
        NotesAndAlertsTab.buttonUpdate.click();

        RolePage.change(tdSpecific.getTestData("RoleCorporateSearch").adjust("SearchByField|Role Name", roleName));

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(2, tdSpecific.getTestData("NoteUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE_UPDATE.getLabel()));

        //delete note
        NotesAndAlertsSummaryPage.deleteNoteByRow(2);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionNotExist(tdSpecific.getTestData("NoteUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE_UPDATE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Delete Note");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //add alert
        NotesAndAlertsSummaryPage.add(tdSpecific.getTestData("TestData_Alert"));

        NotesAndAlertsTab.labelAlert.verify.value(tdSpecific.getTestData("TestData_Alert").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.ALERT.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //update alert
        NotesAndAlertsSummaryPage.updateAlert(tdSpecific.getTestData("AlertUpdate"));

        NotesAndAlertsTab.labelAlert.verify.value(tdSpecific.getTestData("AlertUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.ALERT_UPDATE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Alert");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //update profile and reopen to check changes are saved
        NotesAndAlertsTab.buttonUpdate.click();

        RolePage.change(tdSpecific.getTestData("RoleCorporateSearch").adjust("SearchByField|Role Name", roleName));

        NotesAndAlertsTab.labelAlert.verify.value(tdSpecific.getTestData("AlertUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.ALERT_UPDATE.getLabel()));

        //delete alert
        NotesAndAlertsSummaryPage.deleteAlert();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Delete Alert");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        NotesAndAlertsTab.buttonUpdate.click();
        RolePage.tableRolesSearchResult.verify.present();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
