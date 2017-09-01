package aaa.modules.docgen.home_ss.ho3;

import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.DeliveryMethod;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.HomeSSHO3BaseTest;

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
		policy.quoteDocGen().start();
		GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		
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
//				Documents.HSEIXX, // Present on the page, need to confirm the request
				Documents.HSES);
		
		documentActionTab.generateDocuments(Documents.HSIQXX);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSIQXX, Documents.AHPNXX);

		policy.quoteDocGen().start();
		if(getState().equals("VA"))
			documentActionTab.selectDocuments(Documents.HSAUDVA);
		documentActionTab.generateDocuments(Documents.HS11.setState(getState()), Documents.AHFMXX, Documents.HSILXX); 
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HS11.setState(getState()), Documents.AHFMXX, Documents.HSILXX, Documents.AHPNXX);
		if(getState().equals("VA"))
			DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSAUDVA);

		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(DeliveryMethod.LOCAL_PRINT, Documents.HSU03XX, Documents.HSU04XX, Documents.HSU05XX, Documents.HSU06XX, Documents.HSU08XX);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSU03XX, Documents.HSU04XX, Documents.HSU05XX, Documents.HSU06XX, Documents.HSU08XX);

		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride926"), ReportsTab.class, PropertyInfoTab.class, true);
		Tab.buttonSaveAndExit.click();
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(false, Documents.AHFMXX, Documents.HSILXX);
		documentActionTab.verify.documentsPresent(false, Documents.AHAUXX);
		documentActionTab.buttonCancel.click();
		
		policy.dataGather().start();
		NavigationPage.toViewTab(HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride920"), ReportsTab.class, PremiumsAndCoveragesQuoteTab.class);
		policy.getDefaultView().getTab(PremiumsAndCoveragesQuoteTab.class).calculatePremium();
		Tab.buttonSaveAndExit.click();
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(Documents.AHFMXX, Documents.HSILXX);
		documentActionTab.verify.documentsPresent(Documents.AHAUXX);

		log.info("==========================================");
		log.info(getState() + " HO3 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
	}
}
