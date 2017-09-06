package aaa.modules.e2e.home_ss.ho3;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario6;

public class TestScenario6 extends Scenario6 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		super.TC02_Generate_First_Bill();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Verify_Form_AHIBXX() {
		super.TC03_Verify_Form_AHIBXX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Endorse_Policy() {
		super.TC04_Endorse_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_First_Bill() {
		super.TC05_Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_CancellNotice() {
		super.TC06_Generate_CancellNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Second_Bill() {
		super.TC07_Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Second_Bill() {
		super.TC08_Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Third_Bill() {
		super.TC09_Generate_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Pay_Third_Bill() {
		super.TC10_Pay_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Set_Do_Not_Renew_Flag() {
		super.TC11_Set_Do_Not_Renew_Flag();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Image_Generation() {
		super.TC12_Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Preview_Generation() {
		super.TC13_Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Offer_Generation() {
		super.TC14_Renewal_Offer_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Manual_Renew_Policy() {
		super.TC15_Manual_Renew_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Verify_Form_AHR1XX_And_HSRNXX() {
		super.TC16_Verify_Form_AHR1XX_And_HSRNXX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Renew_Offer() {
		super.TC17_Pay_Renew_Offer();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Update_Policy_Status() {
		super.TC18_Update_Policy_Status();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Automatic_Refund_Not_Generated() {
		super.TC19_Automatic_Refund_Not_Generated();
	}
}
