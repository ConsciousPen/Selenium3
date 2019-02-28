package aaa.modules.regression.document_fulfillment.auto_ss;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;
import aaa.helpers.docgen.AaaDocGenEntityQueries;

public class PasDoc_AdhocGODDcommon extends AutoSSBaseTest {
	
	private GenerateOnDemandDocumentActionTab odd_tab = new GenerateOnDemandDocumentActionTab();
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario1(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		//TODO 
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(odd_tab.getAssetList().getAsset(AutoSSMetaData.GenerateOnDemandDocumentActionTab.DELIVERY_METHOD)).isPresent();
		});		
		odd_tab.saveAndExit();
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc2 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC2").resolveLinks());
		createPolicy(td_sc2);
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		//TODO 
		odd_tab.saveAndExit();
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		log.info("PAS DOC: Policy created with #" + PolicySummaryPage.getPolicyNumber());
		
		policy.policyDocGen().start();
		//TODO 
		odd_tab.saveAndExit();
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		log.info("PAS DOC: Scenario 4: Privilege 'Agent Advise Memo Document Operation': Created Policy#" + policyNumber);		
		mainApp().close();
		
		mainApp().open(getLoginTD(UserGroups.B31));
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, false, Documents.AA06XX);
		odd_tab.saveAndExit();
		mainApp().close();
		
		mainApp().open(getLoginTD(UserGroups.QA));
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, Documents.AA06XX);
		odd_tab.saveAndExit();
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDrivers").resolveLinks());
		String policyNumber = createPolicy(td_sc5);
		log.info("PAS DOC: Scenario 5: Several Excluded Drivers: Created Policy#" + policyNumber);
		
		DocGenHelper.clearDocGenFolders();
		policy.policyDocGen().start();
		odd_tab.verify.documentsPresent(null, true, Documents.AA43AZ);
		odd_tab.generateDocuments(Documents.AA43AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNumber, Documents.AA43AZ);	
		verifyOneDocumentGenerated(policyNumber, "AA43AZ");
		
		String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "AA43AZ", "ADHOC_DOC_ON_DEMAND_GENERATE");
		//assertThat(DocGenHelper.getDocumentDataSectionsByName("DriverDetails", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(2).getDataElementChoice().getTextField()).isEqualTo("Driver1");
		//assertThat(DocGenHelper.getDocumentDataSectionsByName("DriverDetails", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(3).getDataElementChoice().getTextField()).isEqualTo("Driver2");
		CustomSoftAssertions.assertSoftly(softly -> {
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrLstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo("Driver1");
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrLstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(1).getDataElementChoice().getTextField()).isEqualTo("Driver2");
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrFrstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(0).getDataElementChoice().getTextField()).isEqualTo("Excluded1");
			assertThat(DocGenHelper.getDocumentDataElemByName("DrvrFrstNm", Documents.AA43AZ, query).get(0).getDocumentDataElements().get(1).getDataElementChoice().getTextField()).isEqualTo("Excluded2");
		});
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
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

	
	private void verifyOneDocumentGenerated(String policyNum, String document) {
		String query = String.format(AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNum, document, "ADHOC_DOC_ON_DEMAND_GENERATE");
		assertThat(DBService.get().getValue(query).map(Integer::parseInt)).hasValue(1);
	}
}
