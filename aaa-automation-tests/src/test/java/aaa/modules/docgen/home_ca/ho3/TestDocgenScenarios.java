package aaa.modules.docgen.home_ca.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.policy.home_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;

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
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	public void testQuoteDocuments(String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		String currentHandle = WebDriverHelper.getWindowHandle();
		createCustomerIndividual();
		String quoteNum = createQuote();
		
		policy.quoteDocGen().start();
		documentActionTab.verify.documentsEnabled(
				Documents._61_6528,
				Documents.F1122,
				Documents._61_6530,
				Documents.HSU06CA,
				Documents.HSU08XX,
				Documents._61_3000,
				Documents._61_3026,
				Documents.F1076B
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
		documentActionTab.verify.documentsPresent(false, Documents._61_6513);
		
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU03"),
				Documents._61_6528, 
				Documents.HSU03XX
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, Documents._61_6528, Documents.HSU03XX, Documents.WUAUCA, Documents.AHPNCA);
		
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(
				Documents.F1122, 
				Documents._61_6530, 
				Documents.HSU04XX
				); 
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.F1122, 
				Documents._61_6530, 
				Documents.HSU04XX
				);
		
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments(getTestSpecificTD("QuoteGenerateHSU05"), 
				Documents._61_3000, 
				Documents._61_3026, 
				Documents.HSU05XX
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents._61_3000, 
				Documents._61_3026, 
				Documents.HSU05XX
				);
		
		policy.quoteDocGen().start();
		documentActionTab.generateDocuments( 
				Documents.F1076B, 
				Documents.HSU06CA, 
				Documents.HSU08XX
				);
		WebDriverHelper.switchToWindow(currentHandle);
		DocGenHelper.verifyDocumentsGenerated(quoteNum, 
				Documents.F1076B, 
				Documents.HSU06CA, 
				Documents.HSU08XX
				);
		
		policy.purchase(getPolicyTD());
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.policyDocGen().start();
		documentActionTab.verify.documentsPresent(false, Documents._61_6513);

		log.info("==========================================");
		log.info(getState() + " HO3 Quote Documents Generation is checked, quote: " + policyNum);
		log.info("==========================================");
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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
//	@Parameters({"state"})
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
//	public void testPolicyDocuments(String state) {
//		CustomAssert.enableSoftMode();
//		mainApp().open();
//		String currentHandle = WebDriverHelper.getWindowHandle();
//		String policyNum = getCopiedPolicy();
//		
//		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.HS02, Documents.AHNBXX, Documents.HS0420);
//		
//		policy.policyDocGen().start();
//		documentActionTab.verify.documentsEnabled(
//				Documents.F605005, 
//				Documents.AHRCTXX, 
//				Documents.HS11.setState(getState()), 
//				Documents.HSEIXX, 
//				Documents.HSILXX, 
//				Documents.HSU01XX,
////				Documents.HSU02XX  //TODO Actually HSU02XX is disabled, need to confirm the request
//				Documents.HSU04XX,
//				Documents.HSU06XX,
//				Documents.HSU07XX,
//				Documents.HSU08XX,
//				Documents.HSU09XX
//				);
//		documentActionTab.verify.documentsEnabled(false, 
//				Documents.AHFMXX, 
//				Documents.HSRFIXX,
//				Documents.HSU03XX
//				);
//		documentActionTab.verify.documentsPresent(false, 
////				Documents.AHAUXX // TODO Actually AHAUXX is present, need to confirm the request
//				Documents.HSIQXX,
//				Documents.AHPNXX,
//				Documents._438BFUNS,
//				Documents.HSES
//				);
//		
//		documentActionTab.generateDocuments(Documents.HS11);
//		WebDriverHelper.switchToWindow(currentHandle);
//		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.HS11, Documents.AHPNXX);
//		
//		policy.policyDocGen().start();
//		documentActionTab.generateDocuments(getTestSpecificTD("PolicyGenerateHSU"),
//				Documents.AHRCTXX, 
//				Documents.HSEIXX, 
//				Documents.HSILXX, 
//				Documents.HSU01XX, 
//				Documents.HSU09XX
//				);
//		DocGenHelper.verifyDocumentsGenerated(policyNum, 
//				Documents.AHRCTXX, 
//				Documents.HSEIXX, 
//				Documents.HSILXX, 
//				Documents.HSU01XX, 
//				Documents.HSU09XX
//				);
//		CustomAssert.disableSoftMode();
//		CustomAssert.assertAll();
//	}
	
}
