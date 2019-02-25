package aaa.modules.docgen.home_ss.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeSSDP3BaseTest {
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

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {
		mainApp().open();
		//
		createCustomerIndividual();
		String quoteNum = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_PolicyDocuments").resolveLinks()));

		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		documentActionTab.verify.documentsEnabled(softly,
				//PAS-6839 Remove Old CIN Documents and triggers from the system
				//Documents.AHAUXX,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.DS11.setState(getState()),
				DocGenEnum.Documents.DSIQXX,
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
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents._438BFUNS,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.AHPNXX,
				DocGenEnum.Documents.DS02,
				DocGenEnum.Documents.AHNBXX,
				//				Documents.HSEIXX //TODO Actually HSEIXX is present, need to confirm the request
				DocGenEnum.Documents.HSES
		);
		documentActionTab.generateDocuments(DocGenEnum.Documents.DSIQXX);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.DSIQXX, DocGenEnum.Documents.AHPNXX);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(DocGenEnum.Documents.DS11.setState(getState()), DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.HSILXX);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.DS11, DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.HSILXX, DocGenEnum.Documents.AHPNXX);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		policy.getDefaultView().getTab(PropertyInfoTab.class).fillTab(getTestSpecificTD("PropertyInfoTab_1000"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PropertyQuoteTab.btnCalculatePremium.click();
		Tab.buttonSaveAndExit.click();
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.DS02, DocGenEnum.Documents.AHNBXX, DocGenEnum.Documents.DS0469);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.DS11.setState(getState()),
				DocGenEnum.Documents.HSEIXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSRFIXX,
				DocGenEnum.Documents.HSU01XX,
				//				Documents.HSU02XX  //TODO Actually HSU02XX is disabled, need to confirm the request
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU07XX,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HSU03XX
		);
		documentActionTab.verify.documentsPresent(softly, false,
				//Documents.AHAUXX,
				DocGenEnum.Documents.HSIQXX,
				DocGenEnum.Documents.AHPNXX,
				DocGenEnum.Documents._438BFUNS,
				DocGenEnum.Documents.HSES
		);

		documentActionTab.generateDocuments(DocGenEnum.Documents.DS11.setState(getState()));
		WebDriverHelper.switchToDefault();
		//WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.DS11, DocGenEnum.Documents.AHPNXX);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.HSEIXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSRFIXX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.HSEIXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSRFIXX
		);
		softly.close();
	}

	/**
	 * <pre>
	 * TC Steps:
	 * Create HO3 quote:
	 *
	 * Set   Payment Plan : Mortgagee Bill
	 * Add one morgagee                                        // for HSES and 438BFUNS forms
	 *
	 * Set  Fire department type = 'S - Subscription based'
	 * Subscription to fire department/station = 'Yes'
	 * Public protection class = 5                             // for HSRFIXX form generation
	 * Construction type = "Frame"
	 * Proof of subscription to fire department = "No"
	 *
	 * Rate the quote.
	 * Save and exit quote, Go to On-Demand Documents tab,
	 * Verify state of forms on the quote:
	 * AHAUXX - enabled
	 * AHFMXX - enabled
	 * HSILXX - enabled
	 * DS11   - enabled
	 * DSIQXX - enabled
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
	 * DS02
	 * AHNBXX
	 * HSEIXX
	 * HSES
	 * Select forms on On-Demand Documents Tab :
	 * HSU03XX,HSU04XX,HSU05XX,HSU06XX,HSU08XX
	 * Press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * AHAUXX
	 * HSU03XX
	 * HSU04XX
	 * HSU05XX
	 * HSU06XX
	 * HSU08XX
	 * Bind the quote.
	 * Verify that below forms are generated in xml batch after bind:
	 * DS02
	 * AHNBXX
	 * 438BFUNS
	 * Go To On-Demand Document Tab
	 * Verify state of forms on the policy:
	 * AHFMXX - enabled
	 * AHRCTXX - disabled
	 * HS11 - enabled
	 * HSEIXX - enabled
	 * HSES - enabled
	 * HSILXX - enabled
	 * HSRFIXX - enabled
	 * HSU01XX - enabled
	 * HSU02XX - enabled
	 * HSU03XX - disabled
	 * HSU04XX - enabled
	 * HSU05XX - enabled
	 * HSU06XX - enabled
	 * HSU07XX - enabled
	 * HSU08XX - enabled
	 * HSU09XX - enabled
	 *
	 * Verify that below forms aren't present:
	 * AHNBXX
	 * HSIQXX
	 * AHPNXX
	 * 438 BFUNS
	 * DS02
	 *
	 * Select forms on On-Demand Documents Tab :
	 * HSES
	 * HSRFIXX
	 * HSU01XX
	 * HSU09XX
	 * Press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * HSES
	 * HSRFIXX
	 * HSU01XX
	 * HSU09XX
	 * <pre>
	 * Req:
	 *  AHAUXX - 15377: US CL GD-94 Consumer Information Notice
	 *  AHFMXX - 15379: US CL GD-96 Generate Fax Memorandum Document
	 * DS11   - 15209: US CL GD-77 Generate Application Document All Products
	 * DSIQXX - 15207: US CL GD-76 Generate Quote Document All Products
	 * HSRFIXX - 16187: US CL GD-124 Generate HSRFIXX Request For Information
	 * HSU01XX - 15272: US CL GD-53 Generate Underwriting Letter HSU01 Advisory
	 * HSU02XX - 15274: US CL GD-55 Generate Underwriting Letter HSU02 Cancellation
	 * HSU03XX - 15275: US CL GD-56 Generate Underwriting Letter HSU03 Customer Decline
	 * HSU04XX - 15276: US CL GD-57 Generate Underwriting Letter HSU04 Free Form to Insured
	 * HSU05XX - 15277: US CL GD-58 Generate Undewriting Letter HSU05 Free Form to Other
	 * HSU06XX - 15278: US CL GD-59 Generate Underwriting Letter HSU06 Free Form to Producer
	 * HSU07XX - 16238:US CL GD-60 Generate Underwriting letter HSU07 Non Renewal
	 * HSU08XX - 15282: US CL GD-62 Generate Underwriting Letter HSU08 Request Add'l Info
	 * HSU09XX - 15283: US CL GD-63 Generate Underwriting Letter HSU09 Uprate
	 * 438 BFUNS - 15210: US CL GD-01 Generate 438BFUNS Endorsement
	 * AHRCTXX - 15384: US CL GD-101 Generate Insured Receipt for Funds Received by Agent
	 * AHPNXX - 15382: US CL GD-99 Generate Privacy Information Notice
	 * DS02 - 16881: US CL GD-78 Generate  Declaration Documents All Products;
	 * AHNBXX - 15381: US CL GD-98 Generate New Business Welcome Letter
	 * HSEIXX - 16184: US CL GD-121 Generate HSEIXX Evidence of Insurance
	 * HSES - 16200: US CL GD-119 Generate HSESXX Property Insurance Invoice
	 * HSILXX - 16185:US CL GD-122 Generate HSILXX Property Inventory List
	 */

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testMortgagePolicyDocuments(@Optional("") String state) {
		mainApp().open();
		//
		createCustomerIndividual();
		String quoteNum = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_MortgagePolicy")));

		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		documentActionTab.verify.documentsEnabled(softly,
				//Documents.AHAUXX,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.DS11.setState(getState()),
				DocGenEnum.Documents.DSIQXX,
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
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents._438BFUNS,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.AHPNXX,
				DocGenEnum.Documents.DS02,
				DocGenEnum.Documents.AHNBXX,
				//				Documents.HSEIXX, // TODO Present on the page, need to confirm the request
				DocGenEnum.Documents.HSES);
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU08XX
		);
		WebDriverHelper.switchToDefault();
		//WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
				//Documents.AHAUXX,
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU08XX
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.DS02, DocGenEnum.Documents.AHNBXX, DocGenEnum.Documents._438BFUNS);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents.AHRCTXX,
				DocGenEnum.Documents.DS11.setState(getState()),
				DocGenEnum.Documents.HSEIXX,
				//				Documents.HSES, //TODO HSES is not present, need to check the request
				DocGenEnum.Documents.HSILXX,
				DocGenEnum.Documents.HSRFIXX,
				DocGenEnum.Documents.HSU01XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06XX,
				DocGenEnum.Documents.HSU07XX,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.AHFMXX,
				DocGenEnum.Documents.HSU02XX,
				DocGenEnum.Documents.HSU03XX
		);
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents.AHNBXX,
				DocGenEnum.Documents.HSIQXX,
				DocGenEnum.Documents.AHPNXX,
				DocGenEnum.Documents._438BFUNS,
				DocGenEnum.Documents.DS02
		);

		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
				DocGenEnum.Documents.HSRFIXX,
				DocGenEnum.Documents.HSU01XX,
				DocGenEnum.Documents.HSU09XX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents.HSRFIXX,
				DocGenEnum.Documents.HSU01XX,
				DocGenEnum.Documents.HSU09XX
		);
		softly.close();
	}

	/**
	 * <pre>
	 * TC Steps:
	 * Create a DP3 quote:
	 * Go to Endorsements Tab
	 * Add endorsements:
	 * DS0410
	 * DS0420
	 * DS0441
	 * DS0463
	 * DS0468
	 * DS0469
	 * DS0471
	 * DS0473
	 * DS0495
	 * DS0926
	 * DS0934
	 * DS2482
	 * Rate the quote.
	 * Save and exit quote,
	 * Go to On-Demand Documents tab.
	 * Verify that added endorsements are absent here on On-Demand Documents tab.
	 * Bind the quote.
	 * Verify that all added endorsements forms are generated in batch xml file.
	 *
	 * <pre>
	 * Req:
	 * DS 04 10  15213: US CL GD-109 Generate DS 04 10 Endorsement
	 * DS 04 20  15211: US CL GD-03 Generate DS 04 20 Endorsement
	 * DS 04 41  15214: US CL GD-04 Generate DS 04 41 Endorsement
	 * DS 04 63  15215: US CL GD-05 Generate DS 04 63 Endorsement
	 * DS 04 68  15216: US CL GD-06 Generate DS 04 68 Endorsement
	 * DS 04 69  15221: US CL GD-07 Generate DS 04 69 Endorsement
	 * DS 04 71  15222: US CL GD-08 Generate DS 04 71 Endorsement
	 * DS 04 73  15223: US CL GD-09 Generate DS 04 73 Endorsement
	 * DS 04 95  15228: US CL GD-10 Generate DS 04 95 Endorsement
	 * DS 09 26  15229: US CL GD-11 Generate DS 09 26 Endorsement (There is no DS 09 26 for NJ)
	 * DS 09 34  15230: US CL GD-12 Generate DS 09 34 Endorsement
	 * DS 24 82  15231: US CL GD-12 Generate DS 24 82 Endorsement
	 */

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testEndorsementsForms(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_EndorsementsForms")));

		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents.DS0420,
				DocGenEnum.Documents.DS0463,
				DocGenEnum.Documents.DS0468,
				DocGenEnum.Documents.DS0469,
				DocGenEnum.Documents.DS0471,
				DocGenEnum.Documents.DS0473,
				DocGenEnum.Documents.DS0495,
				DocGenEnum.Documents.DS0926,
				DocGenEnum.Documents.DS0934,
				DocGenEnum.Documents.DS2482);
		documentActionTab.cancel(true);

		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		switch (state) {
			case Constants.States.NJ:
				DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
						DocGenEnum.Documents.DS0420,
						DocGenEnum.Documents.DS0463,
						DocGenEnum.Documents.DS0468,
						DocGenEnum.Documents.DS0469,
						DocGenEnum.Documents.DS0471,
						DocGenEnum.Documents.DS0473,
						DocGenEnum.Documents.DS0495,
						DocGenEnum.Documents.DS0934,
						DocGenEnum.Documents.DS2482);
				break;
			default:
				DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
						DocGenEnum.Documents.DS0420,
						DocGenEnum.Documents.DS0463,
						DocGenEnum.Documents.DS0468,
						DocGenEnum.Documents.DS0469,
						DocGenEnum.Documents.DS0471,
						DocGenEnum.Documents.DS0473,
						DocGenEnum.Documents.DS0495,
						DocGenEnum.Documents.DS0926,
						DocGenEnum.Documents.DS0934,
						DocGenEnum.Documents.DS2482);
				break;
		}
		softly.close();
	}
}
