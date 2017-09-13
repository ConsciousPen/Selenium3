package aaa.modules.docgen.home_ss.dp3;

import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeSSDP3BaseTest{
	private GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	
	/**
    TC Steps:
      1. Create an DP3 quote;
       (Special conditions in this quote:
        For DS 04 69:
            Add DS 04 69 - EarthQuake 
        For AHAUXX:
            Score Override >= 925
            User selects answer as "No" for the question "Are any pets or animals kept on the property?" displayed under the "Pets or animals" section.
            User selects "Yes" for the question "Are there any detached structures on the property?" under "Detached structures" section
            User enters the value for the field "Limit of liability" in such that Combined value of limit of Liability for all the detached structure added greater than 50% of Cov A (Not greater than 100% of Cov A)
        For HSRFIXX 
            Signed policy application = Not Signed - and bind. 
       )
      2. Rate the quote.      
      3. Save and exit the quote, Go to On-Demand Documents tab, 
      4. Verify state of forms on the quote:
                 AHAUXX - enabled
                 AHFMXX - enabled
                 DS11   - enabled
                 DSIQXX - enabled
                 HSRFIXX - disabled
                 HSU01XX - disabled
                 HSU02XX - disabled     
                 HSU03XX - enabled
                 HSU04XX - enabled
                 HSU05XX - enabled
                 HSU06XX - enabled
                 HSU07XX - disabled
                 HSU08XX - enabled
                 HSU09XX - disabled 
       5. Verify that below forms aren't present:
                 438 BFUNS
                 AHRCTXX
                 AHPNXX
                 DS02
                 AHNBXX
                 HSEIXX
                 HSESUT    
       6. Select DSIQXX form and press "Generate" button
       7. Verify that below forms are generated in xml batch:
                 DSIQXX
                 AHPNXX
       8.Select DS11,AHFMXX,HSILXX forms and press "Generate" button
       9. Verify that below forms are generated in xml batch:
                 DS11
                 AHFMXX
                 HSILXX
                 AHPNXX
       10. Go to Edit Mode and On PropertyInfo 
       11. Set Limit of liability = 1000 (On detached structures) to get rid of error 
       12. Rate the quote
       13. Bind the quote.
       14. Verify that below forms are generated in xml batch after bind:              
                  DS02
                  AHNBXX
                  DS0469
       14. Go to On-Demand Documents tab.
       15. Verify state of forms on the policy:
                   AHFMXX - disabled
                   AHRCTXX - enabled
                   HS11UT - enabled
                   HSEIXX - enabled
                   HSILXX - enabled
                   HSRFIXX - enable
                   HSU01XX - enabled
                   HSU02XX - enabled
                   HSU03XX - disabled
                   HSU04XX - enabled
                   HSU05XX - enabled
                   HSU06XX - enabled
                   HSU07XX - enabled
                   HSU08XX - enabled
                   HSU09XX - enabled                 
       16. Verify that below forms aren't present:
                   AHAUXX
                   HSIQXX
                   AHPNXX
                   438 BFUNS
                   HSES
       17. Select DS11 form and press "Generate" button
       18. Verify that below forms are generated in xml batch:
                   DS11
                   AHPNXX
       19. Select AHRCTXX,HSEIXX,HSILXX,HSRFIXX forms and press "Generate" button               
       20. Verify that below forms are generated in xml batch:
                   AHRCTXX
                   HSEIXX
                   HSILXX 
                   HSRFIXX
     * Req:
        AHAUXX - 15377: US CL GD-94 Consumer Information Notice
        AHFMXX - 15379: US CL GD-96 Generate Fax Memorandum Document
        DS11   - 15209: US CL GD-77 Generate Application Document All Products
        DSIQXX - 15207: US CL GD-76 Generate Quote Document All Products
        HSRFIXX - 16187: US CL GD-124 Generate HSRFIXX Request For Information
        HSU01XX - 15272: US CL GD-53 Generate Underwriting Letter HSU01 Advisory
        HSU02XX - 15274: US CL GD-55 Generate Underwriting Letter HSU02 Cancellation    
        HSU03XX - 15275: US CL GD-56 Generate Underwriting Letter HSU03 Customer Decline
        HSU04XX - 15276: US CL GD-57 Generate Underwriting Letter HSU04 Free Form to Insured
        HSU05XX - 15277: US CL GD-58 Generate Undewriting Letter HSU05 Free Form to Other
        HSU06XX - 15278: US CL GD-59 Generate Underwriting Letter HSU06 Free Form to Producer
        HSU07XX - 16238:US CL GD-60 Generate Underwriting letter HSU07 Non Renewal
        HSU08XX - 15282: US CL GD-62 Generate Underwriting Letter HSU08 Request Add'l Info
        HSU09XX - 15283: US CL GD-63 Generate Underwriting Letter HSU09 Uprate
        438 BFUNS - 15210: US CL GD-01 Generate 438BFUNS Endorsement
        AHRCTXX - 15384: US CL GD-101 Generate Insured Receipt for Funds Received by Agent
        AHPNXX - 15382: US CL GD-99 Generate Privacy Information Notice
        DS02 - 16881: US CL GD-78 Generate  Declaration Documents All Products;
        AHNBXX - 15381: US CL GD-98 Generate New Business Welcome Letter
        HSEIXX - 16184: US CL GD-121 Generate HSEIXX Evidence of Insurance
        HSESUT - 16200: US CL GD-119 Generate HSESXX Property Insurance Invoice  
        HSILXX - 16185:US CL GD-122 Generate HSILXX Property Inventory List
        HS0469 - 15221: US CL GD-07 Generate DS 04 69 Endorsement
     * */

    @Test
    public void ho6PolicyDocuments() {
    	CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String quoteNum = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_PolicyDocuments")));
		
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents.AHAUXX,
				Documents.AHFMXX,
				Documents.DS11.setState(getState()),
				Documents.DSIQXX,
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
				Documents.DS02,
				Documents.AHNBXX,
//				Documents.HSEIXX //TODO Actually HSEIXX is present, need to confirm the request
				Documents.HSES
				);
		documentActionTab.generateDocuments(Documents.DSIQXX);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.DSIQXX, Documents.AHPNXX);
		
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(Documents.DS11.setState(getState()), Documents.AHFMXX, Documents.HSILXX, Documents.AHPNXX);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.DS11, Documents.AHFMXX, Documents.HSILXX, Documents.AHPNXX);
		
		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.PROPERTY_INFO.get());
		policy.getDefaultView().getTab(PropertyInfoTab.class).fillTab(getTestSpecificTD("PropertyInfoTab_1000"));
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PropertyQuoteTab.btnCalculatePremium.click();
		Tab.buttonTopSave.click();
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.DS02, Documents.AHNBXX, Documents.DS0469);
		
    	policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents.AHRCTXX, 
				Documents.DS11.setState(getState()), 
				Documents.HSEIXX, 
				Documents.HSILXX, 
				Documents.HSRFIXX,
				Documents.HSU01XX,
//				Documents.HSU02XX  //TODO Actually HSU02XX is disabled, need to confirm the request
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06XX,
				Documents.HSU07XX,
				Documents.HSU08XX,
				Documents.HSU09XX
				);
		documentActionTab.verify.documentsEnabled(false, 
				Documents.AHFMXX,
				Documents.HSU03XX
				);
		documentActionTab.verify.documentsPresent(false, 
				Documents.AHAUXX,
				Documents.HSIQXX,
				Documents.AHPNXX,
				Documents._438BFUNS,
				Documents.HSES
				);
		
		documentActionTab.generateDocuments(Documents.DS11.setState(getState()));
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.DS11, Documents.AHPNXX);
		
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(
				Documents.AHRCTXX, 
				Documents.HSEIXX, 
				Documents.HSILXX, 
				Documents.HSRFIXX
				);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents.AHRCTXX, 
				Documents.HSEIXX, 
				Documents.HSILXX, 
				Documents.HSRFIXX
				);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
    }
}
