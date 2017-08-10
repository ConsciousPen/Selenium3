package aaa.modules.regression.service.template;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.pup.TestPolicyBackdated;

public abstract class PolicyCancellation extends PolicyBaseTest {

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
	protected void testPolicyCancellationFlat() {
		mainApp().open();

		getCopiedPolicy();

		log.info("TEST: Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}
	
	/**
	 * @author Ryan Yu
	 * @name Test Midterm Cancellation Policy
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Midterm Cancellation Policy
	 * 4. Verify Policy status is "Policy Cancelled"
	 * @details
	 */
	protected void testPolicyCancellationMidTerm() {
		mainApp().open();

		if (getPolicyType().equals(PolicyType.PUP)) {
			new TestPolicyBackdated().testPolicyBackdated();
		} else {
			createCustomerIndividual();
			createPolicy(getBackDatedPolicyTD());
		}
		log.info("TEST: MidTerm Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}
}
