package aaa.modules.e2e.home_ss.ho4;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario1;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
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

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Pay_First_Bill() {
		super.TC04_Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Generate_Second_Bill() {
		super.TC05_Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Pay_Second_Bill() {
		super.TC06_Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Third_Bill() {
		super.TC07_Generate_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Third_Bill() {
		super.TC08_Pay_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_R_74() {
		super.TC09_Renewal_R_74();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_R_73() {
		super.TC10_Renewal_R_73();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_R_45() {
		super.TC11_Renewal_R_45();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_R_35() {
		super.TC12_Renewal_R_35();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Premium_Notice() {
		super.TC13_Renewal_Premium_Notice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Pay_Renewal_Bill_R() {
		super.TC14_Pay_Renewal_Bill_R();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Update_Policy_Status() {
		super.TC15_Update_Policy_Status();
	}
}
