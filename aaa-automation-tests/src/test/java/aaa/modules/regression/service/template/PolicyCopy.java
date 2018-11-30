package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public class PolicyCopy extends PolicyBaseTest {

	protected void testPolicyCopy() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
		policy.calculatePremiumAndPurchase(getPolicyTD("CopyFromPolicy", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyNumber).as("Copied policy number is the same as initial policy number").doesNotHaveValue(policyNumber);
	}
}
