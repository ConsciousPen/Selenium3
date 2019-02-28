package aaa.modules.docgen.pup;

import static aaa.main.enums.DocGenEnum.Documents.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.pup.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestDocgenScenariosCA extends PersonalUmbrellaBaseTest {

	String quoteNum;
	String policyNum;

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
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void TC01(@Optional("") String state) {
		mainApp().open();

		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		createCustomerIndividual();
		quoteNum = createQuote();

		// Verify the documents on quote GODD page
		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		goddTab.verify.documentsEnabled(softly,
				HSU06CA,
				HSU08XX,
				_61_3026,
				_58_4000,
				_61_6528
		);
		goddTab.verify.documentsEnabled(softly, false,
				HSU01CA,
				HSU02XX,
				HSU07CA,
				HSU09XX,
				AHAPXX
		);
		goddTab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, _58_4000);
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, _58_4000, AHPNCA);

		goddTab.cancel();
		softly.close();
	}

	public void TC02(@Optional("") String state) {

		mainApp().open();
		SearchPage.openQuote(quoteNum);

		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		policy.quoteDocGen().start();
		goddTab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, getTestSpecificTD("QuoteGenerateHSU"),
				HSU03XX,
				HSU04XX,
				HSU05XX,
				HSU08XX,
				AHFMXX,
				HSU06CA,
				_60_5019,
				_61_3026,
				_58_1027
		);

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
				HSU03XX,
				HSU04XX,
				HSU05XX,
				HSU08XX,
				AHFMXX,
				HSU06CA,
				_60_5019,
				_61_3026,
				_58_1027
		);

		goddTab.cancel();
		softly.close();
	}

	public void TC03(@Optional("") String state) {

		mainApp().open();
		SearchPage.openQuote(quoteNum);

		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);

		policy.purchase(getPolicyTD());

		policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

		// Verify the documents for policy
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, _61_5120, _58_1500);
		policy.policyDocGen().start();
		goddTab.verify.documentsEnabled(softly,
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
		goddTab.verify.documentsEnabled(softly, false,
				AHAPXX,
				WURFICA_PUP);
		goddTab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, null, _58_4000);
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, _58_4000, AHPNCA);

		goddTab.cancel();
		softly.close();
	}

	public void TC04(@Optional("") String state) {

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);

		policy.policyDocGen().start();
		goddTab.generateDocuments(true, DocGenEnum.DeliveryMethod.EMAIL, DocGenEnum.EMAIL, null, getTestSpecificTD("PolicyGenerateHSU"),
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

		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
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
		goddTab.cancel();
		softly.close();
	}
}
