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
	public void TC02_0_Generate_First_Bill_One_Day_Before(@Optional("CA") String state) {
		super.generateFirstBillOneDayBefore();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_1_Generate_First_Bill(@Optional("CA") String state) {
		super.generateFirstBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_0_Pay_First_Bill_One_Day_Before(@Optional("CA") String state) {
		super.payFirstBillOneDayBefore();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_1_Pay_First_Bill(@Optional("CA") String state) {
		super.payFirstBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Bill(@Optional("CA") String state) {
		super.generateSecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Bill(@Optional("CA") String state) {
		super.paySecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Decline_Payments(@Optional("CA") String state) {
		super.declinePayments();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_0_Generate_CancellNotice_One_Day_Before(@Optional("CA") String state) {
		super.generateCancellNoticeOneDayBefore();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_1_Generate_CancellNotice(@Optional("CA") String state) {
		super.generateCancellNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Verify_Form_AH34XX(@Optional("CA") String state) {
		super.verifyFormAH34XX();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_0_Cancel_Policy_One_Day_Before(@Optional("CA") String state) {
		super.cancelPolicyOneDayBefore();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_1_Cancel_Policy(@Optional("CA") String state) {
		super.cancelPolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_0_Generate_First_EP_Bill_One_Day_Before(@Optional("CA") String state) {
		super.generateFirstEPBillOneDayBefore();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_1_Generate_First_EP_Bill(@Optional("CA") String state) {
		super.generateFirstEPBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Second_EP_Bill(@Optional("CA") String state) {
		super.generateSecondEPBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Generate_Third_EP_Bill(@Optional("CA") String state) {
		super.generateThirdEPBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_0_Generate_EP_Write_Off_One_Day_Before(@Optional("CA") String state) {
		super.generateEPWriteOffOneDayBefore();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_1_Generate_EP_Write_Off(@Optional("CA") String state) {
		super.generateEPWriteOff();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Image_Generation(@Optional("CA") String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Renewal_Preview_Generation(@Optional("CA") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.renewalOfferGeneration();
	}
}
