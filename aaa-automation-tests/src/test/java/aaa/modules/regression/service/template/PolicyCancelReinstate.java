package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
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
		
		if (getUserGroup().equals(UserGroups.B31.get())) {
			mainApp().open(getLoginTD(UserGroups.QA));
			getCopiedPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			
			policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
			mainApp().close();
			
			//re-login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			log.info("Verifying 'Reinstatement' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Reinstatement' is available").doesNotContainOption("Reinstatement");			
		}
		else {
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
}
