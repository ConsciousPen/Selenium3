package aaa.modules.regression.document_fulfillment.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.xml.model.pasdoc.DocumentGenerationRequest;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.EventName;
import static aaa.main.enums.DocGenEnum.Documents.*;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.waiters.Waiters;

public class PasDoc_AdhocGODDcommon extends AutoSSBaseTest {
	
	private GenerateOnDemandDocumentActionTab odd_tab = new GenerateOnDemandDocumentActionTab();
	
	/**
	 * Adhoc GODD Common Scenario 1: "Generate on Demand Document" UI
	 * Precondition: 
	 * 		Policy is issued. 
	 * 1.1. Start 'Generate on Demand Document" and check the UI. 
	 * 		"Delivery Method" section is shown with the following options:
	 * 		- Email, 
	 * 		- Fax, 
	 * 		- Central Print, 
	 * 		- eSignature, 
	 * 		- Local Print. 
	 * 		The following buttons are shown: OK, Cancel, Preview Documents. 
	 * 1.2. Do not select documents, click OK and verify error message is shown: 
	 * 		"Please select document(s) to be generated". 
	 * 1.3. Click Cancel and verify Policy Consolidated Page is shown. 
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		CustomSoftAssertions.assertSoftly(softly -> {
			//1.1
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD)).isPresent();
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD))
				.hasOptions("Email", "Fax", "Central Print", "eSignature",  "Local Print");
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.OK_BTN)).isPresent();
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.CANCEL_BTN)).isPresent();
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN)).isPresent();
			//1.2
			odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN).click(Waiters.AJAX);
			softly.assertThat(odd_tab.errorMsg.getValue()).isEqualTo("Please select document(s) to be generated.");
			odd_tab.closeErrorDialogBtn.click();
			//1.3
			odd_tab.buttonCancel.click();
			assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();
		});		
	}
	
	/**
	 * Adhoc GODD Common Scenario 2: Preview Documents - Standard Policy
	 * Precondition: 
	 * 		Policy is Issued with: 
	 * 		- Policy Type = Standard, 
	 * 		- Excluded Driver is added, 
	 * 		- Driver with Permit, 
	 * 		- Driver: Financial Responsibility = Yes, 
	 * 		- Vehicle: enrolled in UBI, 
	 * 		- Uninsured and Underinsured Coverages < than recommended. 
	 * 		Start 'Generate On Demand Document' action. 
	 * 1.1. Select form AA10XX, click 'Preview Document' and verify document is opened on preview 
	 * 		(it means that xml is generated with tag <doc:EventName>ADHOC_DOC_ON_DEMAND_PREVIEW</doc:EventName>).
	 * 1.2. Select form AHRCTXX, click 'Preview Document' and verify document is opened on preview. 
	 * 1.3. Select form AHAPXX, click 'Preview Document' and verify document is opened on preview. 
	 * 1.4. Select form AA11AZ, click 'Preview Document' and verify document is opened on preview with form AHPNXX. 
	 * 1.5. Select form AA43AZ, click 'Preview Document' and verify document is opened on preview. 
	 * 1.6. Select form AASR22, click 'Preview Document' and verify document is opened on preview. 
	 * 1.7. Select form AA52AZ, click 'Preview Document' and verify document is opened on preview. 
	 * 1.8. Select form AAUBI, click 'Preview Document' and verify document is opened on preview.
	 * 1.9. Select form AAUBI1, click 'Preview Document' and verify document is opened on preview. 
	 * 1.10. Select form ACPUBI, click 'Preview Document' and verify document is opened on preview.
	 * 1.11. Select form AAPDXX, click 'Preview Document' and verify document is opened on preview.
	 * 1.12. Select form AU02, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.13. Select form AU04, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.14. Select form AU05, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.15. Select form AU06, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.16. Select form AU07, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.17. Select form AU08, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.18. Select form AU09, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.19. Select form AU10, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.20. Select form 60 5004, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 * 1.21. Select form AA06XX, specify data in additional fields, click 'Preview Document' and verify document is opened on preview.
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc2; 
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc2 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2").resolveLinks());
		}
		else {
			td_sc2 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2_NO_PASDOC").resolveLinks());
		}
		String policyNumber_sc2 = createPolicy(td_sc2);
		log.info("PAS DOC: Scenario 2: Policy with specific data created: " + policyNumber_sc2);
		
		policy.policyDocGen().start();
		//2.1 - 2.21
		verifyPreviewDocument(policyNumber_sc2, AA10XX);
		verifyPreviewDocument(policyNumber_sc2, AHRCTXXAUTO);
		verifyPreviewDocument(policyNumber_sc2, AHAPXX);
		verifyPreviewDocumentAA11AZ(policyNumber_sc2);
		verifyPreviewDocument(policyNumber_sc2, AA43AZ);
		verifyPreviewDocument(policyNumber_sc2, AASR22);
		verifyPreviewDocument(policyNumber_sc2, AAUBI);
		verifyPreviewDocument(policyNumber_sc2, ACPUBI);
		verifyPreviewDocument(policyNumber_sc2, AAPDXX);
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			verifyPreviewDocument(policyNumber_sc2, AA52AZ);
			verifyPreviewDocument(policyNumber_sc2, AAUBI1_PASDOC);
		}
		else {
			verifyPreviewDocument(policyNumber_sc2, AA52AZ_UPPERCASE);
			verifyPreviewDocument(policyNumber_sc2, AAUBI1);
		}
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU02"), AU02);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU04"), AU04);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU05"), AU05);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU06"), AU06);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU07"), AU07);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU08"), AU08);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU09"), AU09);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AU10"), AU10);
		verifyPreviewDocument(policyNumber_sc2, getTestSpecificTD("TestData_AA06XX"), AA06XX_AUTOSS);
		odd_tab.saveAndExit();		
	}
	
	/**
	 * Adhoc GODD Common Scenario 2: Preview Documents - NANO
	 * Precondition: 
	 * 		Policy is Issued with Policy Type = Non-Owner. 
	 * 		Start 'Generate On Demand Document' action. 
	 * 2.22. Select form AA41XX, click 'Preview Documents' and verify document is opened on preview 
	 * 		(it means that xml is generated with tag <doc:EventName>ADHOC_DOC_ON_DEMAND_PREVIEW</doc:EventName>).
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2_Nano(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc2_nano = getPolicyTD().adjust(getTestSpecificTD("TestData_NANO").resolveLinks());
		String policyNum_sc2_nano = createPolicy(td_sc2_nano);
		log.info("PAS DOC: Scenario 2: Non-owner Policy created: " + policyNum_sc2_nano);
		
		policy.policyDocGen().start();
		//2.22
		verifyPreviewDocument(policyNum_sc2_nano, AA41XX);
		odd_tab.saveAndExit();		
	}
	
	/**
	 * Adhoc GODD Common Scenario 2: Preview Documents - Quote
	 * Precondition: 
	 * 		Quote is Rated with Policy Type = Standard. 
	 * 		Start 'Generate On Demand Document' action. 
	 * 2.23. Select form AU03, specify data in the additional fields, click 'Preview Documents' and verify 
	 * 		document is opened on preview (xml is generated with tag <do c:EventName>ADHOC_DOC_ON_DEMAND_PREVIEW</doc:EventName>). 
	 * 2.24. Select form AHFMXX, click 'Preview Document' and verify document is opened on preview. 
	 * 2.25. Select form AAIQAZ, click 'Preview Document' and verify document is opened on preview.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2_Quote(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String quoteNumber_sc2 = createQuote();
		log.info("PAS DOC: Scenario 2: Quote created: " + quoteNumber_sc2);
		
		policy.policyDocGen().start();
		//2.23 - 2.25
		verifyPreviewDocument(quoteNumber_sc2, getTestSpecificTD("TestData_AU03"), AU03);
		verifyPreviewDocument(quoteNumber_sc2, AHFMXX);
		verifyPreviewDocument(quoteNumber_sc2, AAIQAZ);
		odd_tab.saveAndExit();	
	}
	
	/**
	 * Adhoc GODD Common Scenario 3: All/several documents are selected
	 * Precondition: 
	 * 		Policy is Issued: 
	 * 		- Policy Type = Standard, 
	 * 		- Excluded Driver is added, 
	 * 		- Driver with Permit, 
	 * 		- Driver: Financial Responsibility = Yes, 
	 * 		- Vehicle: enrolled in UBI, 
	 * 		- Uninsured and Underinsured Coverages < than recommended. 
	 * 		Start 'Generate On Demand Document' action.
	 * 3.1. Select all the documents, click 'Preview Documents' and verify all forms are opened on preview. 
	 * 3.2. Select several documents, click 'Preview Documents' and verify only selected forms are opened on preview. 
	 * 3.3. Select all the documents, set 'Delivery Method' = Email, click 'OK' and verify All forms are sent. 
	 * 3.4. Select several documents, set 'Delivery Method' = Email, click 'OK' and verify only selected forms are sent. 
	 * 3.5. Select all the documents, select 'Delivery Method' = Local Print, click 'Preview Documents' 
	 * 		and verify All forms are generated (+ AHPNXX form). 
	 * 3.6. Select several documents, select 'Delivery Method' = Local Print, click 'Preview Documents'
	 * 		and verify only selected forms are generated. 
	 * 3.7. Select several documents with Delivery Method = Central Print available, select 'Delivery Method' = Central Print, 
	 * 		click 'OK' and verify only selected forms are generated. 
	 * 3.8. Select several documents with Delivery Method = eSignature available, select 'Delivery Method' = eSignature, 
	 * 		click 'OK' and verify only selected forms are sent. 
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc3; 
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc3 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2").resolveLinks());
		}
		else {
			td_sc3 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2_NO_PASDOC").resolveLinks());
		}
		createPolicy(td_sc3);
		String policyNumber = createPolicy(td_sc3);
		log.info("PAS DOC: Scenario 3: Policy created with #" + policyNumber);
		
		//3.1
		policy.policyDocGen().start();
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			verifyPreviewDocument(policyNumber, getTestSpecificTD("TestData_AllDocs"), AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, 
					AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		}	
		else {
			verifyPreviewDocument(policyNumber, getTestSpecificTD("TestData_AllDocs_withoutAA06XX"), //AA06XX_AUTOSS,
					AA10XX, AA11AZ, AA43AZ, AA52AZ_UPPERCASE, AAPDXX, AASR22, 
					AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		}
		//3.2
		verifyPreviewDocument(policyNumber,  AA10XX, AA11AZ);
		odd_tab.saveAndExit();
		
		//3.3
		policy.policyDocGen().start();
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, getTestSpecificTD("TestData_AllDocs"), 
					AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, 
					AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		}
		else {			
			odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, getTestSpecificTD("TestData_AllDocs_withoutAA06XX"), //AA06XX_AUTOSS,
					AA10XX, AA11AZ, AA43AZ, AA52AZ_UPPERCASE, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, //AA06XX_AUTOSS, 
					AA10XX, AA11AZ, AA43AZ, AA52AZ_UPPERCASE, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		}

		//3.4
		policy.policyDocGen().start();
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);
		policy.policyDocGen().start();
		odd_tab.saveAndExit();
		
		//3.5
		policy.policyDocGen().start();
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, getTestSpecificTD("TestData_AllDocs"), 
					AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, 
					AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10, AHPNXX);
		}
		else {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, getTestSpecificTD("TestData_AllDocs_withoutAA06XX"), //AA06XX_AUTOSS,
					AA10XX, AA11AZ, AA43AZ, AA52AZ_UPPERCASE, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, //AA06XX_AUTOSS, 
					AA10XX, AA11AZ, AA43AZ, AA52AZ_UPPERCASE, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		}
		odd_tab.saveAndExit();
		
		//3.6
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);
		odd_tab.saveAndExit();
		
		//3.7
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, null, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);
		policy.policyDocGen().start();
		odd_tab.saveAndExit();
		
		//3.8
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.E_SIGNATURE, DocGenEnum.EMAIL, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);		
		//odd_tab.saveAndExit();
	}
	
	/**
	 * Adhoc GODD Common Scenario 4: "Agent Advice Memo Document Operation" privilege for AA06XX
	 * Precondition: 
	 * 		Policy is issued. 
	 * 4.1. Log in via User with 'Agent Advice Memo Document Operation' privilege and Group = B31. 
	 * 		Open Policy and start 'Generate On Demand Document' action. 
	 * 		Verify Form AA06XX is shown. 
	 * 4.2. Log in via User without 'Agent Advice Memo Document Operation' privilege and Group = B31. 
	 * 		Open Policy and start 'Generate On Demand Document' action. 
	 * 		Verify Form AA06XX is NOT shown. 
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("PAS DOC: Scenario 4: Privilege 'Agent Advise Memo Document Operation': Created Policy#" + policyNumber);
		mainApp().close();
		
		//4.1
		mainApp().open(getLoginTD(UserGroups.B31));
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, false, AA06XX_AUTOSS);
		odd_tab.saveAndExit();
		mainApp().close();
		
		//4.2
		mainApp().open(getLoginTD(UserGroups.QA));
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, AA06XX_AUTOSS);
		odd_tab.saveAndExit();
	}
	
	/**
	 * Adhoc GODD Common Scenario 5: Several Excluded Drivers are added
	 * Precondition: 
	 * 		Policy is issued with 2 or more Excluded Drivers are added. 
	 * 		Start 'Generate On Demand Document' action. 
	 * 5.1. Select form AA43XX. Click 'Preview Documents' or select 'Local Print' and click 'OK'. 
	 * 		Verify that one document with the list of Excluded Drivers is generated. 
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDrivers").resolveLinks());
		String policyNumber = createPolicy(td_sc5);
		log.info("PAS DOC: Scenario 5: Several Excluded Drivers: Created Policy#" + policyNumber);

		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(AA43AZ);		
		//5.1
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			//verify document preview
			//verifyPreviewDocument(policyNumber, AA43AZ);
			//verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, AA43AZ);
			//OR verify document generation
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA43AZ);
			PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AA43AZ);		
			verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AA43AZ);
		}
		else {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA43AZ);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA43AZ);
			verifyOneDocumentGenerated(policyNumber, "AA43AZ");	
		}
	}

	/**
	 * Adhoc GODD Common Scenario 6: Several Vehicles with UBI are added
	 * Precondition: 
	 * 		Policy is issued with 2 or more Vehicles with UBI are added. 
	 * 		Start 'Generate On Demand Document' action
	 * 6.1. Select UBI forms. Click 'Preview Documents' or select 'Local Print' and click 'OK'. 
	 * 		Verify One document per form is generated (not for every Vehicle).
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc6; 
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			td_sc6 = getPolicyTD().adjust(getTestSpecificTD("TestData_VehiclesWithUBI").resolveLinks());
		}
		else {
			td_sc6 = getPolicyTD().adjust(getTestSpecificTD("TestData_VehiclesWithUBI_NO_PASDOC").resolveLinks());
		}
		createPolicy(td_sc6);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 6: Several Vehicles with UBI: Created Policy#" + policyNumber);
		
		policy.policyDocGen().start();
		//6.1
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			odd_tab.verify.documentsPresent(AAUBI, ACPUBI, AAUBI1_PASDOC);
			//verify document preview
			verifyPreviewDocument(policyNumber, AAUBI, ACPUBI, AAUBI1_PASDOC);	
			verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, AAUBI);
			verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, ACPUBI);
			verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, AAUBI1_PASDOC);
			//or verify document generation
			//odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAUBI, ACPUBI, AAUBI1_PASDOC);
			//PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AAUBI, ACPUBI, AAUBI1_PASDOC);			
			//verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AAUBI);
			//verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, ACPUBI);
			//verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AAUBI1_PASDOC);
		}
		else {
			odd_tab.verify.documentsPresent(AAUBI, ACPUBI, AAUBI1);
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAUBI, ACPUBI, AAUBI1);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AAUBI, ACPUBI, AAUBI1);
			verifyOneDocumentGenerated(policyNumber, "AAUBI");
			verifyOneDocumentGenerated(policyNumber, "AAUBI1");
			verifyOneDocumentGenerated(policyNumber, "ACPUBI");
		}
	}
	
	/**
	 * Adhoc GODD Common Scenario 7: Several Drivers with Permit are added
	 * Precondition: 
	 * 		Policy is issued with 2 or more Drivers with Permit are added. 
	 * 		Start 'Generate On Demand Document' action
	 * 7.1. Select AAPDXX form. Click 'Preview Documents' or select 'Local Print' and click 'OK'. 
	 * 		Verify that One document per form is generated with 1 page per permit driver.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_PermitDrivers").resolveLinks());
		createPolicy(td_sc7);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 7: Several Drivers with Permit: Created Policy#" + policyNumber);
		
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(AAPDXX);
		//7.1
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			//verify document preview
			verifyPreviewDocument(policyNumber, AAPDXX);
			verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, AAPDXX);
			//OR verify document generation
			//odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAPDXX);
			//PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AAPDXX);
			//verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AAPDXX);
		}
		else {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAPDXX);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, AAPDXX);
			verifyOneDocumentGenerated(policyNumber, "AAPDXX");
		}
	}
	
	/**
	 * Adhoc GODD Common Scenario 8: Several Drivers with Financial Responsibility = Yes are added
	 * Precondition: 
	 * 		Policy is issued with 2 or more Drivers with Financial Responsibility = Yes are added. 
	 * 		Start 'Generate On Demand Document' action.
	 * 8.1. Select AASR22 form/s. Click 'Preview Documents' or select 'Local Print' and click 'OK'. 
	 * 		Verify that One document with multiple SR22 forms generated for all drivers.
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc8 = getPolicyTD().adjust(getTestSpecificTD("TestData_FinancialDrivers").resolveLinks());
		//createPolicy(td_sc8);
		//workaround added
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc8, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.submitTab();
		if (docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER).isPresent()) {
			docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER).setValue("12346");
			docsAndBindTab.submitTab();
		}
		new PurchaseTab().fillTab(td_sc8);
		new PurchaseTab().submitTab();	
		
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 8: Several Drivers with Financial Responsibility: Created Policy#" + policyNumber);
		
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(AASR22);
		//8.1
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			//verify document preview
			//verifyPreviewDocument(policyNumber, AASR22);
			//verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, AASR22);
			//OR verify document generation
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AASR22);
			PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AASR22);			
			verifyOneDocumentGenerated(policyNumber, EventName.ADHOC_DOC_ON_DEMAND_GENERATE, AASR22);
		}
		else {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AASR22);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22);
			verifyOneDocumentGenerated(policyNumber, "AASR22");
		}
	}

	private void verifyPreviewDocument(String policyNumber, DocGenEnum.Documents...documents) {
		verifyPreviewDocument(policyNumber, null, documents);
	}
	
	/**
	 * Select <b>documents</b>, click <b>Preview Documents</b> button, unselect documents and than verify 
	 * that documents are opened on preview (e.g. generated xml with <doc:EventName>ADHOC_DOC_ON_DEMAND_PREVIEW</doc:EventName>)
	 * @param policyNumber  Policy number
	 * @param td			TestData if selected documents have additional fields on GODD tab or null
	 * @param documents		List of documents should be sent on preview
	 */
	private void verifyPreviewDocument(String policyNumber, TestData td, DocGenEnum.Documents...documents) {
		odd_tab.previewDocuments(td, documents);
		WebDriverHelper.switchToDefault();
		odd_tab.unselectDocuments(documents);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, documents); 	
	}
	
	/**
	 * The method verifies that with document AA11AZ opens on preview also AHPNXX
	 * @param policyNumber
	 */
	private void verifyPreviewDocumentAA11AZ(String policyNumber) {
		odd_tab.previewDocuments(null, AA11AZ);
		WebDriverHelper.switchToDefault();
		odd_tab.unselectDocuments(AA11AZ);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, AA11AZ, AHPNXX);
	}

	/**
	 * The method verifies that only one <b>document</b> is generated. 
	 * Can be used only for NON-PAS DOC 
	 * @param policyNum
	 * @param document
	 */
	private void verifyOneDocumentGenerated(String policyNum, String document) {
		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, document, "ADHOC_DOC_ON_DEMAND_GENERATE");
		assertThat(DBService.get().getValue(query).map(Integer::parseInt)).hasValue(1);
	}
	
	/**
	 * The method verifies that only one <b>document</b> ig generated or opened on preview (depends on <b>eventName</b>)
	 * @param policyNumber
	 * @param eventName
	 * @param document
	 */
	private void verifyOneDocumentGenerated(String policyNumber, DocGenEnum.EventName eventName, DocGenEnum.Documents document) {
		DocumentGenerationRequest docGenReq = PasDocImpl.getDocumentRequest(policyNumber, eventName, document);
		long docsNumber = docGenReq.getDocuments().stream().filter(c -> document.getIdInXml().equals(c.getTemplateId())).count();
		assertThat(docsNumber).as("More than one document " + document.getIdInXml() + " generated: " + docsNumber).isEqualTo(1);
	}
	
}
