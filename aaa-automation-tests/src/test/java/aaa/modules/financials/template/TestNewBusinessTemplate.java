package aaa.modules.financials.template;

import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

	protected void testNBZ_01() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getPolicyDefaultTD());
		POLICIES.add(PolicySummaryPage.getPolicyNumber());
	}

	protected void testNBZ_03() {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getBackDatedPolicyTD(getPolicyType(), DateTimeUtils.getCurrentDateTime().plusWeeks(1).format(DateTimeUtils.MM_DD_YYYY));
		if (getPolicyType().equals(PolicyType.PUP)) {
			td = new PrefillTab().adjustWithRealPolicies(td, getPrimaryPoliciesForPup());
		}
		createPolicy(td);
		POLICIES.add(PolicySummaryPage.getPolicyNumber());
	}



}
