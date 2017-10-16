package aaa.modules.e2e.home_ss.ho3;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

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
	public void TC04_Billing_on_hold(@Optional("") String state) {
		super.billingOnHold();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Bill_Not_Generated(@Optional("") String state) {
		super.billNotGenerated();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Second_Bill(@Optional("") String state) {
		super.generateSecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill(@Optional("") String state) {
		super.paySecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill(@Optional("") String state) {
		super.generateThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill(@Optional("") String state) {
		super.payThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Fourth_Bill(@Optional("") String state) {
		super.generateFourthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Pay_Fourth_Bill(@Optional("") String state) {
		super.payFourthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_Fifth_Bill(@Optional("") String state) {
		super.generateFifthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_Fifth_Bill(@Optional("") String state) {
		super.payFifthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Generate_Sixth_Bill(@Optional("") String state) {
		super.generateSixthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Sixth_Bill(@Optional("") String state) {
		super.paySixthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Generate_Seventh_Bill(@Optional("") String state) {
		super.generateSeventhBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Seventh_Bill(@Optional("") String state) {
		super.paySeventhBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Generate_Eighth_Bill(@Optional("") String state) {
		super.generateEighthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Pay_Eighth_Bill(@Optional("") String state) {
		super.payEighthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Generate_Ninth_Bill(@Optional("") String state) {
		super.generateNinthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Pay_Ninth_Bill(@Optional("") String state) {
		super.payNinthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Renewal_Image_Generation(@Optional("") String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC23_Generate_Tenth_Bill(@Optional("") String state) {
		super.generateTenthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC24_Pay_Tenth_Bill(@Optional("") String state) {
		super.payTenthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC25_Renewal_Preview_Generation(@Optional("") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC26_Renewal_Offer_Generation(@Optional("") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC27_Renewal_Premium_Notice(@Optional("") String state) {
		super.renewalPremiumNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC28_Verify_DocGen_Forms(@Optional("") String state) {
		super.verifyDocGenForms();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC29_Remove_AutoPay(@Optional("") String state) {
		super.removeAutoPay();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC30_Renewal_Payment_Not_Generated(@Optional("") String state) {
		super.renewalPaymentNotGenerated();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC31_Update_Policy_Status(@Optional("") String state) {
		super.updatePolicyStatus();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC32_Make_Manual_Payment_In_Full_Renewal_Offer_Amount(@Optional("") String state) {
		super.makeManualPaymentInFullRenewalOfferAmount();
	}
}
