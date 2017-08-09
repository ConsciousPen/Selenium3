package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.QuoteManualTask;

public class TestQuoteManualTask extends QuoteManualTask {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Test
	@Override
	public void testQuoteManualTask() {
		super.testQuoteManualTask();
	}
	
}
