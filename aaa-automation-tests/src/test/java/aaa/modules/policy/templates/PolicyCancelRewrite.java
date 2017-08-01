package aaa.modules.policy.templates;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.verification.CustomAssert;

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
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        
        CustomAssert.enableSoftMode();

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
		
		
		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);
		
		policy.dataGather().start();
		policy.getDefaultView().fill(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"));
				

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		CustomAssert.assertFalse(String.format("Rewriting Policy %s number is the same as initial policy number %s", originalPolicyNumber, rewritePolicyNumber),
			originalPolicyNumber.equals(rewritePolicyNumber));
		
		
		CustomAssert.assertAll();
		
		
	}
}
