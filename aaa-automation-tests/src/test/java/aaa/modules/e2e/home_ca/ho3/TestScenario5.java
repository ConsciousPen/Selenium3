package aaa.modules.e2e.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;

public class TestScenario5 extends Scenario5 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill(@Optional("CA") String state) {
		super.Generate_First_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_First_Bill(@Optional("CA") String state) {
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

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Decline_Payments(@Optional("CA") String state) {
		super.Decline_Payments();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_CancellNotice(@Optional("CA") String state) {
		super.Generate_CancellNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Verify_Form_AH34XX(@Optional("CA") String state) {
		super.Verify_Form_AH34XX();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Cancel_Policy(@Optional("CA") String state) {
		super.Cancel_Policy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_First_EP_Bill(@Optional("CA") String state) {
		super.Generate_First_EP_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Second_EP_Bill(@Optional("CA") String state) {
		super.Generate_Second_EP_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_Third_EP_Bill(@Optional("CA") String state) {
		super.Generate_Third_EP_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Generate_EP_Write_Off(@Optional("CA") String state) {
		super.Generate_EP_Write_Off();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Image_Generation(@Optional("CA") String state) {
		super.Renewal_Image_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Renewal_Preview_Generation(@Optional("CA") String state) {
		super.Renewal_Preview_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.Renewal_Offer_Generation();
	}
}
