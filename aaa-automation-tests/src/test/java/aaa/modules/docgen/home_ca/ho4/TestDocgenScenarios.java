package aaa.modules.docgen.home_ca.ho4;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testQuoteDocuments(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();
		String quoteNum = createQuote();

		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents._61_6528_HO4,
				DocGenEnum.Documents._61_4003,
				//Documents.WUAUCA,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU08XX,
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
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents.F1076B,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_2006
		);

		documentActionTab.generateDocuments(DocGenEnum.Documents._61_4003);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents._61_4003, DocGenEnum.Documents.AHPNCA, DocGenEnum.Documents._60_5019);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(DocGenEnum.Documents._61_6528_HO4);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents._61_6528_HO4, DocGenEnum.Documents.AHPNCA);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
				//Documents.WUAUCA,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents._61_3026
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
				//Documents.WUAUCA,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents._61_3026
		);

		log.info("==========================================");
		log.info(getState() + " HO4 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
		documentActionTab.cancel();
		softly.close();
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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD());
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents._61_5120, DocGenEnum.Documents._1075_HO4);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents._61_4003,
				DocGenEnum.Documents._62_6500,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX,
				DocGenEnum.Documents._61_3026
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.WURFICA
		);
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents._61_6528_HO4,
				DocGenEnum.Documents.AHPNCA,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents._61_6513,
				DocGenEnum.Documents._61_3000
		);
		documentActionTab.generateDocuments(DocGenEnum.Documents._61_4003);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents._61_4003,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.AHPNCA);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
				//Documents.WUAUCA,
				DocGenEnum.Documents._62_6500,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX,
				DocGenEnum.Documents._61_3026,
				DocGenEnum.Documents.AHRCTXXPUP
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				//Documents.WUAUCA,
				DocGenEnum.Documents._62_6500,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX,
				DocGenEnum.Documents._61_3026,
				DocGenEnum.Documents.AHRCTXXPUP
		);
		log.info("==========================================");
		log.info(getState() + " HO6 Policy Documents Generation is checked, policy: " + policyNum);
		log.info("==========================================");
		documentActionTab.cancel();
		softly.close();
	}

}
