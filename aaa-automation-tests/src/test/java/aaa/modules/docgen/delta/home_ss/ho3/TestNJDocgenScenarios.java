package aaa.modules.docgen.delta.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestNJDocgenScenarios extends HomeSSHO3BaseTest {
	private GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);

	/**
	 * TC Steps:
	 * Create an HO3 quote;
	 * (condit for AHELCXXA,AHELCXXD,AHELCXXL,AHELCXXP - Extraordinary Life Circumstances = "Military deployment overseas" )
	 * Rate the quote.
	 * Save and exit the quote, Go to On-Demand Documents tab,
	 * Verify that below forms aren't present:
	 * HSEQNJ
	 * Select HS11NJ form and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * HSEQNJ
	 * HS11NJ
	 * Bind the quote
	 * Verify that below form is generated in xml batch after bind:
	 * HSCSNA
	 * Go to On-Demand Documents tab.
	 * Verify state and names of forms on the policy:
	 * AHELCXXA - enabled
	 * AHELCXXD - enabled
	 * AHELCXXL - enabled
	 * AHELCXXP - enabled
	 * HS11NJ - enabled
	 * Select HS11UT,AHELCXXA,AHELCXXD,AHELCXXL,AHELCXXP form and press "Generate" button
	 * Verify that below forms are generated in xml batch:
	 * AHELCXXA
	 * AHELCXXD
	 * AHELCXXL
	 * AHELCXXP
	 * HS11NJ
	 * HSEQNJ
	 * </pre>
	 * Req:
	 * 18177- US SCL Capture ELC Information
	 * 17414 - US SCL Determine Eligibility - ELC
	 * 17326   HO-DOC-ELC005-SCL-01 17326:US SCL GD-23 ELC UW Letter of Denial (AHELCXXD)
	 * 17803   HO-DOC-ELC003-SCL-01 17803:US SCL GD-22 ELC UW Letter of Approval (AHELCXXA)
	 * 17805   HO-DOC-ELC004-SCL-01 17805:US SCL GD-25 ELC UW Letter for No Premium benefit Denial (AHELCXXP)
	 * 17327   HO-DOC-ELC006-SCL-01 17327:US SCL GD-24 ELC UW Letter for Lack of Supporting Documents (AHELCXXL)
	 * 17257:US NJ GD- 18 Generate Homeowners Insurance (HSCSNA 11 12)
	 * 17313:US NJ GD-10 Generate New Jersey Earthquake Insurance Availability Notice (HSEQNJ 11 12)
	 */
	@Parameters({"state"})
	@StateList(states = States.NJ)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testDeltaPolicyDocuments(@Optional("") String state) {

			mainApp().open();

			createCustomerIndividual();
			String quoteNum = createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_DeltaPolicyDocuments")));

			policy.quoteDocGen().start();
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			documentActionTab.verify.documentsPresent(softly, false, DocGenEnum.Documents.HSEQNJ);
			documentActionTab.generateDocuments(DocGenEnum.Documents.HS11.setState(getState()));
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents.HSEQNJ, DocGenEnum.Documents.HS11);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			policy.getDefaultView().fillFromTo(getPolicyTD().adjust(getTestSpecificTD("TestData_DeltaPolicyDocuments")), BindTab.class, PurchaseTab.class, true);
			policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
			String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HSCSNA);

			policy.policyDocGen().start();
			documentActionTab.verify.documentsEnabled(softly,
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.AHELCXXA,
					DocGenEnum.Documents.AHELCXXD,
					DocGenEnum.Documents.AHELCXXL,
					DocGenEnum.Documents.AHELCXXP
			);
			documentActionTab.generateDocuments(
					DocGenEnum.Documents.HS11.setState(getState()),
					DocGenEnum.Documents.AHELCXXA,
					DocGenEnum.Documents.AHELCXXD,
					DocGenEnum.Documents.AHELCXXL,
					DocGenEnum.Documents.AHELCXXP
			);
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
					DocGenEnum.Documents.HS11,
					DocGenEnum.Documents.HSEQNJ,
					DocGenEnum.Documents.AHELCXXA,
					DocGenEnum.Documents.AHELCXXD,
					DocGenEnum.Documents.AHELCXXL,
					DocGenEnum.Documents.AHELCXXP
			);
			documentActionTab.cancel();
		softly.close();
	}
}
