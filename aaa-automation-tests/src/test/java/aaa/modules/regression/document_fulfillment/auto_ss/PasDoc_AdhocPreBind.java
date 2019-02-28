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
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
//import toolkit.db.DBService;
import toolkit.verification.CustomAssertions;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.waiters.Waiters;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;

public class PasDoc_AdhocPreBind extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario1_2(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), DocumentsAndBindTab.class);
		
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
			//Verify enabled buttons
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_DOCUMENTS)).isEnabled();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)).isEnabled();
			//Required to Bind
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AUTO_INSURANCE_APPLICATION)).isPresent();
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});

		fillFromDriverToDocumentsAndBindTab(getTestSpecificTD("TestData_SC2"));		
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent();
			verifyVehicleDocsPresent();
			verifyCoverageDocPresent();
		});

		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_SC2"), DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent();
			verifyVehicleDocsPresent();
			verifyCoverageDocPresent();
		});
		
		removeAdditionalData(getTestSpecificTD("TestData_Endorsement_SC2"));
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(false);
			verifyVehicleDocsPresent(false);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();		
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		//Renewal
		policy.renew().perform();		
		fillAdditionalData(getTestSpecificTD("TestData_SC2"));
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent();
			verifyVehicleDocsPresent();
			verifyCoverageDocPresent();
		});
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
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		
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
		
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_SC3"), DocumentsAndBindTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();		
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
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
		documentsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
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
		documentsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());
	}
	
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario4(@Optional("") String state) {	
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		//Endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		fillAdditionalData(getTestSpecificTD("TestData_SC2"));
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent();			
			verifyVehicleDocsPresent();
			verifyCoverageDocPresent();
		});
		documentsAndBindTab.fillTab(getTestSpecificTD("TestData_Renewal_SC2"));
		//documentsAndBindTab.submitTab();
		DocumentsAndBindTab.btnPurchase.click();
		DocumentsAndBindTab.confirmEndorsementPurchase.confirm();
		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		errorTab.submitTab();
		log.info("TEST: Endorsement created for policy #" + PolicySummaryPage.getPolicyNumber());
		
		policy.renew().perform();
		removeAdditionalData(getTestSpecificTD("TestData_Endorsement_SC2"));
		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		CustomSoftAssertions.assertSoftly(softly -> {
			verifyDriverDocsPresent(false);			
			verifyVehicleDocsPresent(false);
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
			
			softly.assertThat(documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
					AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent(false);
		});
		documentsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy #" + PolicySummaryPage.getPolicyNumber());
		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario5(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc5 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc5, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Quote created #" + quoteNumber);
		
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateAndVerifyDoc(getTestSpecificTD("TestData_GenAllDocs_Quote"), quoteNumber, Documents.AAIQAZ, Documents.AA11AZ, 
				Documents.AHAPXX, Documents.AA52AZ, Documents.AA43AZ, Documents.AATSXX, Documents.AAUBI, Documents.ACPPNUBI, Documents.AAUBI1);	
		//Verify docs generation
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAIQAZ"), quoteNumber, Documents.AAIQAZ);
		docsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AUTO_INSURANCE_QUOTE).setValue("No");
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA11XX"), quoteNumber, Documents.AA11AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AHAPXX"), quoteNumber, Documents.AHAPXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA52AZ"), quoteNumber, Documents.AA52AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA43AZ"), quoteNumber, Documents.AA43AZ);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AATSXX"), quoteNumber, Documents.AATSXX);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI"), quoteNumber, Documents.AAUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_ACPUBI"), quoteNumber, Documents.ACPPNUBI);
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AAUBI1"), quoteNumber, Documents.AAUBI1);
		//log.info("Distribution Channel is: " + getDistributionChannel(quoteNumber));
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Policy created #" + policyNumber);
		
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
		//DocGenHelper.checkPasDocEnabled(States.AZ, PolicyType.AUTO_SS);
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc6 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());	
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc6, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		String quoteNumber = PolicySummaryPage.getPolicyNumber();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
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
		
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateAndVerifyDoc(getTestSpecificTD("TestData_Gen_AA41XX"), policyNumber, Documents.AA41XX);
		//log.info("Distribution Channel is: " + getDistributionChannel(policyNumber));
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
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
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc7 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC5").resolveLinks());
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc7, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAIQAZ"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);
		
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		//generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAIQAZ"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);		
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
		policy.renew().start();
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();		
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		//Verify generation all docs
		generateESignatureDocs(getTestSpecificTD("TestData_GenAllDocs"), true);		
		//Verify generation of separate doc
		//generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAIQAZ"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA11XX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AHAPXX"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA52AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA43AZ"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AATSXX"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI"), true);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_ACPUBI"), false);
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AAUBI1"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Renewal created for policy#" + PolicySummaryPage.getPolicyNumber());	
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario8(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_sc8 = getPolicyTD().adjust(getTestSpecificTD("TestData_SC3").resolveLinks());	
		
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_sc8, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab docsAndBindTab = new DocumentsAndBindTab();
		docsAndBindTab.saveAndExit();
		log.info("TEST: Quote created #" + PolicySummaryPage.getPolicyNumber());
		
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		new PurchaseTab().fillTab(getPolicyTD());
		new PurchaseTab().submitTab();
		log.info("TEST: Policy created #" + PolicySummaryPage.getPolicyNumber());
		
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().calculatePremium();	
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		generateESignatureDocs(getTestSpecificTD("TestData_Gen_AA41XX"), true);
		docsAndBindTab.submitTab();
		log.info("TEST: Endorsement created for policy#" + PolicySummaryPage.getPolicyNumber());
		
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
	
	private void verifyDriverDocsPresent(boolean value) {
		//Docs in Available For Printing section
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.NAMED_DRIVER_EXCLUSION_ELECTION)).isPresent(value);			
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.CRITICAL_INFORMATION_FOR_TEENAGE_DRIVERS_AND_THEIR_PARENTS)).isPresent(value);
		//Doc in Required to Bind section
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.NAMED_DRIVER_EXCLUSION)).isPresent(value);
	}
	
	private void verifyDriverDocsPresent() {
		verifyDriverDocsPresent(true);
	}
	
	private void verifyVehicleDocsPresent(boolean value) {
		//Docs in Available for Printing section
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_USAGE_BASED_INSURANCE_PROGRAM_TERMS_AND_CONDITIONS)).isPresent(value);
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT)).isPresent(value);
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.ACP_SMARTTRECK_SUBSCRIPTION_TERMS)).isPresent(value);
		//Doc in Required to Bind section
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.AAA_INSURANCE_WITH_SMARTTRECK_ACKNOWLEDGEMENT_OF_TERMS)).isPresent(value);
	}
	
	private void verifyVehicleDocsPresent() {
		verifyVehicleDocsPresent(true);
	}
	
	private void verifyCoverageDocPresent() {
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DOCUMENTS_FOR_PRINTING).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
		
		CustomAssertions.assertThat(new DocumentsAndBindTab().getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND).getAsset(
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.UNINSURED_AND_UNDERINSURED_MOTORIST_COVERAGE_SELECTION)).isPresent();
	}
	
}
