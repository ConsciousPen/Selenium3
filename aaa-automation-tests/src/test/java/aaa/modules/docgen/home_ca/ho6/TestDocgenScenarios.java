package aaa.modules.docgen.home_ca.ho6;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO6BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testQuoteDocuments(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();
		String quoteNum = createQuote();

		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents._61_6528_HO6,
				DocGenEnum.Documents._61_4002,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_3026
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU02XX,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU09XX,
				//				Documents._62_6500,
				DocGenEnum.Documents.WURFICA,
				DocGenEnum.Documents.AHPNCA
		);

		documentActionTab.generateDocuments(DocGenEnum.Documents._61_4002);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents._61_4002, DocGenEnum.Documents.AHPNCA);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
				DocGenEnum.Documents._61_6528_HO6,
				DocGenEnum.Documents.F1122,
				//Documents.WUAUCA,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents._60_5019,
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
				DocGenEnum.Documents._61_6528_HO6,
				DocGenEnum.Documents.F1122,
				//Documents.WUAUCA,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU03XX,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_3026
		);
		log.info("==========================================");
		log.info(getState() + " HO6 Quote Documents Generation is checked, quote: " + quoteNum);
		log.info("==========================================");
		softly.close();
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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD());
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents._61_3000, DocGenEnum.Documents._61_6530, DocGenEnum.Documents._61_5120, DocGenEnum.Documents._61_1500);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(softly,
				DocGenEnum.Documents._61_4002,
				DocGenEnum.Documents._62_6500,
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX,
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_3026
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.WURFICA
		);
		documentActionTab.verify.documentsPresent(softly, false, DocGenEnum.Documents._61_6528_HO6);
		documentActionTab.generateDocuments(DocGenEnum.Documents._61_4002);
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents._61_4002,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.AHPNCA);

		log.info("==========================================");
		log.info(getState() + " HO6 Policy Documents Generation is checked, policy: " + policyNum);
		log.info("==========================================");
		softly.close();
	}

}
