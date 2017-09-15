package aaa.modules.e2e.auto_ca;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;

public class TestScenario5 extends Scenario5 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_Bill_One_Day_Before() {

	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_1_Generate_First_Bill() {
		super.Generate_First_Bill();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_Bill_One_Day_Before() {

	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_1_Pay_First_Bill() {
		super.Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Bill() {
		super.Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Bill() {
		super.Pay_Second_Bill();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Decline_Second_Payment() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill_Again() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Fourth_Bill() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation() { // only renewalOfferGenerationPart1 ?
		super.Renewal_Image_Generation();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Pay_Fourth_Bill() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Image_Generation() { // only renewalOfferGenerationPart2 ?
		super.Renewal_Image_Generation();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Offer_Generation() {
		super.Renewal_Offer_Generation();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Renewal_Bill() {

	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC_16_Update_Policy_Status() {

	}
}
