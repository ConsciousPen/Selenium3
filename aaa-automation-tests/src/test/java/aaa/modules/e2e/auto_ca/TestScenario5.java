package aaa.modules.e2e.auto_ca;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;

public class TestScenario5 extends Scenario5 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_Bill_One_Day_Before(@Optional("CA") String state) {

	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_1_Generate_First_Bill(@Optional("CA") String state) {
		super.Generate_First_Bill();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_Bill_One_Day_Before(@Optional("CA") String state) {

	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_1_Pay_First_Bill(@Optional("CA") String state) {
		super.Pay_First_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Bill(@Optional("CA") String state) {
		super.Generate_Second_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Bill(@Optional("CA") String state) {
		super.Pay_Second_Bill();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Decline_Second_Payment(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill_Again(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Fourth_Bill(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation(@Optional("CA") String state) { // only renewalOfferGenerationPart1 ?
		super.Renewal_Image_Generation();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Pay_Fourth_Bill(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Image_Generation(@Optional("CA") String state) { // only renewalOfferGenerationPart2 ?
		super.Renewal_Image_Generation();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.Renewal_Offer_Generation();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Renewal_Bill(@Optional("CA") String state) {

	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC_16_Update_Policy_Status(@Optional("CA") String state) {

	}
}
