package aaa.modules.regression.sales.home_ca.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.QuoteManualTask;

public class TestQuoteManualTask extends QuoteManualTask {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	@Test
	@Override
	public void testQuoteManualTask() {
		super.testQuoteManualTask();
	}
	
}
