package aaa.modules.bct.renewal;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;

public class DoNotRenewTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_048_NonRenewal(@Optional("") String state) {
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_048_NonRenewal", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();

		SearchPage.openPolicy(policyNumber);
		deletePendingTransaction(policy);
		policy.doNotRenew().perform(getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DoNotRenew", "TestData"));
		PolicySummaryPage.verifyDoNotRenewFlagPresent();
	}
}
