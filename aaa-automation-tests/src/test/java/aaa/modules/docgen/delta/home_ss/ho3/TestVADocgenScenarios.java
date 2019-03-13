package aaa.modules.docgen.delta.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestVADocgenScenarios extends HomeSSHO3BaseTest {
	private GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);

	/**
	 * TC Steps:
	 * 1. Create an HO3 quote, rate it and save;
	 * (Special conditions for this quote:
	 * For HSAUDVA,HSHUVA:
	 * Add HS 03 30 endorsement ( adds automatically if
	 * Insurance Score has been run and is available.
	 * Insured is not in Tier J.)
	 * For AHAUXX (From CL Story):
	 * Insurance Score has been run and is available.
	 * Insurance Score is lower than 925.
	 * There are NO CLUE chargeable loss that affects the premium (refer to Claim Rating stories).
	 * There are NO unapproved U/W eligibility errors.
	 * For HSINVAP
	 * HSINVA:
	 * No specific conditions, generates on bind.)
	 * <p/>
	 * 2. Go to On-Demand Documents tab.
	 * 3. Verify that HSAUDVA form is enabled.
	 * 4. Verify that AHAUXX form is absent.
	 * 5. Select HSAUDVA form and press "Generate" button
	 * 6. Verify that HSAUDVA form is generated in xml batch.
	 * 7. Bind the quote.
	 * 8. Verify that below forms are generated in xml batch:
	 * HSHUVA
	 * HS0330
	 * HSVAAD
	 * HSINVAP
	 * HSINVA
	 * 9. Go to On-Demand Documents tab.
	 * 10. Verify that below forms are absent:
	 * HSHUVA
	 * HSVAAD
	 * HSINVAP
	 * HSINVA
	 * <p/>
	 * Req:
	 * 17274:US VA GD-94 DO NOT Generate Consumer Information Notice
	 * 17435:US VA GD-143 Documents Tab - Documents Available for Printing (relates to 17274)
	 * 15787:US VA GD Generate Virginia Hurricane Deductible Advisory Notice To Policyholders (HO3, DP3)
	 * 15790:US VA GD Generate Policyholder Advisory Notice (Ho3)
	 * 15789:US VA GD Generate Important Information Regarding Your Insurance (HO3, HO4, HO6, DP3,PUP)
	 * 15786:US VA GD-142 Generate Virginia Adverse Action Underwriting Decision Notice ( HO3, HO4, HO6, DP3, PUP)
	 * 15788:US VA GD Generate Important Notice Regarding Flood and Earthquake (HO3, HO4, HO6, DP3)
	 * 15687:US SCL Hurricane Deductible Endorsement
	 */
	@Parameters({"state"})
	@StateList(states = States.VA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testDeltaPolicyDocuments(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_DeltaPolicyDocuments")));

		//policy.quoteDocGen().start();
		//documentActionTab.verify.documentsEnabled(Documents.HSAUDVA);
		//documentActionTab.verify.documentsPresent(false, Documents.AHAUXX);
		//documentActionTab.generateDocuments(Documents.HSAUDVA);
		//WebDriverHelper.switchToDefault();
		//DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents.HSAUDVA);

		//PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HS_03_30, DocGenEnum.Documents.HSVAAD, DocGenEnum.Documents.HSINVAP, DocGenEnum.Documents.HSINVA);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsPresent(softly, false, DocGenEnum.Documents.HSVAAD, DocGenEnum.Documents.HSINVAP, DocGenEnum.Documents.HSINVA);
		documentActionTab.cancel();
		softly.close();
	}
}
