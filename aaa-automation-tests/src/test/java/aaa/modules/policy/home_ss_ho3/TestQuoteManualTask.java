package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.QuoteManualTask;

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
