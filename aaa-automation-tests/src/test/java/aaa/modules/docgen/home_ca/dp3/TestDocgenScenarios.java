package aaa.modules.docgen.home_ca.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testQuoteDocuments(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		String quoteNum = createQuote();

		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			documentActionTab.verify.documentsEnabled(softly,
					DocGenEnum.Documents._61_6528_DP3,
					DocGenEnum.Documents.WU11DCA,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026
			);
			documentActionTab.verify.documentsEnabled(softly, false,
					//				Documents._62_6500,
					DocGenEnum.Documents.WURFICA,
					DocGenEnum.Documents.HSU01CA,
					DocGenEnum.Documents.HSU02XX,
					DocGenEnum.Documents.HSU07CA,
					DocGenEnum.Documents.HSU09XX,
					DocGenEnum.Documents.AHPNCA
			);

			documentActionTab.generateDocuments(DocGenEnum.Documents.WU11DCA);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.WU11DCA, DocGenEnum.Documents.AHPNCA);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.quoteDocGen().start();
			documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
					DocGenEnum.Documents._61_6528_DP3,
					//Documents.WUAUCA,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU03XX,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
					DocGenEnum.Documents._61_6528_DP3,
					//Documents.WUAUCA,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU03XX,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026
			);
			log.info("==========================================");
			log.info(getState() + " DP3 Quote Documents Generation is checked, quote: " + quoteNum);
			log.info("==========================================");
		softly.close();
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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD());
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents._61_3000, DocGenEnum.Documents._61_6530, DocGenEnum.Documents._61_5120, DocGenEnum.Documents.DF02CA);

			policy.policyDocGen().start();
			documentActionTab.verify.documentsEnabled(softly,
					DocGenEnum.Documents.WU11DCA,
					DocGenEnum.Documents._62_6500,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU01CA,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU07CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents.HSU09XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026,
					DocGenEnum.Documents.AHRCTXXPUP
			);
			//		documentActionTab.verify.documentsEnabled(false,
			//				Documents._61_2006 // TODO not present
			//		);
			documentActionTab.generateDocuments(DocGenEnum.Documents.WU11DCA);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
					DocGenEnum.Documents.WU11DCA,
					DocGenEnum.Documents.AHPNCA);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.policyDocGen().start();
			documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
					//Documents.WUAUCA,
					DocGenEnum.Documents._62_6500,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU01CA,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU07CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents.HSU09XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026,
					DocGenEnum.Documents.AHRCTXXPUP
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
					//Documents.WUAUCA,
					DocGenEnum.Documents._62_6500,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU01CA,
					DocGenEnum.Documents.HSU04XX,
					DocGenEnum.Documents.HSU05XX,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU07CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents.HSU09XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026,
					DocGenEnum.Documents.AHRCTXXPUP
			);
			log.info("==========================================");
			log.info(getState() + " DP3 Policy Documents Generation is checked, policy: " + policyNum);
			log.info("==========================================");
		softly.close();
	}

}
