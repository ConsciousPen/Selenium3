package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Alexander Tinkovan
 * @name Test Rewrite Homeowners Signature Series Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Homeowners Signature Series policy
 * 4. Issue Policy Eff date = system date
 * 5. Verify Policy status is 'Policy Active'
 * 6. Cancel Policy with Eff date = system date
 * 7. Verify Policy status is 'Cancelled'
 * 8. Perform Rewrite action
 * 9. Verify Quote status is 'Data Gather'
 * 10. Issue Policy
 * 11. Verify Policy status is 'Policy Active'
 * 12. Verify new Policy number is different from cancelled policy number
 * @details
 */
public class TestPolicyCancellationFlatRewrite extends HomeSSBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyCancellationFlatRewrite() {
		mainApp().open();

		createCustomerIndividual();
		String nbPolicyNumber = createPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: New Business Policy #" + nbPolicyNumber);

		policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.rewrite().perform(tdPolicy.getTestData("Rewrite", "TestDataSameDate"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);

		policy.calculatePremiumAndPurchase(tdPolicy.getTestData("Rewrite", "TestDataIssueRewritingPolicy"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		CustomAssert.assertFalse(String.format("Rewriting Policy %s number is the same as initial policy number %s", nbPolicyNumber, rewritePolicyNumber),
				nbPolicyNumber.equals(rewritePolicyNumber));

	}
}
