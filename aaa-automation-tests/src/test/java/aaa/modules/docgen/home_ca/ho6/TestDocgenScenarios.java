package aaa.modules.docgen.home_ca.ho6;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.verification.CustomAssert;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO6BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeCaHO6BaseTest {
	private PolicyDocGenActionTab documentActionTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	/**
     * Create a ho6 quote
     * Go to On-Demands Documents Page and verify:
     *      enabled:
     *      61_6528
     *      61_4002
     *      F1122
     *      61_6530
     *      HSU06CA
     *      HSU08XX
     *      61_3000
     *      61_3026
     *      disabled:
     *      HSU01CA
     *      HSU02XX
     *      HSU07CA
     *      HSU09XX
     *      62_6500
     *      WURFICA
     *      AHPNCA
     *      61_2021
     *
     * Verify that 61_2021 must be generated with 61_4002 (Application documents) (AC2)
     * Verify that AHPNCA must be generated with 61_4002 (Application documents)
     * Verify that all enabled documents can be generated
     */
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testQuoteDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String quoteNum = createQuote();
		
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents._61_6528_HO6,
				Documents._61_4002,
				Documents.F1122,
				Documents._61_6530,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3000,
				Documents._61_3026
				);
		documentActionTab.verify.documentsEnabled(false,
				Documents.HSU01CA,
				Documents.HSU02XX,
				Documents.HSU07CA,
				Documents.HSU09XX,
//				Documents._62_6500,
				Documents.WURFICA,
				Documents.AHPNCA
		);
		
		documentActionTab.generateDocuments(Documents._61_4002);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents._61_4002, Documents.AHPNCA);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"), 
				Documents._61_6528_HO6,
				Documents.F1122,
				Documents.WUAUCA,
				Documents._61_6530,
				Documents._60_5019,
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3000,
				Documents._61_3026
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents._61_6528_HO6,
				Documents.F1122,
				Documents.WUAUCA,
				Documents._61_6530,
				Documents._60_5019,
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3000,
				Documents._61_3026
				);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
		log.info("==========================================");
		log.info(getState() + " HO6 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
	}
	
	/**
     * Create a ho6 policy
     * Check the following forms are generated at bind:
     *      61 6530
     *      61 3000
     *      61 5120
     *      61 1500
     * Go to On-Demands Documents Page and verify:
     *      enabled:
     *      61_4002
     *      62_6500
     *      F1122
     *      60_5019
     *      61_6530
     *      HSU01CA
     *      HSU06CA
     *      HSU07CA
     *      HSU08XX
     *      HSU09XX
     *      AHRCTXX
     *      61_3000
     *      61_3026
     *      disabled:
     *      WURFICA
     *      61_2021
     *      absent:
     *      61_6528
     *
     * Verify 60 5019 is generated with Application document (61 4002) (18962 AC2)
     * Verify that 61_2021 must be generated with 61_4002 (Application documents) (AC2)
     * Verify that AHPNCA must be generated with 61_4002 (Application documents)
     */
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testPolicyDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD());
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents._61_3000, Documents._61_6530, Documents._61_5120, Documents._61_1500);
		
		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents._61_4002,
				Documents._62_6500,
				Documents.F1122,
				Documents._60_5019,
				Documents._61_6530,
				Documents.HSU01CA,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3000,
				Documents._61_3026
				);
		documentActionTab.verify.documentsEnabled(false,
				Documents.WURFICA
		);
		documentActionTab.verify.documentsPresent(false, Documents._61_6528_HO6);
		documentActionTab.generateDocuments(Documents._61_4002);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents._61_4002,
				Documents._60_5019,
				Documents.AHPNCA);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
		log.info("==========================================");
		log.info(getState() + " HO6 Policy Documents Generation is checked, policy: " + policyNum);
		log.info("==========================================");
	}
	
}
