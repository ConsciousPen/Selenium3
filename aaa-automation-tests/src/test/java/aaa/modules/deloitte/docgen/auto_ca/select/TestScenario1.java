package aaa.modules.deloitte.docgen.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestScenario1 extends AutoCaSelectBaseTest {
	private PolicyDocGenActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	/** 
	 * 1. Create CA Select Quote
	 * 2. Check Documents on GODD: displayed, enable/disable
	 * 3. CA Select Quote:
	 *    To get 550026 document: Add Minor Violation with Include in Rating = No, Reason = Penalty Of Perjury
	 *    To get 550014 document: Set Financial Responsibility Filling Needed = Y, Filling Type = SR-22
	 *    To get 551003 document: Add Excluded Driver 
	 *    To get 550002 document: Add LSOPCE (Lienholder Statement Of Policy Coverage) form
	 *    To get 550019 document: Change ownership from Owned to Leased for vehicle 
	 *    To get 550007 document: Set Uninsured Motorist Coverage < 30000/60000
	 * 4. Check Documents on GODD: documents 550026, 550014, 551003, 550019, 550007 are enabled
	 * 5. Issue CA Select Quote
	 * 6. Check xml file
	*/
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void testPolicyDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();

		// 1
		createCustomerIndividual();
		createQuote();
		
		// 2
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(
				Documents.AHAPXX_CA,
				Documents._550035,
				Documents._554000,
				Documents._605019,
				Documents._550010,
				Documents._550016,
				Documents._550018,
				Documents._550023,
				Documents._550025,
				Documents.AHRCTXXPUP,
				Documents._553333,
				Documents._605005_SELECT,
				Documents._550039,
				Documents._550009
				);
		docgenActionTab.verify.documentsEnabled(false,
				Documents._550007,
				Documents._550011,
				Documents._550026,
				Documents._551003,
				Documents._550002,
				Documents._550014,
				Documents._550019,
				Documents.CAU01,
				Documents.CAU04,
				Documents.CAU08,
				Documents.CAU09
				);
		docgenActionTab.cancel();
		
		// 3
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoCaTab.DRIVER.get());
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_QuoteUpdate"), PremiumAndCoveragesTab.class, true);
		Tab.buttonSaveAndExit.click();
		
		// 4
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(
				Documents._550007,
				Documents._550026,
				Documents._551003,
				Documents._550014,
				Documents._550019
				);
		docgenActionTab.cancel();
		
		// 5
		policy.calculatePremiumAndPurchase(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase")));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		
		// 6
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents._55_3333,
				Documents._55_1500,
				Documents._55_0038,
				Documents._55_0002,
				Documents._55_0019,
				Documents._55_1006
				);
				
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
