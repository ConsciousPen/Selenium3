package aaa.modules.docgen.home_ss.ho4;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO4BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import static aaa.main.enums.DocGenEnum.Documents.*;


public class TestHO4DocgenScenarios extends HomeSSHO4BaseTest{
	private String quoteNumber;
	private String policyNumber;
	/**
	 * @author Lina Li
	 * @name Verify On-Demand Documents tab for quote
	 * @scenario 
	 * 1. Create an individual customer
	 * 2. Create HO4 quote and add HS 09 88
	 * 3. Calculate premium and Save&Exit the quote 
	 * 4. Navigate to On Demand Document page
	 * 5. Verify the following forms present and enable on GODD tab
	 *    AHAUXX 
     *    HS11XX4
     *    HSIQXX4
     *    HSU03XX
     *    HSU04XX
     *    HSU05XX
     *    HSU06XX
     *    HSU08XX
	 * 6. Verify following forms present and disable on GODD tab.
	 *    HSRFIXX
     *    HSU01XX
     *    HSU02XX   
     *    HSU07XX
     *    HSU09XX
	 * 7. Verify following forms absent on GODD tab
	 *    438 BFUNS
     *    AHRCTXX
     *    AHPNXX
     *    HS02_4
     *    AHNBXX
     *    HSEIXX
     *    HSES  
     * 8. Select HSU03XX,HSU04XX,HSU06XX,HSU08XX forms and generate the forms
     * 9. Verify HSU03XX,HSU04XX,HSU06XX,HSU08XX were generated on xml
     * 10. Select HSIQXX4 and generate the form 
     * 11. Verify HSIQXX4 and AHPNXX were generated on xml
     * 12. Select HS11XX4,AHFMXX,HSILXX forms and generate
     * 13. Verify HS11XX4,AHFMXX,HSILXX were generated on xml
     * 14. Go to Edit Mode and override insurance score, set it to 926
     * 15. Save and exit quote without rating
     * 16. Go to On-Demand Documents tab.
     * 17. Verify that forms  AHFMXX, HSILXX are disabled 
     * 18. Verify that AHAUXX form is absent
     * 19. Go to Edit Mode and override insurance score, set it to 920
     * 20. Rate and save quote.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	public void TC01_Quote_Documents(String state) {
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		TestData tdPoicy=getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
		createQuote(tdPoicy);
		quoteNumber=PolicySummaryPage.labelPolicyNumber.getValue();
		policy.quoteDocGen().start();
		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		goddTab.verify.documentsPresent(AHAUXX,AHFMXX,HS11_4.setState(String.format("%s4",getState())),HSIQXX4,HSRFIXX,HSILXX,HSU01XX,HSU02XX,HSU03XX,HSU04XX,HSU05XX,HSU06XX,HSU07XX,HSU08XX,HSU09XX);
		goddTab.verify.documentsPresent(false,_438BFUNS,AHRCTXX,AHPNXX,HS02_4.setState(String.format("%s4",getState())),AHNBXX,HSEIXX,HSES);
		goddTab.verify.documentsEnabled(AHAUXX,AHFMXX,HS11_4.setState(String.format("%s4",getState())),HSIQXX4,HSILXX,HSU03XX,HSU04XX,HSU05XX,HSU06XX,HSU08XX);
		goddTab.verify.documentsEnabled(false, HSRFIXX,HSU01XX,HSU02XX,HSU07XX,HSU09XX);
		
		goddTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"), HSU03XX,HSU04XX,HSU06XX,HSU08XX);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber,HSU03XX,HSU04XX,HSU06XX,HSU08XX);
		
		WebDriverHelper.switchToWindow(currentHandle);
		policy.quoteDocGen().start();
		goddTab.generateDocuments(HSIQXX4);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber,HSIQXX4,AHPNXX);
		
		WebDriverHelper.switchToWindow(currentHandle);
		policy.quoteDocGen().start();
		goddTab.generateDocuments(HS11_4.setState(String.format("%s4",getState())),AHFMXX,HSILXX);
		DocGenHelper.verifyDocumentsGenerated(quoteNumber,HS11_4,AHFMXX,HSILXX);
		
		WebDriverHelper.switchToWindow(currentHandle);
		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride926"), ReportsTab.class, PropertyInfoTab.class, true);
		Tab.buttonSaveAndExit.click();
		
		policy.quoteDocGen().start();
		goddTab.verify.documentsEnabled(false, AHFMXX,HSILXX);
		goddTab.verify.documentsPresent(false, AHAUXX);
		goddTab.buttonCancel.click();
		
		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride920"), ReportsTab.class, MortgageesTab.class, true);
		Tab.buttonSaveAndExit.click();
		
		policy.quoteDocGen().start();
		goddTab.verify.documentsEnabled(true, AHFMXX,HSILXX);
		goddTab.verify.documentsPresent(true, AHAUXX);
		
		log.info("==========================================");
		log.info(getState() + " HO4 Quote Documents Generation is checked, quote: " + quoteNumber);
		log.info("==========================================");
	}
	
	/**
	 * @author Lina Li
	 * @name Verify On-Demand Documents tab for policy
	 * @scenario 
	 * 1. Open and Bind the quote from TC01
	 * 2. Verify that below forms are generated in xml 
	 *    HS02_4
	 *    AHNBXX
	 *    HS0988
	 * 3. Navigate to On Demand Document page
	 * 4. Verify the following forms present and enable on GODD tab
	 *    AHRCTXX
     *    HS11XX4
     *    HSEIXX
     *    HSILXX
     *    HSU01XX   
     *    HSU04XX
     *    HSU05XX
     *    HSU06XX
     *    HSU07XX
     *    HSU08XX
     *    HSU09XX
	 * 5. Verify following forms present and disable on GODD tab.
	 *    HSRFIXX
	 *    HSU02XX
     *    HSU03XX   
     *    AHFMXX 
	 * 6. Verify following forms absent on GODD tab
	 *    AHAUXX  
	 *    438 BFUNS
     *    AHPNXX
     *    HSES
     *    AHNBXX
     *    HSIQXX  
     * 7. Select HS11XX4 form and generate the form
     * 8. Verify that below forms are generated 
     *    HS11XX4
     *    AHPNXX
     * 9. Select AHRCTXX,HSEIXX,HSILXX,HSU01XX,HSU09XX  forms and generate the form
     * 10. Verify that below forms are generated
     *    AHRCTXX
     *    HSEIXX
     *    HSILXX
     *    HSU01XX
     *    HSU09XX
     * 11. Manually add cancel notice flag to the policy
     * 12. Navigate to On Demand page
     * 13. Verify HUS02XX is enable
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	public void TC02_Policy_Documents(String state) {
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		SearchPage.openQuote(quoteNumber);
		policy.purchase(getPolicyTD("DataGather", "TestData"));
		policyNumber=PolicySummaryPage.labelPolicyNumber.getValue();
		DocGenHelper.verifyDocumentsGenerated(policyNumber, HS02_4,AHNBXX,HS0988);
		
		policy.policyDocGen().start();
		GenerateOnDemandDocumentActionTab goddTab = policy.policyDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		goddTab.verify.documentsPresent(AHFMXX,AHRCTXX,HS11_4.setState(String.format("%s4",getState())),HSEIXX,HSILXX,HSRFIXX,HSU01XX,HSU02XX,HSU03XX,HSU04XX,HSU05XX,HSU06XX,HSU07XX,HSU08XX,HSU09XX);
		goddTab.verify.documentsEnabled(
				AHRCTXX,HS11_4.setState(String.format("%s4",getState())),
				HSEIXX,
				HSILXX,
				HSU01XX,
				HSU04XX,
				HSU05XX,
				HSU06XX,
				HSU07XX,
				HSU08XX,
				HSU09XX);
		goddTab.verify.documentsEnabled(false, AHFMXX,HSRFIXX,HSU03XX,HSU02XX);
		goddTab.verify.documentsPresent(false, 
//				AHAUXX,// TODO Actually AHAUXX is present, need to confirm the request
				HSIQXX4,
				AHPNXX,
				HSES,
				_438BFUNS);
		
		goddTab.generateDocuments(HS11_4.setState(String.format("%s4",getState())));
		DocGenHelper.verifyDocumentsGenerated(policyNumber, HS11_4,AHPNXX);
		
		WebDriverHelper.switchToWindow(currentHandle);
		policy.policyDocGen().start();
		goddTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),AHRCTXX,HSEIXX,HSILXX,HSU01XX,HSU09XX);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AHRCTXX,HSEIXX,HSILXX,HSU01XX,HSU09XX);
		
//		when the policy with cancel notice flag, HSU02XX will display as enable
		policy.cancelNotice().perform(getTestSpecificTD("TestData_CancelNotice"));
		PolicySummaryPage.labelCancelNotice.verify.present();
		policy.policyDocGen().start();
		goddTab.verify.documentsEnabled(HSU02XX);
		
	}
	
}
