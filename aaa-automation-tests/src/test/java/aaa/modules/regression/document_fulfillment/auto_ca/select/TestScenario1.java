package aaa.modules.regression.document_fulfillment.auto_ca.select;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestScenario1 extends AutoCaSelectBaseTest {
	private PolicyDocGenActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(PolicyDocGenActionTab.class);
	private IBillingAccount billing = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private String policyNum;
	
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
	public void TC01_PolicyDocuments(@Optional("") String state) {
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
			 // Documents._550010, //784859
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
		policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		
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
	
	/** 
	 * 1. Do Mid-term Endorsement:
	 *    To get 55 1005 document: Change Employee Benefit Type to other than None for first driver
	 *    To get 55 1004 document: Set ADB = yes for first driver 
	 *    To get 55 1007 document: Add Trailer vehicle with business usage
	 *    To get 55 0038, 55 1001, 55 1000 document: Add vehicle with business usage. Add General Endorsement. 
	 *    To get 55 1000 document: Set add Co-Registered Car Endorsement (2 D for 1 Vehicle: must be registered domestic partners; must be US Citizen and have Drivers License in state that insured). US FR430-047
	 *    To get 55 5086 document: Add driver with chargeable activity 
	 *    To get 55 5002 document: Remove LSOPCE (Lienholder Statement Of Policy Coverage) form from first vehicle and Change ownership from Owned to Financed for third Vehicle
	 * 2. Check xml file
	 * 3. Do Mid-term Endorsement:
	 *    To get 55 0001 document: Change ownership from Financed to Owned for third Vehicle  
	 *    To get 55 6109 document: Remove vehicle with Lessor Form (first)   	
	 * 4. Check xml file
	 * 5. Check documents generation on GOOD:
	 *    Get AHRCTXX document: US 20986
	 * 6. Check AHRCTXX is generated
	*/
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_PolicyDocuments")
	public void TC02_EndorsementDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		// 1
		TestData endorsementTd1 = getTestSpecificTD("TestData_Endorsement1");
		policy.createEndorsement(endorsementTd1.adjust(getPolicyTD("Endorsement", "TestData_Plus5Day")));
		
		// 2
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents._55_1500,
				Documents._55_1000,
				Documents._55_1001,
				Documents._55_1005,
				Documents._55_0038,
				Documents._55_0002,
				Documents._55_1004,
//				Documents._55_5086, // TODO not generated in xml
				Documents._55_1007
				);
		DocGenHelper.verifyDocumentsGenerated(false, policyNum, Documents._55_3333);
		
		// 3
		TestData endorsementTd2 = getTestSpecificTD("TestData_Endorsement2");
		policy.createEndorsement(endorsementTd2.adjust(getPolicyTD("Endorsement", "TestData_Plus10Day")));
		
		// 4
		DocGenHelper.verifyDocumentsGenerated(policyNum, 
				Documents._55_1500,
				Documents._55_1001,
				Documents._55_0001,
				Documents._55_6109
				);
		DocGenHelper.verifyDocumentsGenerated(false, policyNum, Documents._55_3333);
		
		// 5
		policy.policyDocGen().start();
		docgenActionTab.generateDocuments(Documents.AHRCTXXPUP);
		
		// 6
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.AHRCTXXPUP);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	
	/** 
	 * 1. Billing Account:
	 *    To get 60 5001 document: decline deposit payment (done by check) with reason: "Fee + No Restriction"
	 *    To get 60 5000 document: decline payment with Reason: "Fee + Restriction"
	 *    To get 60 5002 document: decline payment with Reason: "No Fee + No Restriction"
	 *    To get 60 5003 document: decline payment with Reason "Fee + Restriction" (previous 60 5000 letter was generated within past 12 months)
	*/
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL }, dependsOnMethods = "TC01_PolicyDocuments")
	public void TC03_BillingDocuments(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		SearchPage.openBilling(policyNum);

		billing.generateFutureStatement().perform();
		new BillingBillsAndStatementsVerifier().setType("Bill").verify(1).verifyPresent();
		
		// Decline deposit payment with reason "Fee + No Restriction" (to get 605001)
		Map<String, String> map = new HashMap<String, String>();
		map.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Deposit Payment");
		map.put(BillingPaymentsAndOtherTransactionsTable.STATUS, "Issued");
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), map);
		
		// Decline previous manual payment with reason "Fee + Restriction" (to get 60 5000)
		billing.acceptPayment().perform(check_payment, new Dollar(200));
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), "($200.00)");
		
		// Decline previous manual payment with reason "No Fee + No Restriction" (to get 60 5002)
		billing.acceptPayment().perform(check_payment, new Dollar(300));
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_NoFeeNoRestriction"), "($300.00)");

		// Decline previous manual payment with reason "Fee + Restriction" (to get 60 5003)
		billing.acceptPayment().perform(check_payment, new Dollar(400));
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), "($400.00)");
		
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);
		
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum,
				Documents._60_5000,
				Documents._60_5001,
				Documents._60_5002,
				Documents._60_5003
				);
		DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.AHIBXX);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
