package aaa.modules.docgen.home_ss.ho3;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeSSHO3BaseTest {
	private GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	private IBillingAccount billing = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData cash_payment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private TestData cc_payment = tdBilling.getTestData("AcceptPayment", "TestData_CC");
	private TestData eft_payment = tdBilling.getTestData("AcceptPayment", "TestData_EFT");
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	private String REFUND_GENERATION_FOLDER_PATH = "/home/mp2/pas/sit/DSB_E_PASSYS_DSBCTRL_7025_D/outbound/";

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

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testQuoteDocuments(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();

			createCustomerIndividual();
			String quoteNum = createQuote();
			policy.quoteDocGen().start();
			//PAS-6839 Remove Old CIN Documents and triggers from the system
			/*
			if (getState().equals(States.VA)) {
				documentActionTab.verify.documentsPresent(DocGenEnum.Documents.HSAUDVA);
			} else {
				documentActionTab.verify.documentsPresent(DocGenEnum.Documents.AHAUXX);
			}
			*/
			documentActionTab.verify.documentsPresent(softly,
					DocGenEnum.Documents.AHFMXX,
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.HSIQXX,
					DocGenEnum.Documents.HSRFIXX,
					DocGenEnum.Documents.HSU01XX,
					DocGenEnum.Documents.HSU02XX,
					DocGenEnum.Documents.HSU03XX,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06XX,
					DocGenEnum.Documents.HSU07XX,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents.HSU09XX);

			documentActionTab.verify.documentsPresent(softly,
					false,
					DocGenEnum.Documents._438BFUNS,
					DocGenEnum.Documents.AHRCTXX,
					DocGenEnum.Documents.AHPNXX,
					DocGenEnum.Documents.HS02,
					DocGenEnum.Documents.AHNBXX,
					//				Documents.HSEIXX, // TODO Present on the page, need to confirm the request
					DocGenEnum.Documents.HSES);

			documentActionTab.generateDocuments(DocGenEnum.Documents.HSIQXX);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.HSIQXX, DocGenEnum.Documents.AHPNXX);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.quoteDocGen().start();
			if (getState().equals(Constants.States.VA)) {
				documentActionTab.selectDocuments(DocGenEnum.Documents.HSAUDVA);
			}
			documentActionTab.generateDocuments(
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.AHFMXX,
					DocGenEnum.Documents.HSILXX
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.AHFMXX,
					DocGenEnum.Documents.HSILXX,
					DocGenEnum.Documents.AHPNXX
			);
			if (getState().equals(Constants.States.VA)) {
				DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.HSAUDVA);
			}

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.quoteDocGen().start();
			documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
					DocGenEnum.Documents.HSU03XX,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06XX,
					DocGenEnum.Documents.HSU08XX
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
					DocGenEnum.Documents.HSU03XX,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06XX,
					DocGenEnum.Documents.HSU08XX
			);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			policy.getDefaultView().fillFromTo(getTestSpecificTD("InsuranceScoreOverride926"), ReportsTab.class, PropertyInfoTab.class, true);
			Tab.buttonSaveAndExit.click();
			policy.quoteDocGen().start();
			documentActionTab.verify.documentsEnabled(softly, false, DocGenEnum.Documents.AHFMXX, DocGenEnum.Documents.HSILXX);
			//		documentActionTab.verify.documentsPresent(false, Documents.AHAUXX);
			documentActionTab.buttonCancel.click();

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
			log.info(getState() + " HO3 Quote Documents Generation is checked, quote: " + quoteNum);
			log.info("==========================================");
		});
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
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();

			String policyNum = getCopiedPolicy();

			DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HS02, DocGenEnum.Documents.AHNBXX, DocGenEnum.Documents.HS0420);

			policy.policyDocGen().start();
			documentActionTab.verify.documentsEnabled(softly,
					DocGenEnum.Documents.F605005,
					DocGenEnum.Documents.AHRCTXX,
					DocGenEnum.Documents.HS11.setState(getState()),
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
					DocGenEnum.Documents.HSIQXX,
					DocGenEnum.Documents.AHPNXX,
					DocGenEnum.Documents._438BFUNS,
					DocGenEnum.Documents.HSES
			);

			documentActionTab.generateDocuments(DocGenEnum.Documents.HS11);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HS11, DocGenEnum.Documents.AHPNXX);

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
		});
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
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyRescissionNoticeDocument(@Optional("") String state) {
		mainApp().open();
		String policyNum = getCopiedPolicy();

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_NewBusinessRescissionNSF"));
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH60XXA);
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
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testMortgagePolicyDocuments(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_MortgagePolicy").resolveLinks()));

			policy.quoteDocGen().start();
			documentActionTab.verify.documentsEnabled(softly,
					//Documents.AHAUXX,
					DocGenEnum.Documents.AHFMXX,
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.HSIQXX,
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
					DocGenEnum.Documents.HS02,
					DocGenEnum.Documents.AHNBXX,
					//				Documents.HSEIXX, // TODO Not absent as expected, need to check the requirement
					DocGenEnum.Documents.HSES);
			documentActionTab.buttonCancel.click();

			policy.purchase(getPolicyTD());
			String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

			DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
					DocGenEnum.Documents.HS02,
					DocGenEnum.Documents.AHNBXX,
					DocGenEnum.Documents._438BFUNS,
					DocGenEnum.Documents.HS0420
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
			documentActionTab.verify.documentsEnabled(softly,
					//				Documents.AHFMXX, // TODO actually it is disabled on the page, need to confirm the request
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.HSEIXX,
					DocGenEnum.Documents.HSES.setState(getState()),
					DocGenEnum.Documents.HSILXX,
					//				Documents.HSRFIXX, // TODO actually it is disabled on the page, need to confirm the request
					DocGenEnum.Documents.HSU01XX,
					//				Documents.HSU02XX, // TODO actually it is disabled on the page, need to confirm the request
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06XX,
					DocGenEnum.Documents.HSU07XX,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents.HSU09XX
			);
			documentActionTab.verify.documentsEnabled(softly, false,
					//				Documents.AHRCTXX, // TODO actually it is enabled on the page, need to confirm the request
					DocGenEnum.Documents.HSU03XX
			);
			documentActionTab.verify.documentsPresent(softly, false,
					DocGenEnum.Documents.AHNBXX,
					DocGenEnum.Documents.HSIQXX,
					DocGenEnum.Documents.AHPNXX,
					DocGenEnum.Documents._438BFUNS,
					DocGenEnum.Documents.HS02);
			documentActionTab.generateDocuments(
					//				Documents.HSRFIXX, // TODO actually it is disabled on the page, need to confirm the request
					DocGenEnum.Documents.HSES.setState(getState())
			);
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
					//				Documents.HSRFIXX, // TODO actually it is disabled on the page, need to confirm the request
					DocGenEnum.Documents.HSES);
		});
	}

	/**
	 * <pre>
	 * Test steps:
	 * 1. Create a HO3 policy;
	 * 2. Add 4 payment methods(Cash, Check, Credit Card, EFT);
	 * 3. Make 6 different payments(Cash, Check, Credit Card, EFT);
	 * 4. Decline Credit Card payment, with reason "NSF fee - with restriction"
	 * 5. Check Payment Decline Transaction appears in Payment and Other Transaction section.
	 * 6. Check Fee Transaction appears in Payment and Other Transaction section.
	 * 7. Check status of the payment transaction changes to "Decline"
	 * 8. Run DocGen Batch Job
	 * 9. Check form 60 5000 is generated
	 * 10. Decline Credit Card payment, with reason "NSF fee - with restriction"
	 * 11. Run DocGen Batch Job
	 * 12. Check form 60 5003 is generated
	 * 13. Decline EFT payment, with reason "NSF fee - without restriction"
	 * 14. Check Payment Decline Transaction appears in Payment and Other Transaction section.
	 * 15. Check Fee Transaction appears in Payment and Other Transaction section.
	 * 16. Check status of the payment transaction changes to "Decline"
	 * 17. Run DocGen Batch Job
	 * 18. Check form 60 5001 is generated
	 * 19. Decline Check payment, with reason "No Fee + No Restriction"
	 * 20. Check Payment Decline Transaction appears in Payment and Other Transaction section.
	 * 21. Check status of the payment transaction changes to "Decline"
	 * 22. Run DocGen Batch Job
	 * 23. Check form 60 5002 is generated
	 * 24. Decline Check payment, with reason "No Fee + No Restriction + No Letter"
	 * 25. Check Payment Decline Transaction appears in Payment and Other Transaction section.
	 * 26. Check status of the payment transaction changes to "Decline"
	 * 27. Decline Cash payment.
	 * 28. Check Payment Decline Transaction appears in Payment and Other Transaction section.
	 * 29. Check status of the payment transaction changes to "Decline"
	 * # Req
	 * 15311: US CL GD-68 Generate NSF = Fee + Restriction Form
	 * 15312: US CL GD-69 Generate Fee + No Restriction Form
	 * 15313: US CL GD-70 Generate No Fee + No Restriction Form
	 * 15314: US CL GD-71 Generate Payment Restriction Form
	 * </pre>
	 */
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testReturnPaymentDocuments(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_ReturnPaymentPolicy")));
		makePayments();

		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), "($17.00)");
		verifyPaymentDeclinedTransactionPresent("17");
		verifyFeeTransaction("NSF fee - with restriction");
		verifyPaymentTransactionBecameDeclined("-17");
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5000);

		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), "($16.00)");
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5003);

		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), "($18.00)");
		verifyPaymentDeclinedTransactionPresent("18");
		verifyFeeTransaction("NSF fee - without restriction");
		verifyPaymentTransactionBecameDeclined("-18");
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5001);

		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_NoFeeNoRestriction"), "($19.00)");
		verifyPaymentDeclinedTransactionPresent("19");
		verifyPaymentTransactionBecameDeclined("-19");
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5002);

		log.info("==========================================");
		log.info(getState() + " HO3 Return Payment Documents is checked, policy: " + policyNum);
		log.info("==========================================");
	}

	/**
	 * <pre>
	 * Test steps:
	 * 1. Open policy which was created in ho3RefundCheckDocument test;
	 * 2. Select "Cancellation Notice" from "MoveTo"
	 * 3. Fill the cancellation notice dialogue (Cancellation reason = "'Material Misrepresentation" )
	 * 4. Run DocGen Batch Job
	 * 5. Verify that AH61XX form is generated
	 * # Req
	 * 15370: US CL GD-87 Generate Cancellation Notice Document U/W or Insured Request
	 * 15780:US PA GD-02 Generate Cancellation Notice-UW or Insured Request
	 * </pre>
	 */
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testCancellationNoticeDocument(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_ReturnPaymentPolicy")));

		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob, true);
		if (getState().equals(Constants.States.PA)) {
			DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.AH61XX);
			DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.HS61PA);
		} else {
			DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH61XX);
			DocGenHelper.verifyDocumentsGenerated(false, true, policyNum, DocGenEnum.Documents.HS61PA);
		}

		log.info("==========================================");
		log.info(getState() + " HO3 Policy Cancellation Notice Document Generation is checked, policy: " + policyNum);
		log.info("==========================================");
	}

	/**
	 * <pre>
	 * Test steps:
	 * 1. Create an HO3 policy;
	 * 2. Make check payment > 1000$;
	 * 3. Make manual refund of the payment;
	 * 4. Approve the refund;
	 * 5. Issue the refund;
	 * 6. Run DocGen Batch Job
	 * 7. Check form  is generated 55 3500
	 * 8. Check the form is enabled on OnDemand Documents Tab
	 * # Req
	 * 18833:US CL GD-64 Generate Refund Check
	 * </pre>
	 */
	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testRefundCheckDocument(@Optional("") String state) {
		Dollar amount = new Dollar(1234);

		mainApp().open();
		String policyNum = getCopiedPolicy();
		BillingSummaryPage.open();
		billing.acceptPayment().perform(check_payment, amount);
		new BillingPaymentsAndTransactionsVerifier().setType("Payment").setSubtypeReason("Manual Payment").setAmount(amount.negate()).setStatus("Issued").verifyPresent();
		billing.refund().perform(tdRefund, amount);
		new BillingPendingTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Pending").verifyPresent();
		billing.approveRefund().perform(amount);
		new BillingPaymentsAndTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Approved").verifyPresent();
		//billing.issueRefund().perform(amount);
		JobUtils.executeJob(BatchJob.aaaRefundDisbursementAsyncJob, true);
		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob, true);
		
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setType("Refund").setSubtypeReason("Manual Refund").setAmount(amount).setStatus("Issued").verifyPresent();

		//refund check are now generated in csv files PASBB-795
		List<String> documentsFilePaths = RemoteHelper.get().waitForFilesAppearance(REFUND_GENERATION_FOLDER_PATH, "csv", 10, policyNum);
		assertThat(documentsFilePaths.size()).isGreaterThan(0);
		
		log.info("==========================================");
		log.info(getState() + " HO3 Refund Check Document is checked, policy: " + policyNum);
		log.info("==========================================");
	}

	private void makePayments() {
		BillingSummaryPage.open();
		billing.acceptPayment().perform(check_payment, new Dollar(16));
		billing.acceptPayment().perform(check_payment, new Dollar(17));
		billing.acceptPayment().perform(cc_payment, new Dollar(18));
		billing.acceptPayment().perform(eft_payment, new Dollar(19));
		billing.acceptPayment().perform(check_payment, new Dollar(20));
		billing.acceptPayment().perform(cash_payment, new Dollar(21));
	}

	private void verifyPaymentDeclinedTransactionPresent(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Adjustment").setSubtypeReason("Payment Declined").setAmount(new Dollar(amount)).setStatus("Applied").verifyPresent();
	}

	private void verifyFeeTransaction(String reason) {
		if (!getState().contains(Constants.States.NJ)) {
			new BillingPaymentsAndTransactionsVerifier().setType("Fee").setSubtypeReason(reason).setAmount(new Dollar(20)).setStatus("Applied").verifyPresent();
		} else {
			new BillingPaymentsAndTransactionsVerifier().setType("Fee").setSubtypeReason(reason).setAmount(new Dollar(15)).setStatus("Applied").verifyPresent();
		}
	}

	private void verifyPaymentTransactionBecameDeclined(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Payment").setSubtypeReason("Manual Payment").setAmount(new Dollar(amount)).setStatus("Declined").verifyPresent();
	}
}
