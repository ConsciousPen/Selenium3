/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.security.profile;

import org.testng.annotations.Test;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.metadata.security.ProfileMetaData.GeneralProfileTab;
import aaa.admin.modules.security.profile.IProfile;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.pages.security.ProfilePage;
import aaa.common.metadata.NotesAndAlertsMetaData;
import aaa.common.metadata.NotesAndAlertsMetaData.NotesAndAlertsTab;
import aaa.main.enums.ActionConstants;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Notes/Alerts on Security - Profile tab
 * @scenario
 * 1. Open Admin app
 * 2. Create Profile
 * 3. Search and Edit created Profile
 * 4. Add Note: Verify BAM Note
 * 5. Update Note: Verify BAM Note
 * 6. Update Profile
 * 7. Delete Note: Verify BAM Note
 * 8. Add Alert: Verify Alert label, BAM
 * 9. Update Alert: Verify Alert label, BAM
 * 10. Delete Alert: Verify BAM record
 * 11. Update Profile
 * @details
 */

public class TestAddNoteAndAlertForProfile extends BaseTest {

    private ProfileType profileType = ProfileType.CORPORATE;
    private IProfile profile = profileType.get();
    private TestData tdProfile = testDataManager.profiles.get(profileType);

    @Test(groups = {"7.2_All_UC_ViewingBAMActivitiesForUserProfileOrManageNotes/Alerts"})
    @TestInfo(component = "Platform.Admin")
    public void testAddNoteAndAlertForProfile() {
        adminApp().open();

        String login = tdProfile.getValue("DataGather", "TestData", GeneralProfileTab.class.getSimpleName(),
                GeneralProfileTab.USER_LOGIN.getLabel());

        profile.create(tdProfile.getTestData("DataGather", "TestData"));

        profile.search(tdProfile.getTestData("SearchData", "TestData").adjust(
                TestData.makeKeyPath(ProfileMetaData.SearchByField.class.getSimpleName(),
                        ProfileMetaData.SearchByField.USER_LOGIN.getLabel()),
                login));

        ProfilePage.tableProfileSearchResults.getRow(1).getCell(ProfilePage.tableProfileSearchResults.getColumnsCount()).controls.links.get(ActionConstants.CHANGE).click();

        CustomAssert.enableSoftMode();

        //add Note
        NotesAndAlertsSummaryPage.add(tdProfile.getTestData("DataGather", "TestData_Note"));

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, tdProfile.getTestData("DataGather",
                "TestData_Note").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Committed");

        //update note
        NotesAndAlertsSummaryPage.updateNoteByRow(tdProfile.getTestData("DataGather", "NoteUpdate"), 1);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(2, tdProfile.getTestData("DataGather",
                "NoteUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE_UPDATE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Note");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //update profile and reopen to check changes are saved
        NotesAndAlertsTab.buttonUpdate.click();
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(ProfilePage.tableProfileSearchResults.getColumnsCount()).controls.links.get(ActionConstants.CHANGE).click();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(2, tdProfile.getTestData("DataGather",
                "NoteUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE_UPDATE.getLabel()));

        //delete note
        NotesAndAlertsSummaryPage.deleteNoteByRow(2);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionNotExist(tdProfile.getTestData("DataGather",
                "NoteUpdate").getValue(NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(),
                NotesAndAlertsTab.NOTE_UPDATE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Delete Note");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //add alert
        NotesAndAlertsSummaryPage.add(tdProfile.getTestData("DataGather", "TestData_Alert"));

        NotesAndAlertsTab.labelAlert.verify.value(tdProfile.getTestData("DataGather", "TestData_Alert").getValue(
                NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(), NotesAndAlertsTab.ALERT.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //update alert
        NotesAndAlertsSummaryPage.updateAlert(tdProfile.getTestData("DataGather", "AlertUpdate"));

        NotesAndAlertsTab.labelAlert.verify.value(tdProfile.getTestData("DataGather", "AlertUpdate").getValue(
                NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(), NotesAndAlertsTab.ALERT_UPDATE.getLabel()));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Update Alert");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        //update profile and reopen to check changes are saved
        NotesAndAlertsTab.buttonUpdate.click();
        ProfilePage.tableProfileSearchResults.getRow(1).getCell(ProfilePage.tableProfileSearchResults.getColumnsCount()).controls.links.get(ActionConstants.CHANGE).click();

        NotesAndAlertsTab.labelAlert.verify.value(tdProfile.getTestData("DataGather", "AlertUpdate").getValue(
                NotesAndAlertsMetaData.NotesAndAlertsTab.class.getSimpleName(), NotesAndAlertsTab.ALERT_UPDATE.getLabel()));

        //delete alert
        NotesAndAlertsSummaryPage.deleteAlert();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Delete Alert");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        NotesAndAlertsTab.buttonUpdate.click();
        ProfilePage.tableProfileSearchResults.verify.present();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
