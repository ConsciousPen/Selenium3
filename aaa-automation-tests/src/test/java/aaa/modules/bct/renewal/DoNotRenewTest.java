package aaa.modules.bct.renewal;

import static aaa.common.enums.Constants.States.CA;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class DoNotRenewTest extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	/**
	 * @author Deloite
	 * @name Non Renewal of a policy
	 * @scenario
	 * @param state
	 * Preconditions:
	 * 1. Policy status is active and a renewal offer has not been generated (policy is more than 35 days prior to renewal)
	 * 2. User has privilege <Policy Do Not Renew>
	 * Steps:
	 * 1. User Navigates to Policy Consolidated View tab
	 * 2. User selects action <Do Not Renew> and clicks ‘Go’
	 * 3. User enters following details:
	 * a. Reason
	 * b. Other Reason (if reason is selected as <Other>)
	 * 4. System displays pop-up message (MES-RE-0002) asking for confirmation as user clicks ‘OK’
	 * 5. User Clicks ‘OK’
	 * Check:
	 * Policy has successfully been set for Do Not Renew
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_048_NonRenewal(@Optional("CA") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openPolicy(policyNumber);
		deletePendingTransaction(policy);

		policy.doNotRenew().start();
		Page.dialogConfirmation.confirm();
		policy.doNotRenew().getView().fill(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DoNotRenew", "TestData"));
		policy.doNotRenew().submit();

		PolicySummaryPage.verifyDoNotRenewFlagPresent();
	}
}
