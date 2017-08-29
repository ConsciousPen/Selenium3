package aaa.modules.e2e.pup;

import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
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
		super.generateFirstBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_First_Bill() {
		super.payFirstBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Billing_on_hold() {
		super.billingOnHold();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Bill_Not_Generated() {
		super.billNotGenerated();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Second_Bill() {
		super.generateSecondBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill() {
		super.paySecondBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill() {
		super.generateThirdBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill() {
		super.payThirdBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Fourth_Bill() {
		super.generateFourthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Pay_Fourth_Bill() {
		super.payFourthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_Fifth_Bill() {
		super.generateFifthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_Fifth_Bill() {
		super.payFifthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Generate_Sixth_Bill() {
		super.generateSixthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Pay_Sixth_Bill() {
		super.paySixthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Generate_Seventh_Bill() {
		super.generateSeventhBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Seventh_Bill() {
		super.paySeventhBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Generate_Eighth_Bill() {
		super.generateEighthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Pay_Eighth_Bill() {
		super.payEighthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Generate_Ninth_Bill() {
		super.generateNinthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Pay_Ninth_Bill() {
		super.payNinthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Generate_Tenth_Bill() {
		super.generateTenthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC23_Renewal_Image_Generation() {
		super.renewalImageGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC24_Pay_Tenth_Bill() {
		super.payTenthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC25_Renewal_Preview_Generation() {
		super.renewalPreviewGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC26_Renewal_Offer_Generation() {
		super.renewalOfferGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC27_Renewal_Premium_Notice() {
		if (!getState().equals(Constants.States.CA)) {
			super.renewalPremiumNotice();
		}
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC28_Verify_DocGen_Forms() {
		super.verifyDocGenForms();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC29_Remove_AutoPay() {
		super.removeAutoPay();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC30_Renewal_Payment_Not_Generated() {
		super.renewalPaymentNotGenerated();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC31_Update_Policy_Status() {
		super.updatePolicyStatus();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC32_Make_Manual_Payment_In_Full_Renewal_Offer_Amount() {
		super.makeManualPaymentInFullRenewalOfferAmount();
	}
}