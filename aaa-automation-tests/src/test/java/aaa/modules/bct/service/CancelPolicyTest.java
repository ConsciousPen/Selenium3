package aaa.modules.bct.service;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.modules.bct.BctType;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class CancelPolicyTest extends BackwardCompatibilityBaseTest {

	@Override
	protected BctType getBctType() {
		return BctType.ONLINE_TEST;
	}

	@Test
	public void BCT_ONL_005_CancelPolicy() {
		String policyNumber = getPoliciesByQuery("BCT_ONL_005_CancelPolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();

		mainApp().open();

		// Search and open the active policy
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getTestSpecificTD("Cancellation_005"));

		// Check if Status is updated to Policy Cancelled in the UI
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	@Test
	public void BCT_ONL_008_CancelPolicy() {
		String policyNumber = getPoliciesByQuery("BCT_ONL_008_CancelPolicy", "SelectPolicy").get(0);
		mainApp().open();
		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.CANCELLATION_PENDING);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
	}

	@Test
	public void BCT_ONL_009_CancelPolicy() {
		String policyNumber = getPoliciesByQuery("BCT_ONL_009_CancelPolicy", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();

		mainApp().open();
		CustomAssert.enableSoftMode();

		// Search and open the active policy
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getTestSpecificTD("Cancellation_005"));

		// Check if Status is updated to Policy Cancelled in the UI
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason("Cancellation - New Business Rescission - NSF on Down Payment")
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

		CustomAssert.assertAll();
	}

	@Test
	public void BCT_ONL_012_CancelPolicy() {
		String policyNumber = getPoliciesByQuery("BCT_ONL_012_CancelPolicy", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar minDue = BillingSummaryPage.getMinimumDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		BillingSummaryPage.openPolicy(1);

		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
	}

}
