package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class BillingHoldTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_059_BillingHold(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_059_BillingHold", SELECT_POLICY_QUERY_TYPE).get(0);

		verifyBillingHold(policyNumber);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_064_BillingHold(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_064_BillingHold", SELECT_POLICY_QUERY_TYPE).get(0);

		verifyBillingHold(policyNumber);
	}

	private void verifyBillingHold(String policyNumber) {
		mainApp().open();

		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		new BillingAccount().addHold().perform(getStateTestData(testDataManager.billingAccount, "AddHold", "TestData"));

		new BillingAccountPoliciesVerifier().setBillingStatus(BillingConstants.BillingStatus.ON_HOLD).verify(1);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
	}
}
