package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class BillingHoldTest extends BackwardCompatibilityBaseTest {

	/**
	 * @author Deloite
	 * @name Billing Hold
	 * @scenario
	 * 1. System selects Hold option from the drop down
	 * 2.Fill necessary details and validate the the status of Billing as On hold
	 *
	 * Note: The scenario is applicable to all types of scheduled bills e.g. Installment Bill, Offcycle bill, Late Bill, Double Bill
	 * Check:
	 * 1. Billing Account is On-Hold status
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_059_BillingHold(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		verifyBillingHold(policyNumber);
	}

	/**
	 * @author Deloite
	 * @name Billing Hold
	 * @scenario
	 * 1. System displays Hold Billing screen with the following fields:
	 *     a. Policy # (this is displayed only if User clicks on Hold link against the Billing Account Policies )
	 *     b. Policy Type (this is displayed only if User clicks on Hold link against the Billing Account Policies )
	 *     c. Effective Date
	 *     d. End Date
	 *     e. Reason
	 * 2. User enters the following fields:
	 *     • Effective Date
	 *     • End Date
	 *     • Reason
	 * 3. User clicks on ‘OK’
	 * 4. System changes the Status of the Billing account from ‘Active’ to ‘On hold’.
	 *
	 * Check:
	 * Billing account is placed on Hold successfully.
	 * Policy status remains unchanged until Hold End Date or Policy expiration date.
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_064_BillingHold(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		verifyBillingHold(policyNumber);
	}

	private void verifyBillingHold(String policyNumber) {
		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);

		billingAccount.addHold().perform(getStateTestData(testDataManager.billingAccount, "AddHold", "TestData"));

		new BillingAccountPoliciesVerifier().setBillingStatus(BillingConstants.BillingStatus.ON_HOLD).verify(1);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
	}
}
