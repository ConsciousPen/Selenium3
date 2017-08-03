package aaa.modules.e2e.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.EndorsementActionTab;
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
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), 
				HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getStateTestData(tdPolicy, "TestScenario1", "TestData").resolveLinks());
		
		super.createTestPolicy(policyCreationTD);
	}
	
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		super.TC02_Generate_First_Bill();
	}
	
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Endorse_Policy() {
		super.TC03_Endorse_Policy();
	}
}
