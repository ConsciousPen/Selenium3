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
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class ReinstatePolicyTest extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	private TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_014_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();

		SearchPage.openPolicy(policyNumber, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//Reinstatement date field is disabled
		policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_016_ReinstatePolicy(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();

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