package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public class PolicyCopy extends PolicyBaseTest {

	protected void testPolicyCopy() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3) && 
				(getUserGroup().equals(UserGroups.F35.get())||getUserGroup().equals(UserGroups.G36.get()))) {
			policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData_F35_G36"));
			policy.calculatePremiumAndPurchase(getPolicyTD("CopyFromPolicy", "TestData_F35_G36"));
		}
		else {
	        policy.policyCopy().perform(getPolicyTD("CopyFromPolicy", "TestData"));
	    	policy.calculatePremiumAndPurchase(getPolicyTD("CopyFromPolicy", "TestData"));
	    }

		assertThat(PolicySummaryPage.labelPolicyNumber).as("Copied policy number is the same as initial policy number").doesNotHaveValue(policyNumber);
	}
}
