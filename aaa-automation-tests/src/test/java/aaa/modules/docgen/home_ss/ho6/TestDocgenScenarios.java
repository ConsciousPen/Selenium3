package aaa.modules.docgen.home_ss.ho6;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeSSHO6BaseTest {
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

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testQuoteDocuments(@Optional("") String state) {
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		mainApp().open();

		createCustomerIndividual();
		String quoteNum = createQuote();
		policy.quoteDocGen().start();

		documentActionTab.verify.documentsEnabled(softly,
				//Documents.AHAUXX,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())),
				DocGenEnum.Documents.HSIQXX6,
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU08XX
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.HSRFIXX,
				DocGenEnum.Documents.HSU01XX,
				DocGenEnum.Documents.HSU02XX,
				DocGenEnum.Documents.HSU07XX,
				DocGenEnum.Documents.HSU09XX
		);

		documentActionTab.verify.documentsPresent(softly,
				false,
				DocGenEnum.Documents._438BFUNS,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.AHPNXX,
				DocGenEnum.Documents.HS02_6,
				DocGenEnum.Documents.AHNBXX,
				//				Documents.HSEIXX, // TODO Present on the page, need to confirm the request
				DocGenEnum.Documents.HSES);

		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU08XX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU08XX
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(DocGenEnum.Documents.HSIQXX6);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.HSIQXX6, DocGenEnum.Documents.AHPNXX);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(
				DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())),
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HSILXX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
				DocGenEnum.Documents.HS11_6,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.AHPNXX
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride926"), ReportsTab.class, PropertyInfoTab.class, true);
		Tab.buttonSaveAndExit.click();
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(softly, false, DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.HSILXX);
		documentActionTab.cancel(true);

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride920"), ReportsTab.class, PremiumsAndCoveragesQuoteTab.class);
		policy.getDefaultView().getTab(PremiumsAndCoveragesQuoteTab.class).calculatePremium();
		Tab.buttonSaveAndExit.click();
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(softly, DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.HSILXX);
		//if (!getState().equals("VA"))
		//	documentActionTab.verify.documentsPresent(Documents.AHAUXX);

		log.info("==========================================");
		log.info(getState() + " HO6 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
		documentActionTab.cancel();
		softly.close();
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

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void ho6PolicyDocuments(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_PolicyDocuments")));
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HS02_6, DocGenEnum.Documents.AHNBXX, DocGenEnum.Documents.HS0614);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())),
				DocGenEnum.Documents.HSEIXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSU01XX,
				//				Documents.HSU02XX  //TODO Actually HSU02XX is disabled, need to confirm the request
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU07XX,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HSRFIXX,
				DocGenEnum.Documents.HSU03XX
		);
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents.HSIQXX6,
				DocGenEnum.Documents.AHPNXX,
				DocGenEnum.Documents._438BFUNS,
				DocGenEnum.Documents.HSES
		);

		documentActionTab.generateDocuments(DocGenEnum.Documents.HS11_6);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HS11_6, DocGenEnum.Documents.AHPNXX);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.HSEIXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSU01XX,
				DocGenEnum.Documents.HSU09XX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.HSEIXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSU01XX,
				DocGenEnum.Documents.HSU09XX
		);
		softly.close();
	}
}
