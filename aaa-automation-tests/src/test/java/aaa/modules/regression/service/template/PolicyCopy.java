package aaa.modules.regression.service.template;

import toolkit.verification.CustomAssert;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public class PolicyCopy extends PolicyBaseTest {
	protected void testPolicyCopy() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
		policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestData").mask("ReportsTab"));
		CustomAssert.assertFalse("Copied policy number is the same as initial policy number " + policyNumber, policyNumber.equals(PolicySummaryPage.labelPolicyNumber.getValue()));
	}
}
