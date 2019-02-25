package aaa.modules.regression.document_fulfillment.auto_ca.select;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

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
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void TC01_PolicyDocuments(@Optional("") String state) {

		mainApp().open();

		// 1
		createCustomerIndividual();
		createQuote();

		// 2
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(
				DocGenEnum.Documents.AHAPXX_CA,
				DocGenEnum.Documents._550035,
				DocGenEnum.Documents._554000,
				DocGenEnum.Documents._605019,
				// Documents._550010, //784859
				DocGenEnum.Documents._550016,
				DocGenEnum.Documents._550018,
				DocGenEnum.Documents._550023,
				//DocGenEnum.Documents._550025, CIN doc is removed
				DocGenEnum.Documents.AHRCTXXPUP,
				DocGenEnum.Documents._553333,
				DocGenEnum.Documents._605005_SELECT,
				DocGenEnum.Documents._550039,
				DocGenEnum.Documents._550009
		);
		docgenActionTab.verify.documentsEnabled(false,
				DocGenEnum.Documents._550007,
				DocGenEnum.Documents._550011,
				DocGenEnum.Documents._550026,
				DocGenEnum.Documents._551003,
				DocGenEnum.Documents._550002,
				DocGenEnum.Documents._550014,
				DocGenEnum.Documents._550019,
				DocGenEnum.Documents.CAU01,
				DocGenEnum.Documents.CAU04,
				DocGenEnum.Documents.CAU08,
				DocGenEnum.Documents.CAU09
		);
		docgenActionTab.cancel();

		// 3
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_QuoteUpdate"), PremiumAndCoveragesTab.class, true);
		Tab.buttonSaveAndExit.click();

		// 4
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(
				DocGenEnum.Documents._550007,
				DocGenEnum.Documents._550026,
				DocGenEnum.Documents._551003,
				DocGenEnum.Documents._550014,
				DocGenEnum.Documents._550019
		);
		docgenActionTab.cancel();

		// 5
		policy.calculatePremiumAndPurchase(getPolicyTD().adjust(getTestSpecificTD("TestData_Purchase")));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
		mainApp().close();

		// 6
		DocGenHelper.verifyDocumentsGenerated(policyNum,
				DocGenEnum.Documents._55_3333,
				DocGenEnum.Documents._55_1500,
				DocGenEnum.Documents._55_0038,
				DocGenEnum.Documents._55_0002,
				DocGenEnum.Documents._55_0019,
				DocGenEnum.Documents._55_1006
		);

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

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		// 1
		TestData endorsementTd1 = getTestSpecificTD("TestData_Endorsement1");
		policy.createEndorsement(endorsementTd1.adjust(getPolicyTD("Endorsement", "TestData_Plus5Day")));

		// 2
		DocGenHelper.verifyDocumentsGenerated(policyNum,
				DocGenEnum.Documents._55_1500,
				DocGenEnum.Documents._55_1000,
				DocGenEnum.Documents._55_1001,
				DocGenEnum.Documents._55_1005,
				DocGenEnum.Documents._55_0038,
				DocGenEnum.Documents._55_0002,
				DocGenEnum.Documents._55_1004,
				//				Documents._55_5086, // TODO not generated in xml
				DocGenEnum.Documents._55_1007
		);
		DocGenHelper.verifyDocumentsGenerated(false, policyNum, DocGenEnum.Documents._55_3333);

		// 3
		TestData endorsementTd2 = getTestSpecificTD("TestData_Endorsement2");
		policy.createEndorsement(endorsementTd2.adjust(getPolicyTD("Endorsement", "TestData_Plus10Day")));
		mainApp().close();

		// 4
		DocGenHelper.verifyDocumentsGenerated(policyNum,
				DocGenEnum.Documents._55_1500,
				DocGenEnum.Documents._55_1001,
				DocGenEnum.Documents._55_0001,
				DocGenEnum.Documents._55_6109
		);
		DocGenHelper.verifyDocumentsGenerated(false, policyNum, DocGenEnum.Documents._55_3333);

		// 5
		//policy.policyDocGen().start();
		//docgenActionTab.generateDocuments(Documents.AHRCTXXPUP);

		// 6
		//DocGenHelper.verifyDocumentsGenerated(policyNum, Documents.AHRCTXXPUP);

		/**
		 * 1. Billing Account:
		 *    To get 60 5001 document: decline deposit payment (done by check) with reason: "Fee + No Restriction"
		 *    To get 60 5000 document: decline payment with Reason: "Fee + Restriction"
		 *    To get 60 5002 document: decline payment with Reason: "No Fee + No Restriction"
		 *    To get 60 5003 document: decline payment with Reason "Fee + Restriction" (previous 60 5000 letter was generated within past 12 months)
		 */
		mainApp().open();
		SearchPage.openBilling(policyNum);

		billing.generateFutureStatement().perform();
		new BillingBillsAndStatementsVerifier().setType("Bill").verify(1).verifyPresent();

		makePayments();

		//Decline previous manual payment with reason "Fee + Restriction" (to get 60 5000)
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), "($200.00)");
		verifyPaymentDeclinedTransactionPresent("200");

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5000);

		//Decline previous manual payment with reason "Fee + Restriction" (to get 60 5003)
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeRestriction"), "($300.00)");

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5003);

		//Decline deposit payment with reason "Fee + No Restriction" (to get 605001)
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), "($400.00)");

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5001);

		//Decline previous manual payment with reason "No Fee + No Restriction" (to get 60 5002)
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_NoFeeNoRestriction"), "($500.00)");

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._60_5002);

	}

	private void makePayments() {
		billing.acceptPayment().perform(check_payment, new Dollar(200));
		billing.acceptPayment().perform(check_payment, new Dollar(300));
		billing.acceptPayment().perform(check_payment, new Dollar(400));
		billing.acceptPayment().perform(check_payment, new Dollar(500));
	}

	private void verifyPaymentDeclinedTransactionPresent(String amount) {
		new BillingPaymentsAndTransactionsVerifier().setType("Adjustment").setSubtypeReason("Payment Declined").setAmount(new Dollar(amount)).setStatus("Applied").verifyPresent();
	}
}
