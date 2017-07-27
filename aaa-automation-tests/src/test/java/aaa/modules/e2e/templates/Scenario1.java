package aaa.modules.e2e.templates;

import aaa.main.modules.policy.IPolicy;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class Scenario1 extends BaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected String policyNumber;
	
	public void step1() {
		policy = getPolicyType().get();
		tdPolicy = testDataManager.policy.get(getPolicyType());
		
		policyNumber = createPolicy(getStateTestData(tdPolicy, "DataGather", "TestData_Scenario1"));
	}

}
