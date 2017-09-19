package aaa.modules.docgen.home_ca.ho4;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.verification.CustomAssert;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeCaHO4BaseTest {
	private PolicyDocGenActionTab documentActionTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	/**
     * 1. Create a ho4 quote
     * 2. Go to On-Demands Documents Page and verify:
     *      enabled:
     *      61 6528 - AC1 20938
     *      61 4003 - AC2 20940
     *      WUAUCA -  AC6 18554
     *      F1122 - AC1 18531
     *      HSU06 - AC1 18741
     *      HSU08 - AC1 18745
     *      61 3026 - AC1 19757
     *      disabled:
     *      62 6500 - 20260
     *      WURFICA - 18698
     *      HSU01 - 18742
     *      HSU02 - 18746
     *      HSU07 - 18737
     *      HSU09 - 18738
     *      61 2022 - AC1 20735
     *      AHPNCA - 18541
     *      absent(not applicable for ho4):
     *      F1076B
     *      61 6530
     *      61 3000
     *      61 2006
     *
     * 3. Verify that 61 2022 gets generated with 61 4003 (AC1 20735)
     * 4. Verify that  AHPNCA gets generated with 61 6528 (AC1 18541)
     * 5. Verify that all enabled documents can be generated
     *
     *
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
				Documents._61_6528_HO4,
				Documents._61_4003,
				Documents.WUAUCA,
				Documents.F1122,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3026
				);
		documentActionTab.verify.documentsEnabled(false,
//				Documents._62_6500, // TODO enabled on the page, need to confirm the request
				Documents.WURFICA,
				Documents.HSU01CA,
				Documents.HSU02XX,
				Documents.HSU07CA,
				Documents.HSU09XX,
				Documents.AHPNCA
		);
		documentActionTab.verify.documentsPresent(false, 
				Documents.F1076B, 
				Documents._61_6530,
				Documents._61_3000,
				Documents._61_2006
				);
		
		documentActionTab.generateDocuments(Documents._61_4003);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents._61_4003, Documents.AHPNCA, Documents._60_5019);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(5000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(Documents._61_6528_HO4); 
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents._61_6528_HO4, Documents.AHPNCA);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(5000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"), 
				Documents.WUAUCA, 
				Documents._62_6500, 
				Documents.F1122,
				Documents._60_5019,
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3026
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.WUAUCA, 
				Documents._62_6500, 
				Documents.F1122,
				Documents._60_5019,
				Documents.HSU03XX,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3026
				);

		log.info("==========================================");
		log.info(getState() + " HO4 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	
	/**
     * 1. Create a ho4 policy
     * 2. Verify that 61 5120 and 1075 has been generated at bind
     * 3. Go to On-Demands Documents Page and verify:
     *  enabled:
     *      61 4003
     *      62 6500
     *      F1122
     *      60 5019
     *      HSU01CA
     *      HSU02XX
     *      HSU04XX
     *      HSU05XX
     *      HSU06CA
     *      HSU07CA
     *      HSU08XX
     *      HSU09XX
     *      AHRCTXX
     *      61 3026
     *  disabled:
     *      WURFICA
     *      61 2022
     *  absent:
     *      61 6528
     *      AHPNCA
     *      61 6530
     *      61 6513
     *      61 3000
     *
     * 4. Verify 60 5019, 61 2022, AHPNCA are generated with Application document (61 4003) (18962 AC2)
     * 5. Verify that all enabled documents can be generated
     */
	@Parameters({"state"})
	@Test
	public void testPolicyDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD());
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents._61_5120, Documents._1075_HO4);
		
		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents._61_4003,
				Documents._62_6500,
				Documents.F1122,
				Documents._60_5019,
				Documents.HSU01CA,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3026
				);
		documentActionTab.verify.documentsEnabled(false, 
				Documents.WURFICA
				);
		documentActionTab.verify.documentsPresent(false,
				Documents._61_6528_HO4,
				Documents.AHPNCA,
				Documents._61_6530,
				Documents._61_6513,
				Documents._61_3000
				);
		documentActionTab.generateDocuments(Documents._61_4003);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents._61_4003,
				Documents._60_5019,
				Documents.AHPNCA);
		
		PolicySummaryPage.labelPolicyNumber.waitForAccessible(5000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
				Documents.WUAUCA, 
				Documents._62_6500,
				Documents.F1122,
				Documents._60_5019,
				Documents.HSU01CA,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3026,
				Documents.AHRCTXXPUP
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents.WUAUCA, 
				Documents._62_6500,
				Documents.F1122,
				Documents._60_5019,
				Documents.HSU01CA,
				Documents.HSU04XX,
				Documents.HSU05XX,
				Documents.HSU06CA,
				Documents.HSU07CA,
				Documents.HSU08XX,
				Documents.HSU09XX,
				Documents._61_3026,
				Documents.AHRCTXXPUP
				);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	
}
