package aaa.modules.regression.service.pup;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;


/**
 * @author Dmitry Kozakevich
 * @name Test Cancel and Rewrite Personal Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Personal Umbrella Policy
 * 3. Verify that Policy status is "Policy Active"
 * 4. Cancel the policy created in step 2
 * 5. Verify that Policy status is "Cancelled"
 * 6. Initiate Rewrite action for the policy cancelled in step 5
 * 7. Verify that status of the Quote is "Gathering Info"
 * 8. Issue the Quote from the step 7
 * 9. Verify that Policy status is "Policy Active"
 * 10. Verify that Policy Number of the initial policy is not the same as for the rewritten policy
 */
public class TestPolicyCancelRewrite extends PersonalUmbrellaBaseTest {

	@Test
	@TestInfo(component = "Policy.PUP")
	public void testPolicyCancelRewrite(){
		mainApp().open();

		getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String initialPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Initial Policy Number: " + initialPolicyNumber);

		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Policy " + initialPolicyNumber + " is cancelled");

		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameNumber"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

		policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("Rewritten Policy Number: " + rewrittenPolicyNumber);

		CustomAssert.assertFalse(String.format("Rewritten Policy Number %s is the same as Initial Policy Number %s", initialPolicyNumber, rewrittenPolicyNumber), rewrittenPolicyNumber.equals(initialPolicyNumber));
	}
}
