package aaa.modules.docgen.home_ca.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.verification.CustomAssert;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeCaDP3BaseTest {
	private PolicyDocGenActionTab documentActionTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	/**
     * Create ca dp3 quote
     * Verify documents:
     *   enabled:
     *      61 6528
     *      WU11DCA
     *      61 6530
     *      HSU06CA
     *      HSU08XX
     *      61 3000
     *      61 3026
     *   disabled:
     *      62 6500
     *      WURFICA
     *      HSU01CA
     *      HSU02XX
     *      HSU07CA
     *      HSU09XX
     *      61 2006
     *      AHPNCA
     *
     * Verify 61 2006 is generated with Application document (WU11DCA)
     * Verify that AHPNCA must be generated with Application documents (WU11DCA)
     * Verify that all enabled documents can be generated
     */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	public void testQuoteDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String quoteNum = createQuote();
		
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents._61_6528_DP3,
				Documents.WU11DCA,
				Documents._61_6530,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3000,
				Documents._61_3026
				);
		documentActionTab.verify.documentsEnabled(false,
				Documents._62_6500,
				Documents.WURFICA,
				Documents.HSU01CA,
				Documents.HSU02XX,
				Documents.HSU07CA,
				Documents.HSU09XX,
				Documents.AHPNCA
		);
		
		documentActionTab.generateDocuments(Documents.WU11DCA);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.WU11DCA, Documents.AHPNCA);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"), 
				Documents._61_6528_DP3,
				Documents.WUAUCA,
				Documents._61_6530,
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
				Documents._61_6528_DP3,
				Documents.WUAUCA,
				Documents._61_6530,
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
		log.info(getState() + " DP3 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
	}
	
	/**
     * Create ca dp3 policy
     * Verify the following documents are generated at bind:
     *      61_6530
     *      61_3000
     *      61_5120
     *      DF02CA
     * Go to On-Demand Documents tab and verify documents:
     *   enabled:
     *      WU11DCA
     *      62_6500
     *      WURFICA
     *      61_6530
     *      HSU01CA
     *      HSU06CA
     *      HSU07CA
     *      HSU08XX
     *      HSU09XX
     *      AHRCTXX
     *      61_3000
     *      61_3026
     *   disabled:
     *      61_2006
     *
     * Verify 61 2006 is generated with Application document (WU11DCA)
     * Verify that AHPNCA must be generated with Application documents (WU11DCA)
     * Verify that all enabled documents can be generated
     *
     */
	@Parameters({"state"})
	@Test
	public void testPolicyDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD());
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents._61_3000, Documents._61_6530, Documents._61_5120, Documents.DF02CA);
		
		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents.WU11DCA,
				Documents._62_6500,
				Documents._61_6530,
				Documents.HSU01CA,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3000,
				Documents._61_3026,
				Documents.AHRCTXXPUP
				);
//		documentActionTab.verify.documentsEnabled(false,
//				Documents._61_2006 // TODO not present
//		);
		documentActionTab.generateDocuments(Documents.WU11DCA);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents.WU11DCA,
				Documents.AHPNCA);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"), 
				Documents.WUAUCA,
				Documents._62_6500,
				Documents._61_6530,
				Documents.HSU01CA,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3000,
				Documents._61_3026,
				Documents.AHRCTXXPUP
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(policyNum,
				Documents.WUAUCA,
				Documents._62_6500,
				Documents._61_6530,
				Documents.HSU01CA,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3000,
				Documents._61_3026,
				Documents.AHRCTXXPUP
				);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
		log.info("==========================================");
		log.info(getState() + " DP3 Policy Documents Generation is checked, policy: " + policyNum);
		log.info("==========================================");
	}
	
}
