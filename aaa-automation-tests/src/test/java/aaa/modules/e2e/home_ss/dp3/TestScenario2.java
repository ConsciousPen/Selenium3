package aaa.modules.e2e.home_ss.dp3;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

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
	public void TC04_Billing_on_hold() {
		super.TC04_Billing_on_hold();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Bill_Not_Generated() {
		super.TC05_Bill_Not_Generated();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Second_Bill() {
		super.TC06_Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill() {
		super.TC07_Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill() {
		super.TC08_Generate_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill() {
		super.TC09_Pay_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Fourth_Bill() {
		super.TC10_Generate_Fourth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Pay_Fourth_Bill() {
		super.TC11_Pay_Fourth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_Fifth_Bill() {
		super.TC12_Generate_Fifth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_Fifth_Bill() {
		super.TC13_Pay_Fifth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Generate_Sixth_Bill() {
		super.TC14_Generate_Sixth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Sixth_Bill() {
		super.TC15_Pay_Sixth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Generate_Seventh_Bill() {
		super.TC16_Generate_Seventh_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Seventh_Bill() {
		super.TC17_Pay_Seventh_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Generate_Eighth_Bill() {
		super.TC18_Generate_Eighth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Pay_Eighth_Bill() {
		super.TC19_Pay_Eighth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Generate_Ninth_Bill() {
		super.TC20_Generate_Ninth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Pay_Ninth_Bill() {
		super.TC21_Pay_Ninth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Generate_Tenth_Bill() {
		super.TC22_Generate_Tenth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC23_Renewal_R_73() {
		super.TC23_Renewal_R_73();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC24_Pay_Tenth_Bill() {
		super.TC24_Pay_Tenth_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC25_Renewal_R_45() {
		super.TC25_Renewal_R_45();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC26_Renewal_R_35() {
		super.TC26_Renewal_R_35();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC27_Renewal_Premium_Notice() {
		super.TC27_Renewal_Premium_Notice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC28_Verify_DocGen_Forms() {
		super.TC28_Verify_DocGen_Forms();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC29_Remove_AutoPay() {
		super.TC29_Remove_AutoPay();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC30_Renewal_R() {
		super.TC30_Renewal_R();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC31_Update_Policy_Status() {
		super.TC31_Update_Policy_Status();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC32_Make_Manual_Payment_In_Full_Renewal_Offer_Amount() {
		super.TC32_Make_Manual_Payment_In_Full_Renewal_Offer_Amount();
	}
}