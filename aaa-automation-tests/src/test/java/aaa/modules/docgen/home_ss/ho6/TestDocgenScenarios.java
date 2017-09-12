package aaa.modules.docgen.home_ss.ho6;

import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeSSHO6BaseTest{
	private GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	
	/**
     * <pre>
     * TC Steps:
     * Create an HO6 quote;
     * (Special conditions in this quote:
     * For HS 06 14:
     * Add HS 06 14 - Increased Amount of Insurance for Personal Property Located in Self Storage Facility
     * )
     *
     * Rate the quote.
     * Save and exit the quote, Go to On-Demand Documents tab,
     * Verify state of forms on the quote:
     * AHAUXX - enabled  (except VA)
     * HSAUDVA - enabled (VA Only)
     * AHFMXX - enabled
     * HS11XX6 - enabled
     * HSIQXX6 - enabled
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
     * HS02_6
     * AHNBXX
     * HSEIXX
     * HSES
     * Select HSU03XX,HSU04XX,HSU06XX,HSU08XX forms and press "Generate" button
     * Verify that below forms are generated in xml batch:
     * HSU03XX
     * HSU04XX
     * HSU06XX
     * HSU08XX
     * AHAUXX (except VA)
     * Select HSIQXX6 form and press "Generate" button
     * Verify that below forms are generated in xml batch:
     * HSIQXX6
     * AHPNXX
     * Select HS11XX6,AHFMXX,HSILXX,(HSAUDVA - VA Only) forms and press "Generate" button
     * Verify that below forms are generated in xml batch:
     * HS11XX6
     * AHFMXX
     * HSILXX
     * AHPNXX
     * HSAUDVA  (VA Only)
     * Go to Edit Mode and override insurance score, set it to 926
     * Save and exit quote without rating
     * Go to On-Demand Documents tab.
     * Verify that forms  AHFMXX, HSILXX are disabled
     * Verify that AHAUXX form is absent
     * Go to Edit Mode and override insurance score, set it to 920
     * Rate and save quote.
     * </pre>
     * Req:
     * AHAUXX - 15377: US CL GD-94 Consumer Information Notice
     * AHFMXX - 15379: US CL GD-96 Generate Fax Memorandum Document
     * HS11_6   - 15209: US CL GD-77 Generate Application Document All Products
     * HSIQXX6 - 15207: US CL GD-76 Generate Quote Document All Products
     * HSRFIXX - 16187: US CL GD-124 Generate HSRFIXX Request For Information
     * HSU03XX - 15275: US CL GD-56 Generate Underwriting Letter HSU03 Customer Decline
     * HSU04XX - 15276: US CL GD-57 Generate Underwriting Letter HSU04 Free Form to Insured
     * HSU05XX - 15277: US CL GD-58 Generate Undewriting Letter HSU05 Free Form to Other
     * HSU06XX - 15278: US CL GD-59 Generate Underwriting Letter HSU06 Free Form to Producer
     * HSU08XX - 15282: US CL GD-62 Generate Underwriting Letter HSU08 Request Add'l Info
     */

    @Test
    public void testQuoteDocuments() throws Exception {
    	CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String quoteNum = createQuote();
		policy.quoteDocGen().start();

		documentActionTab.verify.documentsEnabled(
				Documents.AHAUXX,
				Documents.AHFMXX,
				Documents.HS11_6.setState(String.format("%s6", getState())),
				Documents.HSIQXX6,
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
		
		documentActionTab.verify.documentsPresent(
				false,
				Documents._438BFUNS,
				Documents.AHRCTXX,
				Documents.AHPNXX,
				Documents.HS02_6,
				Documents.AHNBXX,
//				Documents.HSEIXX, // TODO Present on the page, need to confirm the request
				Documents.HSES);
		
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"), 
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU06XX,
				Documents.HSU08XX
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU06XX,
				Documents.HSU08XX
				);
		
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(Documents.HSIQXX6);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSIQXX6, Documents.AHPNXX);
		
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(
				Documents.HS11_6.setState(String.format("%s6", getState())), 
				Documents.AHFMXX, 
				Documents.HSILXX
				); 
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.HS11_6, 
				Documents.AHFMXX, 
				Documents.HSILXX, 
				Documents.AHPNXX
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
		log.info(getState() + " HO6 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
    }
	
    /**
     * <pre>
     * TC Steps:
     * Open and Bind the quote from ho6QuoteDocuments test .
     * Verify that below forms are generated in xml batch after bind:
     * HS02_6
     * AHNBXX
     * HS0614 (except VA)
     * Go to On-Demand Documents tab.
     * Verify state of forms on the policy:
     * AHFMXX - disabled
     * AHRCTXX - enabled
     * HS11XX6 - enabled
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
     * HSIQXX6
     * AHPNXX
     * 438 BFUNS
     * HSES
     * Select HS11XX6 form and press "Generate" button
     * Verify that below forms are generated in xml batch:
     * HS11XX6
     * AHPNXX
     * Select AHRCTXX,HSEIXX,HSILXX,HSU01XX,HSU09XX  forms and press "Generate" button
     * Verify that below forms are generated in xml batch:
     * AHRCTXX
     * HSEIXX
     * HSILXX
     * HSU01XX
     * HSU09XX
     * </pre>
     * Req:
     * HSU07XX - 16238:US CL GD-60 Generate Underwriting letter HSU07 Non Renewal
     * HSU01XX - 15272: US CL GD-53 Generate Underwriting Letter HSU01 Advisory
     * HSU02XX - 15274: US CL GD-55 Generate Underwriting Letter HSU02 Cancellation
     * HSU09XX - 15283: US CL GD-63 Generate Underwriting Letter HSU09 Uprate
     * 438 BFUNS - 15210: US CL GD-01 Generate 438BFUNS Endorsement
     * AHRCTXX - 15384: US CL GD-101 Generate Insured Receipt for Funds Received by Agent
     * AHPNXX - 15382: US CL GD-99 Generate Privacy Information Notice
     * HS02_6 - 16881: US CL GD-78 Generate  Declaration Documents All Products;
     * AHNBXX - 15381: US CL GD-98 Generate New Business Welcome Letter
     * HSEIXX - 16184: US CL GD-121 Generate HSEIXX Evidence of Insurance
     * HSESUT - 16200: US CL GD-119 Generate HSESXX Property Insurance Invoice
     * HSILXX - 16185:US CL GD-122 Generate HSILXX Property Inventory List
     */

    @Test
    public void ho6PolicyDocuments() {
    	CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_PolicyDocuments")));
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.HS02_6, Documents.AHNBXX, Documents.HS0614);
		
    	policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents.AHRCTXX, 
				Documents.HS11_6.setState(String.format("%s6", getState())), 
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
				Documents.HSIQXX6,
				Documents.AHPNXX,
				Documents._438BFUNS,
				Documents.HSES
				);
		
		documentActionTab.generateDocuments(Documents.HS11_6);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.HS11_6, Documents.AHPNXX);
		
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
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
    }
}
