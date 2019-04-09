package aaa.modules.bct.service.auto_ca.select;

import static aaa.common.enums.Constants.States.CA;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class CancelPolicy extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Deloite
	 * @name View Cancelled policy status
	 * @param state
	 * @scenario
	 * Agent retrieves 1 active policy:
	 * Agent selects "New Business Rescission - Underwriting Material Misrepresentation" value for the dropdown field "Cancellation reason".
	 * "System does the following:
	 * Status of the policy is set as "Policy Cancelled."
	 * "Following transaction is created and displayed in the "Payments & Other Transactions" section of the Billing Tab.
	 * -"Type"= Premium
	 * -"Subtype/Reason" = Cancellation - New Business Recission - NSF on Down Payment
	 * -"Amount"= Policy Premium
	 * -"Status" = Applied"
	 * Check:
	 * Policy need to be retrieved and selected for Flat cancellation
	 * Status need to be updated as "Policy Cancelled"
	 */

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_013_CancelPolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getTestSpecificTD("Cancellation_013"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	/**
	 * @author Deloite
	 * @name View Cancelled policy status
	 * @param state
	 * @scenario
	 * "1.Retrieve an active CA state conversion auto policy from BCT data
	 * 2.Put an issued/active CA Select auto policy into Cancellation Pending status
	 * 3.Validate the View policy overview (summary) page,Primary drivers should be displayed in the section for the vehicles they are assigned in the Policy overview page."
	 * Check: Policy need to be set to cancellation pending status
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_112_Cancellation(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getTestSpecificTD("Cancellation_112"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
	}

}
