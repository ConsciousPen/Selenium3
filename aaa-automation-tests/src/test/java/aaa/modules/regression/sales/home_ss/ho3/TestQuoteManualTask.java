package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.QuoteManualTask;

public class TestQuoteManualTask extends QuoteManualTask {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	@Override
	public void testQuoteManualTask() {
		super.testQuoteManualTask();
	}
	
}
