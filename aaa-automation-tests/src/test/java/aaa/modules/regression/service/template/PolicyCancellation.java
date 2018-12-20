package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

public abstract class PolicyCancellation extends PolicyBaseTest {

	/**
	 * @author Ryan Yu
	 * @name Test Policy Cancellation Flat
	 * @scenario 1. Create Customer
	 * 2. Create Policy
	 * 3. Cancel policy
	 * 4. Verify Policy status is 'Policy Cancelled'
	 * @details
	 */
	protected void testPolicyCancellationFlat() {
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
			log.info("Verifying 'Cancellation' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Cancellation' is available").doesNotContainOption("Cancellation");	
		}
		else {
			mainApp().open();

			getCopiedPolicy();

			log.info("TEST: Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
			policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		}
	}

	/**
	 * @author Ryan Yu
	 * @name Test Midterm Cancellation Policy
	 * @scenario 1. Create Customer
	 * 2. Create Policy
	 * 3. Midterm Cancellation Policy
	 * 4. Verify Policy status is "Policy Cancelled"
	 * @details
	 */
	protected void testPolicyCancellationMidTerm() {
		
		if (getUserGroup().equals(UserGroups.B31.get())) {
			mainApp().open(getLoginTD(UserGroups.QA));
			createCustomerIndividual();
			createPolicy(getBackDatedPolicyTD());
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			mainApp().close();
			//re-login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			log.info("Verifying 'Cancellation' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Cancellation' is available").doesNotContainOption("Cancellation");	
		}
		else {
			mainApp().open();

			createCustomerIndividual();
			createPolicy(getBackDatedPolicyTD());
			log.info("TEST: MidTerm Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
			policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		}
	}
}
