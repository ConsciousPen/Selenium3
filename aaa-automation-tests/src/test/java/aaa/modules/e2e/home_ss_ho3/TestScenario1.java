package aaa.modules.e2e.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario1;
import toolkit.datax.TestData;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getStateTestData(tdPolicy, "TestScenario1", "TestData").resolveLinks());
		
		super.TC01_createPolicy(policyCreationTD);
	}
}
