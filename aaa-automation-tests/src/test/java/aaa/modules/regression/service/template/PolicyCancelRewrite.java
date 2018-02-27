package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Jelena Dembovska
 * @name Test Policy Rewrite
 * @scenario
 * 1. Create Customer and Policy with default data
 * 2. Cancel Policy with Eff date = system date
 * 3. Verify Policy status is 'Cancelled'
 * 8. Perform Rewrite action
 * 9. Verify Quote status is 'Data Gather'
 * 10. Issue Policy
 * 11. Verify Policy status is 'Policy Active'
 * 12. Verify new Policy number is different from cancelled policy number
 * @details
 */
public class PolicyCancelRewrite extends PolicyBaseTest {

	public void testPolicyCancelRewrite() {
		
		mainApp().open();

        String originalPolicyNumber = getCopiedPolicy();
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);


		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
		
		
		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);
		
		policy.dataGather().start();
		policy.getDefaultView().fill(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		assertThat(originalPolicyNumber).as("Rewriting Policy %s number is the same as initial policy number %s", originalPolicyNumber, rewritePolicyNumber)
				.isEqualTo(rewritePolicyNumber);
	}
}
