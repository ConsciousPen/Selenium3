package aaa.modules.docgen.pup;

import static aaa.main.enums.DocGenEnum.Documents.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.pup.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestDocgenScenarios extends PersonalUmbrellaBaseTest {

	/**
	 * @author Lina Li
	 * @name Verify On-Demand Documents tab for PUP policy
	 * @scenario
	 * 1. Create an individual customer
	 * 2. Create a PUP quote and fill all mandatory fields;
	 * 3. Save and exit the quote and navigate to quote GODD page
	 * 4. Verify state of forms on the quote:
	 *    AHFMXX
	 *    PS11
	 *    PSIQXX
	 *    HSRFIXX
	 *    HSU01XX
	 *    HSU02XX
	 *    HSU03XX
	 *    HSU04XX
	 *    HSU05XX
	 *    HSU06XX
	 *    HSU07XX
	 *    HSU08XX
	 *    HSU09XX
	 * 5. Verify that below forms aren't present:
	 *    438 BFUNS
	 *    AHRCTXX
	 *    AHPNXX
	 *    PS02
	 *    AHNBXX
	 *    HSEIXX
	 *    HSES
	 * 6. Select PSIQXX form and clcik "Generate"
	 * 7. Verify that below forms are generated in xml batch:
	 *    PSIQXX
	 *    AHPNXX
	 * 8. Select PS11,AHFMXX forms and click "Generate"
	 * 9. Verify that below forms are generated in xml batch
	 *    PS11
	 *    AHFMXX
	 *    AHPNXX
	 * 10. Select HSU03XX,HSU04XX,HSU05XX,HSU06XX,HSU08XX forms and click "Generate"
	 * 11. Verify that below forms are generated in xml batch:
	 *     AHAUXX
	 *     HSU03XX
	 *     HSU04XX
	 *     HSU05XX
	 *     HSU06XX
	 *     HSU08XX
	 * 12.Reset premium(Set Personal Umbrella Coverage = 2000 and than back to 1000)
	 * 13.Save and exit quote without rating
	 * 14.Go to On-Demand Documents tab
	 * 15.Verify that forms  AHFMXX is disabled
	 * 16.Rate and bind the quote.
	 * 17. Verify that below forms are generated in xml batch after bind:
	 *     PS02
	 *     AHNBXX
	 * 18. Go to On-Demand Documents tab.
	 * 19. Verify state of forms on the policy
	 *     AHRCTXX
	 *     PS11
	 *     HSRFIXX
	 *     HSU01XX
	 *     HSU02XX
	 *     HSU03XX
	 *     HSU04XX
	 *     HSU05XX
	 *     HSU06XX
	 *     HSU07XX
	 *     HSU08XX
	 *     HSU09XX
	 * 20. Verify that below forms aren't present:
	 *     AHAUXX
	 *     PSIQXX
	 *     AHPNXX
	 *     438 BFUNS
	 *     HSES
	 *     HSEIXX
	 * 21. Select PS11,AHRCTXX,HSU01XX,HSU09XX forms and click "Generate" button
	 * 22. Verify that below forms are generated in xml batch:
	 *     PS11
	 *     AHPNXX
	 *     AHRCTXX
	 *     HSU01XX
	 *     HSU09XX
	 * @details
	 */

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.PA, States.UT})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testDocGen(@Optional("") String state) {

		mainApp().open();

		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		createCustomerIndividual();
		String quoteNum = createQuote();
		log.info("Create PUP Quote" + quoteNum);

		//Verify the documents on quote GODD page
		policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		goddTab.verify.documentsPresent(softly, AHFMXX, PS11, PSIQXX, HSRFIXXPUP, HSU01XX, HSU02XX, HSU03XX, HSU04XX, HSU05XX, HSU06XX, HSU07XX, HSU08XX, HSU09XX);
		goddTab.verify.documentsPresent(softly, false, _438BFUNS, AHRCTXX, AHPNXX, AHNBXX, HSEIXX, HSES, PS02);
		goddTab.verify.documentsEnabled(softly, HSU03XX, HSU04XX, HSU05XX, HSU06XX, HSU08XX, AHFMXX, PSIQXX, PS11);
		goddTab.verify.documentsEnabled(softly, true, HSU01XX, HSU02XX, HSU07XX, HSU09XX, HSRFIXXPUP);
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, null, null, null, PSIQXX);
		if (goddTab.buttonCancel.isPresent()) {
			goddTab.cancel(true);
		}
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, PSIQXX, AHPNXX);
		softly.close();

		policy.quoteDocGen().start();
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, null, null, null, PS11, AHFMXX);
		softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, PS11, AHPNXX, AHFMXX);
		softly.close();

		policy.quoteDocGen().start();
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, null, null, getTestSpecificTD("QuoteGenerateHSU"),
				HSU03XX, HSU04XX, HSU05XX, HSU06XX, HSU08XX);
		softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, HSU03XX, HSU04XX, HSU05XX, HSU06XX, HSU08XX);
		softly.close();

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
		policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class).fillTab(getTestSpecificTD("ChnagePersonalUmbrellaLimit2000"), false);
		Tab.buttonSaveAndExit.click();

		policy.quoteDocGen().start();
		softly = new ETCSCoreSoftAssertions();
		goddTab.verify.documentsEnabled(softly, false, AHFMXX);
		goddTab.cancel(true);

		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
		policy.getDefaultView().fill(getTestSpecificTD("ChnagePersonalUmbrellaLimit1000"));
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

		//Verify the documents for policy
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, PS02, AHNBXX);

		policy.policyDocGen().start();
		goddTab.verify.documentsPresent(softly, AHRCTXXPUP, PS11, HSRFIXXPUP, HSU01XX, HSU02XX, HSU03XX, HSU04XX, HSU05XX, HSU06XX, HSU07XX, HSU08XX, HSU09XX);
		goddTab.verify.documentsPresent(softly, false,
				PSIQXX,
				AHPNXX,
				_438BFUNS,
				HSEIXX,
				HSES);
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.CENTRAL_PRINT, null, null, getTestSpecificTD("PolicyGenerateHSU"), PS11, AHRCTXXPUP, HSU01XX, HSU09XX);
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, PS11, AHPNXX, AHRCTXXPUP, HSU01XX, HSU09XX);
		softly.close();
	}
}
