package aaa.modules.docgen.home_ss.ho3;

import org.testng.annotations.Test;

import toolkit.webdriver.BrowserController;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeSSHO3BaseTest {
	/**
	 * <pre>
	 * TC Steps:
	 * Create an HO3 quote;
	 * Rate the quote.
	 * Save and exit the quote, Go to On-Demand Documents tab,
	 * Verify state of forms on the quote:
	 * AHAUXX - enabled (except VA)
	 * HSAUDVA - enabled (only VA)
	 * AHFMXX - enabled
	 * HS11   - enabled
	 * HSIQXX - enabled
	 * HSRFIXX - disabled
	 * HSU01XX - disabled
	 * HSU02XX - disabled
	 * HSU03XX - enabled
	 * HSU04XX - enabled
	 * HSU05XX - enabled
	 * HSU06XX - enabled
	 * HSU07XX - disabled
	 * HSU08XX - enabled
	 * HSU09XX - disabled
	 * Verify that below forms aren't present:
	 * 438 BFUNS
	 * AHRCTXX
	 * AHPNXX
	 * HS02
	 * AHNBXX
	 * HSEIXX
	 * HSESUT
	 * Select HSIQXX form and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * HSIQXX
	 * AHPNXX
	 * Select HS11UT,AHFMXX,HSILXX( HSAUDVA - enabled (only VA)) forms and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * HS11
	 * AHFMXX
	 * HSILXX
	 * AHPNXX
	 * Select HSU03XX,HSU04XX,HSU05XX,HSU06XX,HSU08XX forms and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * HSU03XX
	 * HSU04XX
	 * HSU05XX
	 * HSU06XX
	 * HSU08XX
	 * AHAUXX (except VA)
	 * Go to Edit Mode and override insurance score, set it to 926
	 * Save and exit quote without rating
	 * Go to On-Demand Documents tab.
	 * Verify that forms  AHFMXX are disabled
	 * Verify that AHAUXX form is absent
	 * Go to Edit Mode and override insurance score, set it to 920
	 * Rate and save quote.
	 * </pre>
	 * 
	 * Req: AHAUXX - 15377: US CL GD-94 Consumer Information Notice 17274:US VA
	 * GD-94 DO NOT Generate Consumer Information Notice AHFMXX - 15379: US CL
	 * GD-96 Generate Fax Memorandum Document HS11 - 15209: US CL GD-77 Generate
	 * Application Document All Products HSIQXX - 15207: US CL GD-76 Generate
	 * Quote Document All Products HSRFIXX - 16187: US CL GD-124 Generate
	 * HSRFIXX Request For Information HSU03XX - 15275: US CL GD-56 Generate
	 * Underwriting Letter HSU03 Customer Decline HSU04XX - 15276: US CL GD-57
	 * Generate Underwriting Letter HSU04 Free Form to Insured HSU05XX - 15277:
	 * US CL GD-58 Generate Undewriting Letter HSU05 Free Form to Other HSU06XX
	 * - 15278: US CL GD-59 Generate Underwriting Letter HSU06 Free Form to
	 * Producer HSU08XX - 15282: US CL GD-62 Generate Underwriting Letter HSU08
	 * Request Add'l Info
	 */

	@Test
	public void testQuoteDocuments() {
		mainApp().open();
		createCustomerIndividual();
		String quoteNum = createQuote();
		GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		policy.quoteDocGen().start();	
		
		if (getState().equals("VA")) {
			documentActionTab.verify.documentsPresent(DocGenEnum.Documents.HSAUDVA);
		} else {
			documentActionTab.verify.documentsPresent(DocGenEnum.Documents.AHAUXX);
		}
		documentActionTab.verify.documentsPresent(
				Documents.AHFMXX,
				Documents.HS11.setState(getState()),
				Documents.HSIQXX,
				Documents.HSRFIXX,
				Documents.HSU01XX,
				Documents.HSU02XX,
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06XX,
				Documents.HSU07XX,
				Documents.HSU08XX,
				Documents.HSU09XX);
		
		documentActionTab.verify.documentsPresent(
				false,
				Documents._438BFUNS,
				Documents.AHRCTXX,
				Documents.AHPNXX,
				Documents.HS02,
				Documents.AHNBXX,
//				Documents.HSEIXX, // TODO Present on the page, need to confirm the request
				Documents.HSES);
		
		documentActionTab.generateDocuments(Documents.HSIQXX);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSIQXX, Documents.AHPNXX);

		policy.quoteDocGen().start();
		if(getState().equals("VA"))
			documentActionTab.selectDocuments(Documents.HSAUDVA);
		documentActionTab.generateDocuments(
				Documents.HS11.setState(getState()), 
				Documents.AHFMXX, 
				Documents.HSILXX
				); 
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.HS11.setState(getState()), 
				Documents.AHFMXX, 
				Documents.HSILXX, 
				Documents.AHPNXX
				);
		if(getState().equals("VA"))
			DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSAUDVA);

		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"), 
				Documents.HSU03XX, 
				Documents.HSU04XX, 
				Documents.HSU05XX, 
				Documents.HSU06XX, 
				Documents.HSU08XX
				);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.HSU03XX, 
				Documents.HSU04XX, 
				Documents.HSU05XX, 
				Documents.HSU06XX, 
				Documents.HSU08XX
				);

		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride926"), ReportsTab.class, PropertyInfoTab.class, true);
		Tab.buttonSaveAndExit.click();
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(false, Documents.AHFMXX, Documents.HSILXX);
//		documentActionTab.verify.documentsPresent(false, Documents.AHAUXX); // TODO AHAUXX form is not absent, need to confirm the request
		documentActionTab.buttonCancel.click();
		
		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride920"), ReportsTab.class, PremiumsAndCoveragesQuoteTab.class);
		policy.getDefaultView().getTab(PremiumsAndCoveragesQuoteTab.class).calculatePremium();
		Tab.buttonSaveAndExit.click();
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(Documents.AHFMXX, Documents.HSILXX);
		if (!getState().equals("VA"))
			documentActionTab.verify.documentsPresent(Documents.AHAUXX);

		log.info("==========================================");
		log.info(getState() + " HO3 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
	}
	
	/**
	 * <pre>
	 * TC Steps:
	 * Open and Bind the quote from ho3QuoteDocuments test .
	 * Verify that below forms are generated in xml batch after bind:
	 * HS02
	 * AHNBXX
	 * HS0420
	 * Go to On-Demand Documents tab.
	 * Verify state and names of forms on the policy:
	 * 60 5005 - enabled
	 * AHFMXX - disabled
	 * AHRCTXX - enabled
	 * HS11UT - enabled
	 * HSEIXX - enabled
	 * HSILXX - enabled
	 * HSRFIXX - disabled
	 * HSU01XX - enabled
	 * HSU02XX - enabled
	 * HSU03XX - disabled
	 * HSU04XX - enabled
	 * HSU05XX - enabled
	 * HSU06XX - enabled
	 * HSU07XX - enabled
	 * HSU08XX - enabled
	 * HSU09XX - enabled
	 * Verify that below forms aren't present:
	 * AHAUXX
	 * HSIQXX
	 * AHPNXX
	 * 438 BFUNS
	 * HSES
	 * Select HS11UT form and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * HS11UT
	 * AHPNXX
	 * Select AHRCTXX,HSEIXX,HSILXX,HSU01XX,HSU09XX,60 5005  forms and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * AHRCTXX
	 * HSEIXX
	 * HSILXX
	 * HSU01XX
	 * HSU09XX
	 * 60 5005
	 * </pre>
	 * 
	 * Req: HSU07XX - 16238:US CL GD-60 Generate Underwriting letter HSU07 Non
	 * Renewal HSU01XX - 15272: US CL GD-53 Generate Underwriting Letter HSU01
	 * Advisory HSU02XX - 15274: US CL GD-55 Generate Underwriting Letter HSU02
	 * Cancellation HSU09XX - 15283: US CL GD-63 Generate Underwriting Letter
	 * HSU09 Uprate 438 BFUNS - 15210: US CL GD-01 Generate 438BFUNS Endorsement
	 * AHRCTXX - 15384: US CL GD-101 Generate Insured Receipt for Funds Received
	 * by Agent AHPNXX - 15382: US CL GD-99 Generate Privacy Information Notice
	 * HS02 - 16881: US CL GD-78 Generate Declaration Documents All Products;
	 * AHNBXX - 15381: US CL GD-98 Generate New Business Welcome Letter HSEIXX -
	 * 16184: US CL GD-121 Generate HSEIXX Evidence of Insurance HSESUT - 16200:
	 * US CL GD-119 Generate HSESXX Property Insurance Invoice HSILXX - 16185:US
	 * CL GD-122 Generate HSILXX Property Inventory List HS0420 - 15234: US CL
	 * GD-16 Generate HS 04 20 Endorsement 60 5005 - 15316 - US CL GD-73
	 * Generate Returning Payment Form
	 */
	@Test
	public void testPolicyDocuments() {
		mainApp().open();
		String currentHandle = getWindowHandle();
		String policyNum = getCopiedPolicy();
		
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.HS02, Documents.AHNBXX, Documents.HS0420);
		
		GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents.F605005, 
				Documents.AHRCTXX, 
				Documents.HS11.setState(getState()), 
				Documents.HSEIXX, 
				Documents.HSILXX, 
				Documents.HSU01XX,
//				Documents.HSU02XX  //TODO Actually HSU02XX is disabled, need to confirm the request
				Documents.HSU04XX,
				Documents.HSU06XX,
				Documents.HSU07XX,
				Documents.HSU08XX,
				Documents.HSU09XX
				);
		documentActionTab.verify.documentsEnabled(false, 
				Documents.AHFMXX, 
				Documents.HSRFIXX,
				Documents.HSU03XX
				);
		documentActionTab.verify.documentsPresent(false, 
//				Documents.AHAUXX // TODO Actually AHAUXX is present, need to confirm the request
				Documents.HSIQXX,
				Documents.AHPNXX,
				Documents._438BFUNS,
				Documents.HSES
				);
		
		documentActionTab.generateDocuments(Documents.HS11);
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.HS11, Documents.AHPNXX);
		
		switchToWindow(currentHandle);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
				Documents.AHRCTXX, 
				Documents.HSEIXX, 
				Documents.HSILXX, 
				Documents.HSU01XX, 
				Documents.HSU09XX
				);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents.AHRCTXX, 
				Documents.HSEIXX, 
				Documents.HSILXX, 
				Documents.HSU01XX, 
				Documents.HSU09XX
				);
	}
	
	/**
	 * <pre>
	 * Test steps:
	 * 1. Open policy which was created in ho3PolicyDocuments test;
	 * 2. Select "Cancellation" from "MoveTo"
	 * 3. Fill the cancellation dialogue (Cancellation reason = "New Business Recission - NSF on Down Payment" )
	 * 4. Confirm Cancellation.
	 * 5. Run DocGen Batch Job
	 * 6. Verify that AH60XXA form is generated
	 * # Req
	 * 15369 - US CL GD-86 Generate Rescission Notice Document
	 * </pre>
	 */
	@Test()
	public void testPolicyRescissionNoticeDocument() {
		mainApp().open();
		String policyNum = getCopiedPolicy();
		
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_NewBusinessRescissionNSF"));
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob); 
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, Documents.AH60XXA); 
	}
	
	/**
	 * <pre>
	 * TC Steps: Create HO3 quote:
	 *
	 * Set Payment Plan : Mortgagee Bill Add one morgagee // for HSES and
	 * 438BFUNS forms
	 *
	 * Set Fire department type = 'S - Subscription based' Subscription to fire
	 * department/station = 'Yes' Public protection class = 5 // for HSRFIXX
	 * form generation Construction type = "Frame" Proof of subscription to fire
	 * department = "No"
	 *
	 * Set Coverage A - Dwelling limit = 280 000 // for enabling legacy bundle,
	 * and all endorsement forms which are ISO replacement cost = 280 000 //
	 * included : HS0420,HS0435,HS0455,HS0465,HS0465,HS0495 InsuranceScore = 870
	 * // HS0906,HS0926,HS0931,HS0934,HS0965, HS0477
	 *
	 * Rate the quote. Save and exit quote, Go to On-Demand Documents tab,
	 * Verify state of forms on the quote: AHAUXX - enabled AHFMXX - enabled
	 * HS11 - enabled HSIQXX - enabled HSRFIXX - disabled HSU01XX - disabled
	 * HSU02XX - disabled HSU03XX - enabled HSU04XX - enabled HSU05XX - enabled
	 * HSU06XX - enabled HSU07XX - disabled HSU08XX - enabled HSU09XX - disabled
	 * Verify that below forms aren't present: 438 BFUNS AHRCTXX AHPNXX HS02
	 * AHNBXX HSEIXX HSESUT Bind the quote. Verify that below forms are
	 * generated in xml batch after bind: HS02 AHNBXX 438BFUNS HS0420 HS0435
	 * HS0455 HS0465 HS0495 HS0906 HS0926 HS0931 HS0934 HS0965 HS0477 Go
	 * To On-Demand Document Tab Verify state of forms on the policy: AHFMXX -
	 * enabled AHRCTXX - disabled HS11 - enabled HSEIXX - enabled HSES - enabled
	 * HSILXX - enabled HSRFIXX - enabled HSU01XX - enabled HSU02XX - enabled
	 * HSU03XX - disabled HSU04XX - enabled HSU05XX - enabled HSU06XX - enabled
	 * HSU07XX - enabled HSU08XX - enabled HSU09XX - enabled
	 *
	 * Verify that below forms aren't present: AHNBXX HSIQXX AHPNXX 438 BFUNS
	 * HS02
	 *
	 * Select forms on On-Demand Documents Tab : HSES HSRFIXX Press "Generate"
	 * button Verify that below forms are generated in xml batch: HSES HSRFIXX
	 *
	 * <pre>
	 * Req: AHAUXX - 15377: US CL GD-94 Consumer Information Notice AHFMXX -
	 * 15379: US CL GD-96 Generate Fax Memorandum Document HS11 - 15209: US CL
	 * GD-77 Generate Application Document All Products HSIQXX - 15207: US CL
	 * GD-76 Generate Quote Document All Products HSRFIXX - 16187: US CL GD-124
	 * Generate HSRFIXX Request For Information HSU01XX - 15272: US CL GD-53
	 * Generate Underwriting Letter HSU01 Advisory HSU02XX - 15274: US CL GD-55
	 * Generate Underwriting Letter HSU02 Cancellation HSU03XX - 15275: US CL
	 * GD-56 Generate Underwriting Letter HSU03 Customer Decline HSU04XX -
	 * 15276: US CL GD-57 Generate Underwriting Letter HSU04 Free Form to
	 * Insured HSU05XX - 15277: US CL GD-58 Generate Undewriting Letter HSU05
	 * Free Form to Other HSU06XX - 15278: US CL GD-59 Generate Underwriting
	 * Letter HSU06 Free Form to Producer HSU07XX - 16238:US CL GD-60 Generate
	 * Underwriting letter HSU07 Non Renewal HSU08XX - 15282: US CL GD-62
	 * Generate Underwriting Letter HSU08 Request Add'l Info HSU09XX - 15283: US
	 * CL GD-63 Generate Underwriting Letter HSU09 Uprate 438 BFUNS - 15210: US
	 * CL GD-01 Generate 438BFUNS Endorsement AHRCTXX - 15384: US CL GD-101
	 * Generate Insured Receipt for Funds Received by Agent AHPNXX - 15382: US
	 * CL GD-99 Generate Privacy Information Notice HS02 - 16881: US CL GD-78
	 * Generate Declaration Documents All Products; AHNBXX - 15381: US CL GD-98
	 * Generate New Business Welcome Letter HSEIXX - 16184: US CL GD-121
	 * Generate HSEIXX Evidence of Insurance HSESUT - 16200: US CL GD-119
	 * Generate HSESXX Property Insurance Invoice HSILXX - 16185:US CL GD-122
	 * Generate HSILXX Property Inventory List HS0420 - 15234: US CL GD-16
	 * Generate HS 04 20 Endorsement HS0435 - 15235: US CL GD-17 Generate HS 04
	 * 35 Endorsement HS0455 - 15244: US CL GD-27 Generate HS 04 55 Endorsement
	 * HS0465 - 15247: US CL GD-30 Generate HS 04 65 Endorsement HS0495 - 15253:
	 * US CL GD-34 Generate HS 04 95 Endorsement HS0906 - 15257: US CL GD-38
	 * Generate HS 09 04 Endorsement HS0926 - 15259: US CL GD-40 Generate HS 09
	 * 26 Endorsement HS0931 - 15260: US CL GD-41 Generate HS 09 31 Endorsement
	 * HS0934 - 15261: US CL GD-42 Generate HS 09 34 Endorsement HS0965 - 15800:
	 * US CL GD-44 Generate HS 09 65 Endorsement HS0477 - 15248: US CL GD-31
	 * Generate HS 04 77 Endorsement For Bundles new stories :
	 */
	@Test
	public void testMortgagePolicyDocuments(){
		mainApp().open();
		createCustomerIndividual();
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_MortgagePolicy")));
		
		GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents.AHAUXX,
				Documents.AHFMXX,
				Documents.HS11.setState(getState()),
				Documents.HSIQXX,
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06XX,
				Documents.HSU08XX
				);
		documentActionTab.verify.documentsEnabled(false,
				Documents.HSRFIXX,
				Documents.HSU01XX,
				Documents.HSU02XX,
				Documents.HSU07XX,
				Documents.HSU09XX
				);
		documentActionTab.verify.documentsPresent(false,
				Documents._438BFUNS,
				Documents.AHRCTXX,
				Documents.AHPNXX,
				Documents.HS02,
				Documents.AHNBXX,
				Documents.HSEIXX,
				Documents.HSES);
		documentActionTab.buttonCancel.click();
		
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents.HS02,
				Documents.AHNBXX,
				Documents._438BFUNS,
				Documents.HS0420
				// TODO the following documents cannot be found in the xml file, need to confirm the request
//				Documents.HS0435,
//				Documents.HS0455,
//				Documents.HS0465,
//				Documents.HS0495,
//				Documents.HS0906,
//				Documents.HS0926,
//				Documents.HS0931,
//				Documents.HS0934,
//				Documents.HS0965,
//				Documents.HS0477
				);
		
		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
//				Documents.AHFMXX, // TODO actually it is disabled on the page, need to confirm the request
				Documents.HS11.setState(getState()),
				Documents.HSEIXX,
				Documents.HSES.setState(getState()),
				Documents.HSILXX,
//				Documents.HSRFIXX, // TODO actually it is disabled on the page, need to confirm the request
				Documents.HSU01XX,
//				Documents.HSU02XX, // TODO actually it is disabled on the page, need to confirm the request
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06XX,
				Documents.HSU07XX,
				Documents.HSU08XX,
				Documents.HSU09XX
				);
		documentActionTab.verify.documentsEnabled(false, 
//				Documents.AHRCTXX, // TODO actually it is enabled on the page, need to confirm the request
				Documents.HSU03XX
				);
		documentActionTab.verify.documentsPresent(false,
				Documents.AHNBXX,
				Documents.HSIQXX,
				Documents.AHPNXX,
				Documents._438BFUNS,
				Documents.HS02);
		documentActionTab.generateDocuments(
//				Documents.HSRFIXX, // TODO actually it is disabled on the page, need to confirm the request
				Documents.HSES.setState(getState())
				);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
//				Documents.HSRFIXX, // TODO actually it is disabled on the page, need to confirm the request
				Documents.HSES);
	}
	
	private String getWindowHandle(){
		return BrowserController.get().driver().getWindowHandle();
	}
	
	private void switchToWindow(String windowHandle){
		BrowserController.get().driver().switchTo().window(windowHandle);
	}
}
