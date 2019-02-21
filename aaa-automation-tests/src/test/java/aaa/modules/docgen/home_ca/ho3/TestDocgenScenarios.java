package aaa.modules.docgen.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestDocgenScenarios extends HomeCaHO3BaseTest {
	private PolicyDocGenActionTab documentActionTab = policy.policyDocGen().getView().getTab(PolicyDocGenActionTab.class);

	/**
	 * 1. Create CAH quote, rate, save and exit
	 * 2. Select On-Demand Documents option from Move To combobox and click go
	 * 3. Verify enabled:
	 *      61 6528 - 20938
	 *      F11 22 - 18531
	 *      61 6530 - 18607
	 *      HSU06CA - 18741
	 *      HSU08XX - 18745
	 *      61 3000 - 18606
	 *      61 3026 - 19757
	 *      F1076B - 20940
	 * 4. Verify disabled:
	 *      62 6500 - 20260
	 *      WURFICA - 18698
	 *      HSU01CA - 18742
	 *      HSU02XX - 18746
	 *      HSU07CA - 18737
	 *      HSU09XX - 18738
	 *      AHPNCA - 18541
	 * 5. Verify absent:
	 *      WUAUCA (Consumer Information Notice) - 18554 AC8
	 * <p/>
	 * 6. Verify that all enabled documents are generated
	 * 7. Verify that WUAUCA is generated together with HSU03XX
	 * 8. Verify that 61 2006 is generated with F1076B (20234 AC1), AHPNCA with 61 6528
	 * 9. Bind policy, go to on Demand Documents and verify that 61 6513 is not available (AC3)
	 * <p/>
	 * US:
	 * 20938:US CA Generate Quote Document- All Products v3
	 * 20940:US CA Generate Application Documents - HO3, HO4, HO6, DP3, PUP v4
	 * 18606:US CA GD 61 3000 Generate California Residential Property Insurance Bill of Rights
	 * 19757:US CA GD Generate Property Bill Plan Explanation v2
	 * 18546:US CA Generate Property Insurance Invoice (ac2)
	 * 18531:US CA Generate F11 22 Property Inventory List
	 * 18541:US CA Generate Privacy Information Notice
	 * 18560:US CA GD Generate Automatic Payment Plan Authorization
	 * 18738:US CA Generate Underwriting letter HSU09 Uprate(ac4)
	 * 18554:US CA Generate Consumer Information Notice
	 * 18607:US CA Generate 61 6530 Residential Property Insurance Disclosure
	 * 18741:US CA Generate Underwriting letter HSU06CA Free Form to Producer
	 * 18745:US CA Generate Underwriting letter HSU08XX Request Additional Info
	 * 20260:US CA Generate 62 6500 CA Evidence of Property Insurance v2
	 * 18698:US CA Generate WURFICA Request for Information
	 * 18742:US CA Generate Underwriting letter HSU01CA Advisory
	 * 18746:US CA Generate Underwriting letter HSU02 Cancellation
	 * 18737:US CA Generate Underwriting letter HSU07CA Non Renewal
	 * 20234:US CA CEA GD Offer of Earthquake Coverage Homeowners/Dwelling Fire Basic Earthquake Policy (POS) V02
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
					DocGenEnum.Documents._61_6528_HO3,
					DocGenEnum.Documents.F1122,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU08XX,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026,
					DocGenEnum.Documents.F1076B
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
			documentActionTab.verify.documentsPresent(softly, false, DocGenEnum.Documents._61_6513);

			documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU03"),
					DocGenEnum.Documents._61_6528_HO3,
					DocGenEnum.Documents.HSU03XX
			);
			WebDriverHelper.switchToDefault();
			//CIN (Customer information notice) removed in scope of PAS-3152 //Documents.WUAUCA
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum, DocGenEnum.Documents._61_6528_HO3, DocGenEnum.Documents.HSU03XX, DocGenEnum.Documents.AHPNCA);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.quoteDocGen().start();
			documentActionTab.generateDocuments(
					DocGenEnum.Documents.F1122,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU04XX
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
					DocGenEnum.Documents.F1122,
					DocGenEnum.Documents._61_6530,
					DocGenEnum.Documents.HSU04XX
			);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.quoteDocGen().start();
			documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU05"),
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026,
					DocGenEnum.Documents.HSU05XX
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
					DocGenEnum.Documents._61_3000,
					DocGenEnum.Documents._61_3026,
					DocGenEnum.Documents.HSU05XX
			);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.quoteDocGen().start();
			documentActionTab.generateDocuments(
					DocGenEnum.Documents.F1076B,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU08XX
			);
			WebDriverHelper.switchToDefault();
			DocGenHelper.verifyDocumentsGenerated(softly, quoteNum,
					DocGenEnum.Documents.F1076B,
					DocGenEnum.Documents.HSU06CA,
					DocGenEnum.Documents.HSU08XX
			);

			PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
			policy.purchase(getPolicyTD());
			String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
			policy.policyDocGen().start();
			documentActionTab.verify.documentsPresent(softly, false, DocGenEnum.Documents._61_6513);
			documentActionTab.cancel();
			log.info("==========================================");
			log.info(getState() + " HO3 Quote Documents Generation is checked, quote: " + policyNum);
			log.info("==========================================");
		softly.close();
	}

	/**
	 * 1. Create CAH policy with payment plan=Mortgagee Bill and Mortgagee Info filled on Mortgagee and Interest tab
	 * 2. Verify that the following forms are generated after bind:
	 *      61_6530
	 *      61_3000
	 *      61_5120
	 *      1075
	 * 3. Go to On Demand Documents page
	 * 4. Verify enabled:
	 *      WUAUCA - 18554
	 *      62 6500 - 20260
	 *      WURFICA 11 11 - 18698 AC1
	 *      F1122 - 18531
	 *      60 5019 - 18962 AC1
	 *      61 6530 - 18607
	 *      HSU01CA - 18742
	 *      HSU06CA - 18741
	 *      HSU07CA - 18737
	 *      HSU08XX - 18745
	 *      HSU09XX - 18738
	 *      61 3000 - 18606 AC3
	 *      61 3026 - 19757
	 *      F1076B - 20940
	 *      61 6513 CA - 18546
	 * <p/>
	 * 5. Verify disabled:
	 *      61 2006 - 20234 AC3
	 *      AHRCTXX
	 * <p/>
	 * 6. Verify absent:
	 *      61 6528 - 20938
	 *      AHPNCA - 18541
	 * <p/>
	 * 7. Verify 61 2006 is generated with Application document (F1076B) (20234 AC3)
	 * 8. Verify 60 5019 is generated with Application document (F1076B) (18962 AC2)
	 * 9. Verify AHPNCA is generated with Application document (F1076B) (18541 AC2)
	 * 10. Verify that all enabled documents are generated
	 */
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testPolicyDocuments(@Optional("") String state) {

		mainApp().open();

		createCustomerIndividual();
		String policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_PolicyDocuments")));
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents._61_6530, DocGenEnum.Documents._61_3000, DocGenEnum.Documents._61_5120, DocGenEnum.Documents._1075);

		policy.policyDocGen().start();
		documentActionTab.verify.documentsEnabled(softly,
				//CIN (Customer information notice) removed in scope of PAS-3152
				//Documents.WUAUCA,
				DocGenEnum.Documents._62_6500,
				//				Documents.WURFICA, // TODO Actually WURFICA is disabled, need to confirm the request
				DocGenEnum.Documents.F1122,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents._61_6530,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU06CA,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX,
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_3026,
				DocGenEnum.Documents.F1076B
				//				Documents._61_6513 // TODO Actually _61_6513 is not present, need to confirm the request
		);
		documentActionTab.verify.documentsEnabled(softly, false,
				DocGenEnum.Documents.HSU03XX
				//				Documents._61_2006 // TODO Actually _61_2006 is not present, need to confirm the request
		);
		documentActionTab.verify.documentsPresent(softly, false,
				DocGenEnum.Documents._61_6528_HO3,
				DocGenEnum.Documents.AHPNCA
		);
		documentActionTab.generateDocuments(DocGenEnum.Documents.F1076B);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents.F1076B,
				DocGenEnum.Documents._60_5019,
				//				Documents._61_2006,  // TODO Not persent, need to confirm the request
				DocGenEnum.Documents.AHPNCA);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(
				//CIN (Customer information notice) removed in scope of PAS-3152
				//Documents.WUAUCA,
				DocGenEnum.Documents._62_6500,
				//				Documents.WURFICA,
				DocGenEnum.Documents.F1122
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				//Documents.WUAUCA,
				DocGenEnum.Documents._62_6500,
				//				Documents.WURFICA,
				DocGenEnum.Documents.F1122
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU05"),
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents._60_5019,
				DocGenEnum.Documents.HSU01CA,
				DocGenEnum.Documents.HSU04XX,
				DocGenEnum.Documents.HSU05XX,
				DocGenEnum.Documents.HSU06CA
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU09"),
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents.HSU07CA,
				DocGenEnum.Documents.HSU08XX,
				DocGenEnum.Documents.HSU09XX
		);

		PolicySummaryPage.labelPolicyNumber.waitForAccessible(10000);
		policy.policyDocGen().start();
		documentActionTab.generateDocuments(
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_3026,
				DocGenEnum.Documents.HSU08XX
		);
		WebDriverHelper.switchToDefault();
		DocGenHelper.verifyDocumentsGenerated(softly, policyNum,
				DocGenEnum.Documents._61_3000,
				DocGenEnum.Documents._61_3026,
				DocGenEnum.Documents.HSU08XX
		);
		documentActionTab.cancel();
		softly.close();
	}

}
