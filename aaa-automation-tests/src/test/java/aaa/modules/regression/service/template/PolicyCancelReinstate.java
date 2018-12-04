package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Jelena Dembovska
 * @name Test Policy Reinstate
 * @scenario
 * 1. Create Customer and Policy with default data
 * 2. Cancel Policy with Eff date = system date
 * 3. Verify Policy status is 'Cancelled'
 * 8. Perform Reinstate action
 * 9. Verify Policy status is 'Policy Active'
 * @details
 */
public class PolicyCancelReinstate extends PolicyBaseTest {

	public void testPolicyCancelReinstate() {
		
		mainApp().open();

		if (getUserGroup().equals(UserGroups.F35.get())||getUserGroup().equals(UserGroups.G36.get())) {
        	createCustomerIndividual();
            createPolicy();
        }
        else {
        	getCopiedPolicy();
        }

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);


		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);


		log.info("TEST: Reinstate Policy #" + policyNumber);

		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);


	}
}
