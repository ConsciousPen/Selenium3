package aaa.modules.policy.templates;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;

public class ExampleQuoteActionTest extends ExampleQuoteActionAbstract {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void testQuoteCreation() {
		super.testQuoteCreation();
	}
	
	
	@Test
	public void testQuotePropose() {
		super.testQuoteCreation();
	}

}
