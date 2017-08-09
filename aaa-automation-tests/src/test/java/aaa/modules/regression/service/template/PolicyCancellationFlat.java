package aaa.modules.regression.service.template;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Ryan Yu
 * @name Test Policy Cancellation Flat
 * @scenario
 * 1. Create Customer
 * 2. Create Policy
 * 3. Cancel policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * @details
 */
public abstract class PolicyCancellationFlat extends PolicyBaseTest {

	public void testPolicyCancellationFlat() {
		mainApp().open();

		getCopiedPolicy();

		log.info("TEST: Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}
}
