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
import toolkit.datax.TestData;

public class ReinstatePolicyTest extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	private TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

	/**
	 * @name Reinstatement
	 * @scenario
	 * 1. User retrieves the policy which is in "Cancelled" state with following reasons:
	 * 2. Policy has cancelled due to insured request reason / UW cancel reason.
	 * 3. User tries to reinstate - System determines that Reinstate Date' is within the last 48 days of the policy term and displays the following message:"Policy will be reinstated with lapse. Do you wish to continue?".
	 * 4. User selects 'Yes' and policy reinstated with lapse
	 * 5. On policy consolidated view the status of Policy is active and  term includes lapse in policy.
	 * 6. Under transaction history, reinstatement without lapase is logged.
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_014_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//Reinstatement date field is disabled
		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	/**
	 * @name Reinstatement
	 * @scenario
	 * 1. "Retrieve an Active policy and make a flat cancellation"
	 * 2. On cancelling policy-  Payments & Other transactions section display an entry for cancellation.
	 * 3. Policy is reinstated with reinstatement date equal to cancellation effective date, and Total due is no more zero.
	 * 4. Under transaction history, reinstatement without lapase is logged.
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_016_ReinstatePolicy(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.cancel().perform(getStateTestData(tdPolicy, "Cancellation", "TestData"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.labelLapseExist).as("Lapse period flag is present").isPresent(false);

		assertThat(PolicySummaryPage.TransactionHistory.getType(1)).as("Reinstatement transaction added to Transaction History").isEqualTo("Reinstatement");
	}
}