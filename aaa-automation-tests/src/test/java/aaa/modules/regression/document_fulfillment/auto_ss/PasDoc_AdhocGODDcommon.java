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
import aaa.main.enums.DocGenEnum;
import static aaa.main.enums.DocGenEnum.Documents.*;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
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
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc2 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2").resolveLinks());
		createPolicy(td_sc2);
		log.info("PAS DOC: Scenario 2: Policy with specific data created: " + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		//2.1 - 2.21
		verifyPreviewDocument(AA10XX);
		verifyPreviewDocument(AHRCTXXAUTO);
		verifyPreviewDocument(AHAPXX);
		verifyPreviewDocument(AA11AZ);
		verifyPreviewDocument(AA43AZ);
		verifyPreviewDocument(AASR22);
		verifyPreviewDocument(AA52AZ);
		verifyPreviewDocument(AAUBI);
		verifyPreviewDocument(AAUBI1);
		verifyPreviewDocument(ACPUBI);
		verifyPreviewDocument(AAPDXX);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU02"), AU02);		
		verifyPreviewDocument(getTestSpecificTD("TestData_AU04"), AU04);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU05"), AU05);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU06"), AU06);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU07"), AU07);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU08"), AU08);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU09"), AU09);
		verifyPreviewDocument(getTestSpecificTD("TestData_AU10"), AU10);
		verifyPreviewDocument(getTestSpecificTD("TestData_AA06XX"), AA06XX_AUTOSS);
		odd_tab.saveAndExit();		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario2_Nano(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc2_nano = getPolicyTD().adjust(getTestSpecificTD("TestData_NANO").resolveLinks());
		createPolicy(td_sc2_nano);
		log.info("PAS DOC: Scenario 2: Non-owner Policy created: " + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		//2.22
		verifyPreviewDocument(AA41XX);
		odd_tab.saveAndExit();		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario2_Quote(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		createQuote();
		log.info("PAS DOC: Scenario 2: Quote created: " + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		//2.23 - 2.25
		verifyPreviewDocument(getTestSpecificTD("TestData_AU03"), AU03);
		verifyPreviewDocument(AHFMXX);
		verifyPreviewDocument(AAIQAZ);
		odd_tab.saveAndExit();	
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc2 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2").resolveLinks());
		createPolicy(td_sc2);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 3: Policy created with #" + policyNumber);
		
		//3.1
		policy.policyDocGen().start();		
		verifyPreviewDocument(getTestSpecificTD("TestData_AllDocs"), 
				AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
				AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		
		//3.2
		verifyPreviewDocument(AA10XX, AA11AZ);
		odd_tab.saveAndExit();
		
		//3.3
		policy.policyDocGen().start();
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, getTestSpecificTD("TestData_AllDocs"), 
				AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
				AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, 
				AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
				AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		
		//3.4
		policy.policyDocGen().start();
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);
		
		//3.5
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, getTestSpecificTD("TestData_AllDocs"), 
				AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
				AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, 
				AA06XX_AUTOSS, AA10XX, AA11AZ, AA43AZ, AA52AZ, AAPDXX, AASR22, AAUBI, AAUBI1, ACPUBI, AHAPXX, AHRCTXXAUTO, 
				AU02, AU04, AU05, AU06, AU07, AU08, AU09, AU10);
		odd_tab.saveAndExit();
		
		//3.6
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);
		
		//3.7
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, null, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);
		
		//3.8
		policy.policyDocGen().start();
		odd_tab.generateDocuments(false, DocGenEnum.DeliveryMethod.E_SIGNATURE, null, null, null, AA10XX, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA10XX, AA11AZ);		
		odd_tab.saveAndExit();
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
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
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDrivers").resolveLinks());
		String policyNumber = createPolicy(td_sc5);
		log.info("PAS DOC: Scenario 5: Several Excluded Drivers: Created Policy#" + policyNumber);
		
		//DocGenHelper.clearDocGenFolders();
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, AA43AZ);
		//odd_tab.generateDocuments(Documents.AA43AZ);
		odd_tab.selectDocuments(AA43AZ);
		odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN).click(Waiters.AJAX);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AA43AZ);	
		/*
		verifyOneDocumentGenerated(policyNumber, "AA43AZ");
		
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA43AZ", "ADHOC_DOC_ON_DEMAND_GENERATE");
		CustomSoftAssertions.assertSoftly(softly -> {
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrLstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo("Driver1");
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrLstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(1).getDataElementChoice().getTextField()).isEqualTo("Driver2");
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrFrstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo("Excluded1");
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrFrstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(1).getDataElementChoice().getTextField()).isEqualTo("Excluded2");
		});
		*/
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc6 = getPolicyTD().adjust(getTestSpecificTD("TestData_VehiclesWithUBI").resolveLinks());
		createPolicy(td_sc6);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 6: Several Vehicles with UBI: Created Policy#" + policyNumber);
		
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, AAUBI, ACPUBI, AAUBI1);
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null,
				AAUBI, ACPUBI, AAUBI1);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AAUBI, ACPUBI, AAUBI1);
		verifyOneDocumentGenerated(policyNumber, "AAUBI");
		verifyOneDocumentGenerated(policyNumber, "AAUBI1");
		verifyOneDocumentGenerated(policyNumber, "ACPUBI");
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_PermitDrivers").resolveLinks());
		createPolicy(td_sc7);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 7: Several Drivers with Permit: Created Policy#" + policyNumber);
		
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(AAPDXX);
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, AAPDXX);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AAPDXX);
		verifyOneDocumentGenerated(policyNumber, "AAPDXX");
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc8 = getPolicyTD().adjust(getTestSpecificTD("TestData_FinancialDrivers").resolveLinks());
		createPolicy(td_sc8);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("PAS DOC: Scenario 8: Several Drivers with Financial Responsibility: Created Policy#" + policyNumber);
		
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, AASR22);
		odd_tab.generateDocuments(AASR22);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, AASR22);	
		verifyOneDocumentGenerated(policyNumber, "AASR22");
	}

	private void verifyPreviewDocument(DocGenEnum.Documents...documents) {
		verifyPreviewDocument(null, documents);
	}
	
	private void verifyPreviewDocument(TestData td, DocGenEnum.Documents...documents) {
		odd_tab.previewDocuments(td, documents);
		//odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN).click(Waiters.AJAX);
		WebDriverHelper.switchToDefault();
		//TODO verify generated doc contains <doc:EventName>ADHOC_DOC_ON_DEMAND_PREVIEW</doc:EventName>		
		
		odd_tab.unselectDocuments(documents);
	}
	
	private void verifyOneDocumentGenerated(String policyNum, String document) {
		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, document, "ADHOC_DOC_ON_DEMAND_GENERATE");
		assertThat(DBService.get().getValue(query).map(Integer::parseInt)).hasValue(1);
	}
}
