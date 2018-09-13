package aaa.modules.financials.template;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.financials.FinancialsBaseTest;
import toolkit.utils.datetime.DateTimeUtils;

public class TestNewBusinessTemplate extends FinancialsBaseTest {

	protected void testNBZ_01() {
		openAppAndCreatePolicy();
		POLICIES.add(PolicySummaryPage.getPolicyNumber());
	}

	protected void testNBZ_03() {
		openAppAndCreatePolicy(getBackDatedPolicyTD(getPolicyType(), DateTimeUtils.getCurrentDateTime().plusWeeks(1).format(DateTimeUtils.MM_DD_YYYY)));
		POLICIES.add(PolicySummaryPage.getPolicyNumber());
	}



}
