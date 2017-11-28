package aaa.modules.e2e.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario12;
import toolkit.datax.TestData;

public class TestScenario12 extends Scenario12 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		super.createTestPolicy(policyCreationTD);		
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill(@Optional("") String state) {
		super.generateFirstBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_First_Bill(@Optional("") String state) {
		super.payFirstBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Renewal_Image_Generation(@Optional("") String state) {
		super.renewalImageGeneration();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Renewal_Preview_Generation(@Optional("") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Renewal_Offer_Generation(@Optional("") String state) {
		super.renewalOfferGeneration();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Renewal_Bill(@Optional("") String state) {
		super.generateRenewalBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Change_Payment_Plan_For_Renewal(@Optional("") String state) {
		super.changePaymentPlan();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Activate_AutoPay(@Optional("") String state) {
		super.enableAutoPay();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Pay_Renewal_Bill(@Optional("") String state) {
		super.payRenewalBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Update_Policy_Status(@Optional("") String state) {
		super.updatePolicyStatus();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_First_Bill_For_Renewal(@Optional("") String state) {
		super.generateFirstBillOfFirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_First_Bill_For_Renewal(@Optional("") String state) {
		super.payFirstBillOfFirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Generate_Second_Bill_For_Renewal(@Optional("") String state) {
		super.generateSecondBillOfFirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Second_Bill_For_Renewal(@Optional("") String state) {
		super.paySecondBillOfFirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Generate_Third_Bill_For_Renewal(@Optional("") String state) {
		super.generateThirdBillOfFirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Renewal_Image_Generation_For_First_Renewal(@Optional("") String state) {
		super.renewalImageGeneration_FirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Pay_Third_Bill_For_Renewal(@Optional("") String state) {
		super.payThirdBillOfFirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Renewal_Preview_Generation_For_First_Renewal(@Optional("") String state) {
		super.renewalPreviewGeneration_FirstRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Renewal_Offer_Generation_For_First_Renewal(@Optional("") String state) {
		super.renewalOfferGeneration_FirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Change_Payment_Plan_For_First_Renewal(@Optional("") String state) {
		super.changePaymentPlan_FirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Generate_Renewal_Bill_For_First_Renewal(@Optional("") String state) {
		super.generateRenewalBill_FirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC23_Pay_Renewal_Bill_For_First_Renewal(@Optional("") String state) {
		super.payRenewalBill_FirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC24_Update_Policy_Status_First_Renewal(@Optional("") String state) {
		super.updatePolicyStatus_FirstRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC25_Generate_First_Bill_For_Second_Renewal(@Optional("") String state) {
		super.generateFirstBillOfSecondRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC26_Pay_First_Bill_For_Second_Renewal(@Optional("") String state) {
		super.payFirstBillOfSecondRenewal();
	}
}
