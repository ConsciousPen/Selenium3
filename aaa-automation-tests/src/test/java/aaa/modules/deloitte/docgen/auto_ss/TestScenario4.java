package aaa.modules.deloitte.docgen.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.verification.CustomAssert;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting;
import aaa.main.metadata.policy.AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

public class TestScenario4 extends AutoSSBaseTest {
	private DocumentsAndBindTab documentsAndBindTab = policy.getDefaultView().getTab(DocumentsAndBindTab.class);
	private DriverTab driverTab = policy.getDefaultView().getTab(DriverTab.class);
	private GenerateOnDemandDocumentActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	
	private String policyNumber;
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();

		// Create quote
		String quoteNumber = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation").resolveLinks()));

		/*
		 * Verify: In "Documents Available for Printing" section,
		 * "Consumer Information Notice" form is listed, selected and enabled
		 * (radio button="Yes")
		 */
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		verifyConsumerInformationNoticeValue();

		/*
		 * Set Insurance Score = Yes, order reports, insurance score value will
		 * be = 10, Verify: In "Documents Available for Printing" section
		 * "Consumer Information Notice" form is listed, selected and enabled
		 * (radio button="Yes")
		 */
		NavigationPage.toViewTab(AutoSSTab.RATING_DETAIL_REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather1"), RatingDetailReportsTab.class, DocumentsAndBindTab.class);
		verifyConsumerInformationNoticeValue();

		/*
		 * Set Insurance Score = 997 (top level) Verify:
		 * "Consumer Information Notice" form is absent
		 */
		NavigationPage.toViewTab(AutoSSTab.RATING_DETAIL_REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather2"), RatingDetailReportsTab.class, DocumentsAndBindTab.class);
		verifyConsumerInformationNoticeAbsent();

		/*
		 * Add new driver that has incident (MVR) Verify:
		 * "Consumer Information Notice" form is available
		 */
		NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather3"), DriverTab.class, DocumentsAndBindTab.class);
		if (!getState().equals(States.VA)) {
			NavigationPage.toViewTab(AutoSSTab.DRIVER.get());
			DriverTab.tableDriverList.selectRow(5);
			driverTab.getActivityInformationAssetList().getAsset(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER).setValue("Yes");
			policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather4"), DriverTab.class, DocumentsAndBindTab.class);
		}
		verifyConsumerInformationNoticeValue();
		
		/* Update Premium and Coverages tab
		 * To get document: Proof of Prior Insurance document, set 'Bodily Injury Liability' = $50,000/$100,000
		 * To get document: Proof of purchase date (bill of sale) for new vehicle(s), set 'New Car Added Protection' = Yes and 'Purchase Date' = /today-20d for the 4th vehicle
		 * To get document: Proof of equivalent new car added protection with prior carrier for new vehicle(s), set 'New Car Added Protection' = Yes and 'Purchase Date' = /today-40d for the 5th vehicle
		 */
		NavigationPage.toViewTab(AutoSSTab.PREMIUM_AND_COVERAGES.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_QuoteDataGather5"), PremiumAndCoveragesTab.class, DocumentsAndBindTab.class);
		documentsAndBindTab.saveAndExit();
		
		/* Check Documents in 'Generate on Demand Document' screen for quote */
		policy.quoteDocGen().start();
		switch (getState()) {
		case States.VA:
			docgenActionTab.verify.documentsPresent(Documents.AA11VA, Documents.AA52VA, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03);
			break;
		case States.AZ:
			docgenActionTab.verify.documentsPresent(Documents.AA11AZ, Documents.AA52AZ_UPPERCASE, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43AZ);
			break;
		case States.IN:
			docgenActionTab.verify.documentsPresent(Documents.AA11IN, Documents.AA52IN, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43IN);
			break;
		case States.OH:
			docgenActionTab.verify.documentsPresent(Documents.AA11OH, Documents.AA52OH, Documents.AAIQ.setState(getState()), Documents.AHFMXX, Documents.AU03, Documents.AA43OH);
			break;
		}
		docgenActionTab.verify.documentsPresent(false, Documents.AHPNXX);
		docgenActionTab.cancel();
		
		/* Generate documents Test */
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.BTN_GENERATE_DOCUMENTS).click();
		WebDriverHelper.switchToWindow(currentHandle);
		switch(getState()){
		case States.AZ:
			DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AA11AZ, Documents.AA43AZ, Documents.AHAUXX, Documents.AHAPXX);
			break;
		case States.IN:
			DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AA11IN, Documents.AA43IN, Documents.AHAUXX, Documents.AHAPXX);
			break;
		case States.OH:
			DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AA11OH, Documents.AA43OH, Documents.AHAUXX, Documents.AHAPXX);
			break;
		case States.VA:
			DocGenHelper.verifyDocumentsGenerated(quoteNumber, Documents.AA11VA, Documents.AHAPXX, Documents.AAAUVA);
			break;
		}
		documentsAndBindTab.cancel();
		
		/* Purchase */
		policy.calculatePremiumAndPurchase(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase").resolveLinks()));
		policyNumber = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		
		/*
		 * Check Documents in 'Generate on Demand Document' screen for policy
		 */
		policy.policyDocGen().start();
		switch(getState()){
		case States.VA:
			docgenActionTab.verify.documentsEnabled(Documents.AA11VA, Documents.AA52VA, Documents.AA10XX, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605005_SELECT, Documents._605004, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
			docgenActionTab.verify.documentsEnabled(false, Documents.AHFMXX);
			break;
		case States.OH:
			docgenActionTab.verify.documentsEnabled(Documents.AA11OH, Documents.AA10XX, Documents.AASR22OH, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
			docgenActionTab.verify.documentsEnabled(false, Documents.AA52OH);
			break;
		case States.IN:
			docgenActionTab.verify.documentsEnabled(Documents.AA11IN, Documents.AA10XX, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents._605005_SELECT, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
			docgenActionTab.verify.documentsEnabled(false, Documents.AA52IN, Documents.AHFMXX);
			break;
		case States.AZ:
			docgenActionTab.verify.documentsEnabled(Documents.AA11AZ, Documents.AA10XX, Documents.AASR22, Documents.AHAPXX, Documents.AHRCTXXAUTO, Documents.AA06XX_AUTOSS, Documents._605004, Documents._605005_SELECT, Documents.AU02, Documents.AU07, Documents.AU09, Documents.AU10, Documents.AU08, Documents.AU06, Documents.AU04, Documents.AU05);
			docgenActionTab.verify.documentsEnabled(false, Documents.AA52AZ_UPPERCASE, Documents.AHFMXX);
			break;
		}
		docgenActionTab.cancel();
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		
		/* Check xml */
		switch(getState()){
		case States.VA:
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA02VA, Documents.AHNBXX);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AASR22);
			break;
		case States.OH:
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA02OH, Documents.AA43OH, Documents.AHNBXX);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AASR22);
			break;
		case States.IN:
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA02IN, Documents.AA43IN, Documents.AHNBXX);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AASR22);
			break;
		case States.AZ:
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA02AZ, Documents.AA43AZ, Documents.AHNBXX);
			DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AASR22);
			break;
		}
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_CopyFromPolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		
		SearchPage.openPolicy(policyNumber);
		/* Copy from policy and make some update */
		policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_CopyFromPolicy1"), GeneralTab.class, true);
		policy.getDefaultView().fillFromTo(getTestSpecificTD("TestData_CopyFromPolicy1"), DriverTab.class, DocumentsAndBindTab.class);
		
		/*
		 * Consumer Information Notice (not present);
		 * Named Driver Exclusion (not present)
		 * SR22 Financial Responsibility Form (not present)
		 * Canadian MVR for (driver) 
		 * Proof of Defensive Driver course completion 
		 * Proof of Good Student Discount 
		 * Proof of Smart Driver 
		 * Proof of equivalent new car added protection with prior carrier for new vehicle
		 */
		verifyConsumerInformationNoticeAbsent();
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.NAMED_DRIVER_EXCLUSION).verify.present(false);
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(RequiredToIssue.PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION).verify.present(false);
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT).verify.present(false);
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(RequiredToIssue.PROOF_OF_SMART_DRIVER_COURSE_COMPLETION).verify.present(false);
		documentsAndBindTab.getRequiredToIssueAssetList().getAsset(RequiredToIssue.PROOF_OF_EQUIVALENT_NEW_CAR_ADDED_PROTECTION_WITH_PRIOR_CARRIER_FOR_NEW_VEHICLES).verify.present(false);
		documentsAndBindTab.saveAndExit();
		String copiedQuoteNumber = PolicySummaryPage.getPolicyNumber();
		
		/* Generate documents */
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.BTN_GENERATE_DOCUMENTS).click();
		
		/* Check xml */
		switch(getState()){
		case States.AZ:
			DocGenHelper.verifyDocumentsGenerated(copiedQuoteNumber, Documents.AA11AZ, Documents.AHAPXX);
			DocGenHelper.verifyDocumentsGenerated(false, copiedQuoteNumber, Documents.AHAUXX, Documents.AA43AZ, Documents.AASR22, Documents.AAPNXX, Documents.AA02AZ);
			break;
		case States.IN:
			DocGenHelper.verifyDocumentsGenerated(copiedQuoteNumber, Documents.AA11IN, Documents.AHAPXX);
			DocGenHelper.verifyDocumentsGenerated(false, copiedQuoteNumber, Documents.AHAUXX, Documents.AA43IN, Documents.AASR22, Documents.AAPNXX, Documents.AA02IN);
			break;
		case States.OH:
			DocGenHelper.verifyDocumentsGenerated(copiedQuoteNumber, Documents.AA11OH, Documents.AHAPXX);
			DocGenHelper.verifyDocumentsGenerated(false, copiedQuoteNumber, Documents.AHAUXX, Documents.AA43OH, Documents.AASR22, Documents.AAPNXX, Documents.AA02OH);
			break;
		case States.VA:
			DocGenHelper.verifyDocumentsGenerated(copiedQuoteNumber, Documents.AA11VA, Documents.AHAPXX);
			DocGenHelper.verifyDocumentsGenerated(false, copiedQuoteNumber, Documents.AHAUXX, Documents.AASR22, Documents.AAPNXX, Documents.AA02VA);
			break;
		}
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_CopyFromPolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		
		/* Copy from policy and make some update */
		SearchPage.openPolicy(policyNumber);
		policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_CopyFromPolicy2"), PurchaseTab.class, true);
		policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
		String copiedPolicyNumber = PolicySummaryPage.getPolicyNumber();
		
		/* Do Endorsement action */
		policy.createEndorsement(getPolicyTD("Endorsement", "TestData").adjust(getTestSpecificTD("TestData_Endorsement")));
		
		/* Check xml */
		switch(getState()){
		case States.AZ:
			DocGenHelper.verifyDocumentsGenerated(copiedPolicyNumber, Documents.AA02AZ);
			DocGenHelper.verifyDocumentsGenerated(false, copiedPolicyNumber, Documents.AHAUXX, Documents.AHAPXX, Documents.AA43AZ, Documents.AASR22, Documents.AAPNXX);
			break;
		case States.IN:
			DocGenHelper.verifyDocumentsGenerated(copiedPolicyNumber, Documents.AA02IN);
			DocGenHelper.verifyDocumentsGenerated(false, copiedPolicyNumber, Documents.AHAUXX, Documents.AHAPXX, Documents.AA43IN, Documents.AASR22, Documents.AAPNXX);
			break;
		case States.OH:
			DocGenHelper.verifyDocumentsGenerated(copiedPolicyNumber, Documents.AA02OH);
			DocGenHelper.verifyDocumentsGenerated(false, copiedPolicyNumber, Documents.AHAUXX, Documents.AHAPXX, Documents.AA43OH, Documents.AASR22, Documents.AAPNXX);
			break;
		case States.VA:
			DocGenHelper.verifyDocumentsGenerated(copiedPolicyNumber, Documents.AA02VA);
			DocGenHelper.verifyDocumentsGenerated(false, copiedPolicyNumber, Documents.AHAUXX, Documents.AHAPXX, Documents.AASR22, Documents.AAPNXX);
			break;
		}
	}
	
	private void verifyConsumerInformationNoticeValue() {
		switch (getState()) {
		case States.VA:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.ADVERSE_ACTION_UNDERWRITING_DECISION_NOTICE).verify.value("Yes");
			break;
		default:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.CONSUMER_INFORMATION_NOTICE).verify.value("Yes");
			break;
		}
	}

	private void verifyConsumerInformationNoticeAbsent() {
		switch (getState()) {
		case States.VA:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.ADVERSE_ACTION_UNDERWRITING_DECISION_NOTICE).verify.present(false);
			break;
		default:
			documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(DocumentsForPrinting.CONSUMER_INFORMATION_NOTICE).verify.present(false);
			break;
		}
	}
}
