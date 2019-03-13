package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssertions;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.waiters.Waiters;

//import toolkit.db.DBService;

public class PasDoc_AdhocPreBind extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1_2(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		
		//1.1, 2.1, 2.2
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), DocumentsAndBindTab.class);		
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			//Verify documents in Available for Printing section
			//2.1
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			//Verify enabled buttons
			//1.1
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_DOCUMENTS)).isEnabled();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)).isEnabled();
			//Verify documents in Required to Bind section
			//2.2
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();

		//2.3 - 2.6
		fillFromDriverToDocumentsAndBindTab(getTestSpecificTD("TestData_SC2"));		
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(softly);
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();

		//2.7
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_SC2"), DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		//2.8
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent(false);
			verifyDriverDocsPresent(softly);
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		
		//2.9 - 2.12
		removeAdditionalData(getTestSpecificTD("TestData_Endorsement_SC2"));		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(false, softly);
			verifyVehicleDocsPresent(false, softly);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();		
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		
		//2.13
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		//2.14
		policy.renew().perform();	
		//2.15 - 2.18
		fillAdditionalData(getTestSpecificTD("TestData_SC2"));		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(softly);
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		
		//2.19
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Renewal_SC2"));
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmRenewal.confirm();
		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();
		
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());	
	}	


	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario3(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		
		//3.1
		TestData td = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class); 
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
			//Required To Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		
		//3.2
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_SC3"), DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		//3.3
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent(false);
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
			//Required To Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		//3.4
		policy.renew().perform();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			//Available for Printing
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE)).isPresent(false);
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTOPAY_AUTHORIZATION_FORM)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
			//Required To Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NON_OWNER_AUTOMOBILE_ENDORSEMENT)).isPresent();
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		documentsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());
	}
	
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		//4.1
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		
		//4.2 - 4.5
		fillAdditionalData(getTestSpecificTD("TestData_SC2"));
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(softly);			
			verifyVehicleDocsPresent(softly);
			verifyCoverageDocPresent(softly);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		//4.6
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Renewal_SC2"));
		//documentsAndBindTab.submitTab();
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmEndorsementPurchase.confirm();
		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		//4.6
		policy.renew().perform();
		//4.7 - 4.10
		removeAdditionalData(getTestSpecificTD("TestData_Endorsement_SC2"));		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(false, softly);			
			verifyVehicleDocsPresent(false, softly);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		DocumentsAndBindTab.btnGenerateDocuments.click();
		//4.11
		documentsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());
		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc5, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Quote created #" + quoteNumber);
		
		//5.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs_Quote"), quoteNumber, Documents.AAIQAZ, Documents.AA11AZ, 
				Documents.AHAPXX, Documents.AA52AZ, Documents.AA43AZ, Documents.AATSXX, Documents.AAUBI, Documents.ACPPNUBI, Documents.AAUBI1);	
		//Verify docs generation
		//5.2
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAIQAZ"), quoteNumber, Documents.AAIQAZ);
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		//5.3
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), quoteNumber, Documents.AA11AZ);
		//5.4
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), quoteNumber, Documents.AHAPXX);
		//5.5
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), quoteNumber, Documents.AA52AZ);
		//5.6
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), quoteNumber, Documents.AA43AZ);
		//5.7
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), quoteNumber, Documents.AATSXX);
		//5.8
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), quoteNumber, Documents.AAUBI);
		//5.9
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), quoteNumber, Documents.ACPPNUBI);
		//5.10
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), quoteNumber, Documents.AAUBI1);
		//5.1b
		//log.info("Distribution Channel is: " + getDistributionChannel(quoteNumber));
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Policy created #" + policyNumber);
		
		//5.12
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs"), quoteNumber, Documents.AA11AZ, Documents.AHAPXX, 
				Documents.AA52AZ, Documents.AA43AZ, Documents.AATSXX, Documents.AAUBI, Documents.ACPPNUBI, Documents.AAUBI1);	
		//Verify docs generation
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), policyNumber, Documents.AA11AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), policyNumber, Documents.AHAPXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), policyNumber, Documents.AA52AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), policyNumber, Documents.AA43AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), policyNumber, Documents.AATSXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), policyNumber, Documents.AAUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), policyNumber, Documents.ACPPNUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), policyNumber, Documents.AAUBI1);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//5.13
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs"), quoteNumber, Documents.AA11AZ, Documents.AHAPXX, 
				Documents.AA52AZ, Documents.AA43AZ, Documents.AATSXX, Documents.AAUBI, Documents.ACPPNUBI, Documents.AAUBI1);	
		//Verify docs generation
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), policyNumber, Documents.AA11AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), policyNumber, Documents.AHAPXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), policyNumber, Documents.AA52AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), policyNumber, Documents.AA43AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), policyNumber, Documents.AATSXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), policyNumber, Documents.AAUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), policyNumber, Documents.ACPPNUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), policyNumber, Documents.AAUBI1);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario6(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc6 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());	
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc6, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		//6.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), quoteNumber, Documents.AA41XX);		
		//log.info("Distribution Channel is: " + getDistributionChannel(quoteNumber));
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Policy created #" + policyNumber);
		
		//6.2
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), policyNumber, Documents.AA41XX);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//6.3
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), policyNumber, Documents.AA41XX);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario7(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc7, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		//7.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		
		//Verify generation of separate doc
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		//7.2
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAIQAZ"), false);
		//7.3
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		//7.4
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		//7.5
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		//7.6
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		//7.7
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		//7.8
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), false);
		//7.9
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		//7.10
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);
		//7.12
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//7.12
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);		
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//7.13
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());	
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), true);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc8 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());	
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc8, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		//8.1
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//8.2
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		//8.3
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());
	}
	
	/*
	private String getDistributionChannel(String policyNumber) {
		String getDistributionChannelFromDB = "select * from aaadocgenentity " 
				+ "where entityid in (select id from policysummary where policynumber = '%s') order by id";
		return DBService.get().getValue(String.format(getDistributionChannelFromDB, policyNumber)).get();
	}
	*/
	
	private void generateAndVerifyDoc(TestData td_doc, String policyNum, DocGenEnum.Documents... documents) {
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.fillTab(td_doc);
		DocumentsAndBindTab.btnGenerateDocuments.click();
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(true, false, policyNum, documents);		
	}
	
	private void generateESignatureDocs(TestData td_doc, boolean isActiveBtn) {
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.fillTab(td_doc);
		CustomAssertions.assertThat(docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)).isPresent(isActiveBtn);
		if (isActiveBtn) {
			DocumentsAndBindTab.btnGenerateESignaturaDocuments.click(Waiters.AJAX);
			CustomAssertions.assertThat(docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ENTER_RECIPIENT_EMAIL_ADDRESS_DIALOG_PASDOC)).isPresent();
			//docsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS).setValue("test@email.com");
			//docsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
			docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ENTER_RECIPIENT_EMAIL_ADDRESS_DIALOG_PASDOC).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS).setValue("test@email.com");
			docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ENTER_RECIPIENT_EMAIL_ADDRESS_DIALOG_PASDOC).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
			
			Page.dialogConfirmation.buttonOk.click();
		}
	}
	
	private void fillAdditionalData(TestData td) {
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(td, DriverTab.class, PremiumAndCoveragesTab.class, true);
	}
	
	private void fillFromDriverToDocumentsAndBindTab(TestData td) {
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class);
	}
	
	private void removeAdditionalData(TestData td) {
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.removeRow(3);
		DriverTab.tableDriverList.removeRow(2);
		
		NavigationPage.toViewTab(AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.removeRow(2);
		
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().fillTab(td);
		new PremiumAndCoveragesTab().calculatePremium();
	}
	
	private void verifyDriverDocsPresent(boolean value, ETCSCoreSoftAssertions softly) {
		//Docs in Available For Printing section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NAMED_DRIVER_EXCLUSION_ELECTION)).isPresent(value);			
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.CRITICAL_INFORMATION_FOR_TEENAGE_DRIVERS_AND_THEIR_PARENTS)).isPresent(value);
		//Doc in Required to Bind section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION)).isPresent(value);
	}
	
	private void verifyDriverDocsPresent(ETCSCoreSoftAssertions softly) {
		verifyDriverDocsPresent(true, softly);
	}
	
	private void verifyVehicleDocsPresent(boolean value, ETCSCoreSoftAssertions softly) {
		//Docs in Available for Printing section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS)).isPresent(value);
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT)).isPresent(value);
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ACP_SMARTTRECK_SUBSCRIPTION_TERMS)).isPresent(value);
		//Doc in Required to Bind section
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent(value);
	}
	
	private void verifyVehicleDocsPresent(ETCSCoreSoftAssertions softly) {
		verifyVehicleDocsPresent(true, softly);
	}
	
	private void verifyCoverageDocPresent(ETCSCoreSoftAssertions softly) {
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
		
		softly.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
	}
	
}
