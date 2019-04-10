package aaa.modules.regression.document_fulfillment.auto_ss;

//import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
		verifyPreviewDocument(policyNumber_sc2, AA11AZ);
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2_Nano(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario2_Quote(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
		
		//3.5
		policy.policyDocGen().start();
		if (DocGenHelper.isPasDocEnabled(getState(), getPolicyType())) {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, getTestSpecificTD("TestData_AllDocs"), 
					AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, 
					AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1_PASDOC, ACPUBI, AHAPXX, AHRCTXXAUTO, 
					AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
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
		//odd_tab.saveAndExit();
		
		//3.8
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.E_SIGNATURE, DocGenEnum.EMAIL, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);		
		//odd_tab.saveAndExit();
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
			verifyPreviewDocument(policyNumber, AA43AZ);				
			//OR verify document generation
			//odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA43AZ);
			//DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA43AZ);		
			//TODO verify one document generated	
		}
		else {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA43AZ);
			DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA43AZ);
			verifyOneDocumentGenerated(policyNumber, "AA43AZ");	
		}
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
			//or verify document generation
			//odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAUBI, ACPUBI, AAUBI1_PASDOC);
			//DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AAUBI, ACPUBI, AAUBI1_PASDOC);			
			//TODO verify one document generated
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
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
			//OR verify document generation
			//odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAPDXX);
			//DocGenHelper.verifyDocumentsGenerated(policyNumber, AAPDXX);
			//TODO verify one document generated
		}
		else {
			odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AAPDXX);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, AAPDXX);
			verifyOneDocumentGenerated(policyNumber, "AAPDXX");
		}
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
			verifyPreviewDocument(policyNumber, AASR22);			
			//OR verify document generation
			//odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AASR22);
			//DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22);			
			//TODO verify one document generated
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
	
	private void verifyPreviewDocument(String policyNumber, TestData td, DocGenEnum.Documents...documents) {
		odd_tab.previewDocuments(td, documents);
		WebDriverHelper.switchToDefault();
		odd_tab.unselectDocuments(documents);	
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, EventName.ADHOC_DOC_ON_DEMAND_PREVIEW, documents);
	}

	private void verifyOneDocumentGenerated(String policyNum, String document) {
		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, document, "ADHOC_DOC_ON_DEMAND_GENERATE");
		assertThat(DBService.get().getValue(query).map(Integer::parseInt)).hasValue(1);
	}
	
}
