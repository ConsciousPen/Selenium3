package aaa.modules.regression.sales.template;

import static toolkit.verification.CustomAssertions.assertThat;

import java.util.HashMap;

import aaa.common.metadata.NotesAndAlertsMetaData;
import aaa.common.metadata.NotesAndAlertsMetaData.NotesAndAlertsTab;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;


public abstract class PolicyNoteCreateUpdate extends PolicyBaseTest {
	
	public void testPolicyNoteCreateUpdate() {
		mainApp().open();
		getCopiedPolicy();
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Add note
		TestData tdNotes = testDataManager.notesAndAlerts;
		NotesAndAlertsSummaryPage.add(tdNotes.getTestData("CreateNote", "TestData"));
		
		String noteDescription = tdNotes.getTestData("CreateNote", "TestData").getValue(NotesAndAlertsTab.class.getSimpleName(), 
				NotesAndAlertsMetaData.NotesAndAlertsTab.NOTE.getLabel());
		
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", noteDescription)).isPresent();
		
		//Update note
		String noteDateTime = NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Date/Time").getValue();
		NotesAndAlertsSummaryPage.open();
		NotesAndAlertsSummaryPage.updateNoteByRow(tdNotes.getTestData("UpdateNote", "TestData"), 1);
		
		String noteDescriptionUpdated = tdNotes.getTestData("UpdateNote", "TestData").getValue(NotesAndAlertsTab.class.getSimpleName(), 
				NotesAndAlertsMetaData.NotesAndAlertsTab.NOTE_UPDATE.getLabel());
		
		HashMap<String, String> noteUpdated = new HashMap<>();
		noteUpdated.put("Date/Time", noteDateTime);
		noteUpdated.put("Description", noteDescriptionUpdated);
		
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", noteDescription)).isPresent(false);
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(noteUpdated)).isPresent();
		
		String newNoteDescription = "Update Note " + noteDateTime + " for Policy " + PolicySummaryPage.getPolicyNumber();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow("Description", newNoteDescription)).isPresent();	
		
	}

}
