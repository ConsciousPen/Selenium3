package aaa.modules.e2e.pup;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;

public class TestScenario5 extends Scenario5 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
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

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_First_Bill() {
		super.TC03_Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Bill() {
		super.TC04_Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Bill() {
		super.TC05_Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Decline_Payments() {
		super.TC06_Decline_Payments();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_CancellNotice() {
		super.TC07_Generate_CancellNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Verify_Form_AH34XX() {
		super.TC08_Verify_Form_AH34XX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Cancel_Policy() {
		super.TC09_Cancel_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_First_EP_Bill() {
		super.TC10_Generate_First_EP_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Second_EP_Bill() {
		super.TC11_Generate_Second_EP_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_Third_EP_Bill() {
		super.TC12_Generate_Third_EP_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Generate_EP_Write_Off() {
		super.TC13_Generate_EP_Write_Off();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Image_Generation() {
		super.TC14_Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Renewal_Preview_Generation() {
		super.TC15_Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Renewal_Offer_Generation() {
		super.TC16_Renewal_Offer_Generation();
	}

}
