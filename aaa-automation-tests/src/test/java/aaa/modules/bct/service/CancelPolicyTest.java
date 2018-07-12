package aaa.modules.bct.service;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class CancelPolicyTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_005_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_005_CancelPolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();

		// Search and open the active policy
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		deletePendingTransaction(policy);
		policy.cancel().perform(getTestSpecificTD("Cancellation_005"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_008_CancelPolicy(@Optional("") String state) {
		//TODO Test moved from Deloite's code as is, should be updated
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_008_CancelPolicy", "SelectPolicy").get(0);
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.CANCELLATION_PENDING);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_009_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_009_CancelPolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		CustomAssert.enableSoftMode();

		// Search and open the active policy
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		deletePendingTransaction(policy);
		policy.cancel().perform(getTestSpecificTD("Cancellation_009"));

		// Check if Status is updated to Policy Cancelled in the UI
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason("Cancellation - New Business Rescission - NSF on Down Payment")
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

		CustomAssert.assertAll();
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_012_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_012_CancelPolicy", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		BillingSummaryPage.openPolicy(1);

		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_013_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_013_CancelPolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();

		// Search and open the active policy
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getTestSpecificTD("Cancellation_013"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_015_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_015_CancelPolicy", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		BillingSummaryPage.openPolicy(1);

		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_112_Cancellation(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_112_Cancellation", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();

		// Search and open the active policy
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getTestSpecificTD("Cancellation_112"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
	}
}
