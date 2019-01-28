/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.ActivitiesAndUserNotes;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.common.metadata.NotesAndAlertsMetaData;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class NotesAndAlertsSummaryPage extends SummaryPage {

    public static ActivitiesAndUserNotes activitiesAndUserNotes = new ActivitiesAndUserNotes(By.xpath("//div[@id='activityCommandsForm:activityTable']//table"));
    public static StaticElement alert = new StaticElement(By.id("alertArchiveForm:alerts:0:arhiveAlert_id"));
    public static StaticElement alert2 = new StaticElement(By.id("alertArchiveForm:alerts:1:arhiveAlert_id"));
    public static StaticElement alertConfirmPolicyCancellation = new StaticElement(By.id("confirmDialog_msg"));

    public static AssetList assetListAddNoteOrAlert = new AssetList(By.xpath("//form[@id='createNote_form']"), NotesAndAlertsMetaData.NotesAndAlertsTab.class);
    public static AssetList assetListUpdateNote = new AssetList(By.xpath("//form[@id='updateNote_form']"), NotesAndAlertsMetaData.NotesAndAlertsTab.class);
    public static AssetList assetListUpdateAlert = new AssetList(By.xpath("//form[@id='updateAlert_form']"), NotesAndAlertsMetaData.NotesAndAlertsTab.class);
    public static Table tableFilterNoteOrAlert = new Table(By.id("activityCommandsForm:activityFilterTableForm"));
    public static Table tableFilterResults = new Table(By.id("activityCommandsForm:body_userActivitiesFilterTable"));
    public static Link linkCloseAddNoteOrAlert = new Link(By.xpath("//div[@id='createNote_form:createNote_togglePanel:header']//div[@class='rf-cp-lbl-exp']"));
    public static Link linkOpenAddNoteOrAlert = new Link(By.xpath("//div[@id='createNote_form:createNote_togglePanel:header']//div[@class='rf-cp-lbl-colps']"));
    public static Link linkViewAlert = new Link(By.id("alertArchiveForm:alerts:0:arhiveAlertView"));

    public static Button buttonDeleteAlert = new Button(By.id("alertArchiveForm:archiveBtn"));
    public static Button buttonNotesAlerts = new Button(By.id("addUserNote"));

    public static Dialog dialogRemoveNote = new Dialog("//div[@id='activityCommandsForm:eliminateNoteConfirm']");
    public static Dialog dialogRemoveAlert = new Dialog("//div[@id='deleteAlertConfirmDialogDialog_container']");

    public static void add(TestData td) {
        open();
        expandAddNoteOrAlert();
        assetListAddNoteOrAlert.fill(td);
        Tab.buttonSave.click();
    }

    public static void updateNoteByRow(TestData td, int row) {
    	tableFilterResults.getRow(row).getCell("Title").controls.links.getFirst().click();
        assetListUpdateNote.fill(td);
        Tab.buttonSave.click();
    }

    public static void checkActivitiesAndUserNotes (String note, Boolean present) {
        activitiesAndUserNotes.expand();
        assertThat(activitiesAndUserNotes.getRowContains("Description", note)).isPresent(present);
    }

    public static void checkActivitiesAndUserNotes (String note, Boolean present, ETCSCoreSoftAssertions softly) {
        activitiesAndUserNotes.expand();
        softly.assertThat(activitiesAndUserNotes.getRowContains("Description", note)).isPresent(present);
    }

    public static int countActivitiesAndUserNotes (String note) {
        activitiesAndUserNotes.expand();
        Map<String, String> query = new HashMap<>();
        query.put("Description", note);
        return activitiesAndUserNotes.getRowsThatContain(query).size();
    }

    public static void deleteNoteByRow(int row) {
        activitiesAndUserNotes.getRow(row).getCell(5).controls.links.get("ELIMINATE").click();
        dialogRemoveNote.buttonYes.click();
    }

    public static void updateAlert(TestData td) {
        linkViewAlert.click();
        assetListUpdateAlert.fill(td);
        Tab.buttonSave.click();
    }

    public static void deleteAlert() {
        buttonDeleteAlert.click();
        dialogRemoveAlert.buttonYes.click();
    }

    public static void open() {
        buttonNotesAlerts.click();
    }

    private static void expandAddNoteOrAlert() {
        if (linkOpenAddNoteOrAlert.isPresent()) {
            linkOpenAddNoteOrAlert.click();
        }
    }
}
