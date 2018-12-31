package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public class PolicyCopy extends PolicyBaseTest {

	protected void testPolicyCopy() {
		if (getUserGroup().equals(UserGroups.B31.get())) {
			mainApp().open(getLoginTD(UserGroups.QA));
			getCopiedPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			mainApp().close();
			//re-login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			log.info("Verifying 'Copy From  Policy' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Copy From Policy' is available").doesNotContainOption("Copy From Policy");
		}
		else {
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
}
