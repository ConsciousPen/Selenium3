package aaa.modules.e2e.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario1;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	@Override
	public void step1() {
		super.step1();
	}
}
