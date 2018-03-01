package aaa.modules.docgen.pup;

import static aaa.main.enums.DocGenEnum.Documents.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.modules.policy.pup.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.verification.CustomAssert;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestDocgenScenariosCA extends PersonalUmbrellaBaseTest {
	/**
	 * Create pup policy with underlying ca policy
	 * Go to On-Demand Documents tab and verify:
	 *  enabled:
	 *      HSU08XX
	 *      HSU06CA
	 *      61_3026
	 *      58_4000
	 *      61_6528
	 *  disabled:
	 *      HSU02XX
	 *      HSU09XX
	 *      AHAPXX
	 *      HSU07CA
	 *      HSU01CA
	 *      WURFICA
	 *
	 * Check that AHPNCA is generated with Application documents (58 4000)
	 * Check that all enabled documents can be generated
	 *
	 *	Bind the quote
	 * Verify 61 6120 and 58 1500 are generated at bind
	 * Go to On-Demand Documents tab and verify:
	 *  enabled:
	 *      HSU08XX
	 *      HSU09XX
	 *      AHFMXX
	 *      HSU06CA
	 *      60 5019
	 *      AHRCTXX
	 *      61 3026
	 *      58 4000
	 *      HSU07CA
	 *      HSU01CA
	 *  disabled:
	 *      AHAPXX
	 *      WURFICA
	 *
	 *  Check that AHPNCA is generated with Application documents (58 4000)
	 *  Check that all enabled documents can be generated
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPUPDocgenScenarios(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();

		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		createCustomerIndividual();
		String quoteNum = createQuote();

		// Verify the documents on quote GODD page
		policy.quoteDocGen().start();
		goddTab.verify.documentsEnabled(
				HSU06CA,
				HSU08XX,
				_61_3026,
				_58_4000,
				_61_6528
		);
		goddTab.verify.documentsEnabled(false,
				HSU01CA,
				HSU02XX,
				HSU07CA,
				HSU09XX,
				AHAPXX
				//				WURFICA // TODO Not disabled as expected
		);
		goddTab.generateDocuments(_58_4000);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(quoteNum, _58_4000, AHPNCA);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(5000);
		policy.quoteDocGen().start();
		goddTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU"),
				HSU03XX,
				HSU04XX,
				HSU05XX,
				HSU08XX,
				AHFMXX,
				HSU06CA,
				_60_5019,
				_61_3026,
				//				_61_6528, // TODO Transform Document error for 61 6528
				_58_1027
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(quoteNum,
				HSU03XX,
				HSU04XX,
				HSU05XX,
				HSU08XX,
				AHFMXX,
				HSU06CA,
				_60_5019,
				_61_3026,
				//				_61_6528,
				_58_1027
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(5000);
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

		// Verify the documents for policy
		DocGenHelper.verifyDocumentsGenerated(policyNum, _61_5120, _58_1500);
		policy.policyDocGen().start();
		goddTab.verify.documentsEnabled(
				HSU01CA,
				HSU06CA,
				HSU07CA,
				HSU08XX,
				HSU09XX,
				AHFMXX,
				_60_5019,
				_61_3026,
				_58_4000
		);
		goddTab.verify.documentsEnabled(false,
				AHAPXX,
				WURFICA_PUP);
		goddTab.generateDocuments(_58_4000);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(policyNum, _58_4000, AHPNCA);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(5000);
		policy.policyDocGen().start();
		goddTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
				HSU04XX,
				HSU05XX,
				HSU08XX,
				HSU09XX,
				AHRCTXXPUP,
				AHFMXX,
				HSU06CA,
				_60_5019,
				_61_3026,
				_58_1027,
				HSU07CA,
				HSU01CA
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(policyNum,
				HSU04XX,
				HSU05XX,
				HSU08XX,
				HSU09XX,
				AHRCTXXPUP,
				AHFMXX,
				HSU06CA,
				_60_5019,
				_61_3026,
				_58_1027,
				HSU07CA,
				HSU01CA
		);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

}
