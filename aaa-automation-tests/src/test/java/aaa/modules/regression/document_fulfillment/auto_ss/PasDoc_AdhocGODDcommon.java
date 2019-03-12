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
import aaa.main.enums.DocGenEnum.Documents;
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
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD)).isPresent();
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD))
				.hasOptions("Email", "Fax", "Central Print", "eSignature",  "Local Print");
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.OK_BTN)).isPresent();
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.CANCEL_BTN)).isPresent();
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN)).isPresent();
			odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN).click(Waiters.AJAX);
			softly.assertThat(odd_tab.errorMsg.getValue()).isEqualTo("Please select document(s) to be generated.");
			odd_tab.closeErrorDialogBtn.click();			
		});		
		odd_tab.saveAndExit();
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
		verifyPreviewDocument(Documents.AA10XX);
		verifyPreviewDocument(Documents.AHRCTXXAUTO);
		verifyPreviewDocument(Documents.AHAPXX);
		verifyPreviewDocument(Documents.AA11AZ);
		verifyPreviewDocument(Documents.AA43AZ);
		verifyPreviewDocument(Documents.AASR22);
		verifyPreviewDocument(Documents.AA52AZ);
		verifyPreviewDocument(Documents.AAUBI);
		verifyPreviewDocument(Documents.AAUBI1);
		verifyPreviewDocument(Documents.ACPUBI);
		verifyPreviewDocument(Documents.AAPDXX);
		verifyPreviewDocument(Documents.AU02, getTestSpecificTD("TestData_AU02"));		
		verifyPreviewDocument(Documents.AU04, getTestSpecificTD("TestData_AU04"));
		verifyPreviewDocument(Documents.AU05, getTestSpecificTD("TestData_AU05"));
		verifyPreviewDocument(Documents.AU06, getTestSpecificTD("TestData_AU06"));
		verifyPreviewDocument(Documents.AU07, getTestSpecificTD("TestData_AU07"));
		verifyPreviewDocument(Documents.AU08, getTestSpecificTD("TestData_AU08"));
		verifyPreviewDocument(Documents.AU09, getTestSpecificTD("TestData_AU09"));
		verifyPreviewDocument(Documents.AU10, getTestSpecificTD("TestData_AU10"));
		verifyPreviewDocument(Documents.AA06XX_AUTOSS, getTestSpecificTD("TestData_AA06XX"));
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
		verifyPreviewDocument(Documents.AA41XX);
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
		verifyPreviewDocument(Documents.AU03, getTestSpecificTD("TestData_AU03"));
		verifyPreviewDocument(Documents.AHFMXX);
		verifyPreviewDocument(Documents.AAIQAZ);
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
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, Documents.AA06XX_AUTOSS, Documents.AA10XX, Documents.AA11AZ, Documents.AA43AZ, Documents.AA52AZ, 
				Documents.AAPDXX, Documents.AASR22, Documents.AAUBI, Documents.AAUBI1, Documents.ACPUBI, Documents.AHAPXX, Documents.AHRCTXXAUTO, 
				Documents.AU02, Documents.AU04, Documents.AU05, Documents.AU06, Documents.AU07, Documents.AU08, Documents.AU09, Documents.AU10);
		//TODO 
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
		
		mainApp().open(getLoginTD(UserGroups.B31));
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, false, Documents.AA06XX_AUTOSS);
		odd_tab.saveAndExit();
		mainApp().close();
		
		mainApp().open(getLoginTD(UserGroups.QA));
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, Documents.AA06XX_AUTOSS);
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
		odd_tab.verify.documentsPresent(null, true, Documents.AA43AZ);
		//odd_tab.generateDocuments(Documents.AA43AZ);
		odd_tab.selectDocuments(Documents.AA43AZ);
		odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN).click(Waiters.AJAX);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, Documents.AA43AZ);	
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
		odd_tab.verify.documentsPresent(null, true, Documents.AAUBI, Documents.ACPUBI, Documents.AAUBI1);
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null,
				Documents.AAUBI, Documents.ACPUBI, Documents.AAUBI1);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, Documents.AAUBI, Documents.ACPUBI, Documents.AAUBI1);
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
		odd_tab.verify.documentsPresent(Documents.AAPDXX);
		odd_tab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, Documents.AAPDXX);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AAPDXX);
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
		odd_tab.verify.documentsPresent(null, true, Documents.AASR22);
		odd_tab.generateDocuments(Documents.AASR22);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, Documents.AASR22);	
		verifyOneDocumentGenerated(policyNumber, "AASR22");
	}

	private void verifyPreviewDocument(DocGenEnum.Documents document) {
		verifyPreviewDocument(document, null);
	}
	
	private void verifyPreviewDocument(DocGenEnum.Documents document, TestData td) {
		odd_tab.previewDocuments(td, document);
		//odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.PREVIEW_DOCUMENTS_BTN).click(Waiters.AJAX);
		WebDriverHelper.switchToDefault();
		//TODO verify generated doc contains <doc:EventName>ADHOC_DOC_ON_DEMAND_PREVIEW</doc:EventName>		
		
		odd_tab.unselectDocuments(document);
	}
	
	private void verifyOneDocumentGenerated(String policyNum, String document) {
		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, document, "ADHOC_DOC_ON_DEMAND_GENERATE");
		assertThat(DBService.get().getValue(query).map(Integer::parseInt)).hasValue(1);
	}
}
