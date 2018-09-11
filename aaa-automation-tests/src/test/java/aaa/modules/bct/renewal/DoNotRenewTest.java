package aaa.modules.bct.renewal;

import static aaa.common.enums.Constants.States.CA;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class DoNotRenewTest extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_048_NonRenewal(@Optional("CA") String state) {
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		deletePendingTransaction(policy);

		policy.doNotRenew().start();
		Page.dialogConfirmation.confirm();
		policy.doNotRenew().getView().fill(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DoNotRenew", "TestData"));
		policy.doNotRenew().submit();

		PolicySummaryPage.verifyDoNotRenewFlagPresent();
	}
}
