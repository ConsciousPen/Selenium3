package aaa.modules.regression.sales.template;

import static toolkit.verification.CustomAssertions.assertThat;

import java.util.HashMap;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.metadata.NotesAndAlertsMetaData;
import aaa.common.metadata.NotesAndAlertsMetaData.NotesAndAlertsTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

/**
 * @author oreva
 * @name Test - Create Note and update it
 * @scenario
 * 1. Find customer or create new if customer does not exist. 
 * 2. Create a new policy.
 * 3. Click on 'Notes/Alerts' button on Policy Consolidated page. 
 * 4. Add a note. 
 * 5. Verify that note is displaying on Activities & User Notes section. 
 * 6. Click on 'Notes/Alerts' button on Policy Consolidated page.
 * 7. Find added note and update it -change note description, save it. 
 * 8. Verify that updated note is displaying on Activities & User Notes section.
 *
 */
public abstract class PolicyNoteCreateUpdate extends PolicyBaseTest {
	
	public void testPolicyNoteCreateUpdate() {
		
		if (getUserGroup().equals(UserGroups.B31.get())) {
			//Login with QA user and create a policy
			mainApp().open(getLoginTD(UserGroups.QA));
			createCustomerIndividual();
			createPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			mainApp().close();
			//Login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			createNote();			
		}
		else if (getUserGroup().equals(UserGroups.F35.get())) {
			mainApp().open();
			getCopiedPolicy();			
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			createNote();
		}
		else {
			mainApp().open();
			getCopiedPolicy();			
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);		
			createAndUpdateNote();
		}		
	}
	
	public void createNote() {
		//Add note
		TestData tdNotes = testDataManager.notesAndAlerts;
		log.info("Note creation started");
		NotesAndAlertsSummaryPage.add(tdNotes.getTestData("CreateNote", "TestData"));
		
		String noteDescription = tdNotes.getTestData("CreateNote", "TestData").getValue(NotesAndAlertsTab.class.getSimpleName(), 
				NotesAndAlertsMetaData.NotesAndAlertsTab.NOTE.getLabel());
		
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", noteDescription)).isPresent();				
	}
	
	public void createAndUpdateNote() {
		//Add note
		TestData tdNotes = testDataManager.notesAndAlerts;
		log.info("Note creation started");
		NotesAndAlertsSummaryPage.add(tdNotes.getTestData("CreateNote", "TestData"));
		
		String noteDescription = tdNotes.getTestData("CreateNote", "TestData").getValue(NotesAndAlertsTab.class.getSimpleName(), 
				NotesAndAlertsMetaData.NotesAndAlertsTab.NOTE.getLabel());
		
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains("Description", noteDescription)).isPresent();
		
		//Update note
		String noteDateTime = NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1).getCell("Date/Time").getValue();
		log.info("Note update started");
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
