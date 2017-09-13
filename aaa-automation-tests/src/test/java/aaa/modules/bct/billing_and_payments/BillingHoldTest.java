package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Test;

public class BillingHoldTest extends BackwardCompatibilityBaseTest {

	@Test
	public void BCT_ONL_059_BillingHold() {
		String policyNumber = getPoliciesByQuery("BCT_ONL_059_BillingHold", "SelectPolicy").get(0);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		new BillingAccount().addHold().perform(getStateTestData(testDataManager.billingAccount, "AddHold", "TestData"));

		new BillingAccountPoliciesVerifier().setBillingStatus(BillingConstants.BillingStatus.ON_HOLD).verify(1);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
	}

	@Test
	public void BCT_ONL_064_BillingHold() {
		String policyNumber = getPoliciesByQuery("BCT_ONL_064_BillingHold", "SelectPolicy").get(0);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		new BillingAccount().addHold().perform(getStateTestData(testDataManager.billingAccount, "AddHold", "TestData"));

		new BillingAccountPoliciesVerifier().setBillingStatus(BillingConstants.BillingStatus.ON_HOLD).verify(1);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
	}
}
