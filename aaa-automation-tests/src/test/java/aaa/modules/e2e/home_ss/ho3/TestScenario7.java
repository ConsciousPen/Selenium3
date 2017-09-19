package aaa.modules.e2e.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.e2e.templates.Scenario7;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumsAndCoveragesQuoteTab();
		errorTab = new ErrorTab();

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

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
	public void TC05_Generate_Second_Bill(@Optional("") String state) {
		super.generateSecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Total_Due(@Optional("") String state) {
		super.payTotalDue();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Third_Bill(@Optional("") String state) {
		super.generateThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Tenth_Bill(@Optional("") String state) {
		super.generateTenthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Image_Generation(@Optional("") String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Preview_Generation(@Optional("") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Endorsement_RP_Before_Renewal(@Optional("") String state) {
		super.TC12_Endorsement_RP_Before_Renewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Endorsement_AP_Before_Renewal(@Optional("") String state) {
		super.TC13_Endorsement_AP_Before_Renewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Offer_Generation(@Optional("") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Endorsement_RP_After_Renewal(@Optional("") String state) {
		super.endorsementRPAfterRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Endorsement_AP_After_Renewal(@Optional("") String state) {
		super.endorsementAPAfterRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Renewal_Premium_Notice(@Optional("") String state) {
		super.renewalPremiumNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Check_Renewal_Status_And_Payment_Not_Generated(@Optional("") String state) {
		super.checkRenewalStatusAndPaymentNotGenerated();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Expire_Policy(@Optional("") String state) {
		super.expirePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Generate_First_Renewal_Bill(@Optional("") String state) {
		super.generateFirstRenewalBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Customer_Decline_Renewal(@Optional("") String state) {
		super.customerDeclineRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Create_Remittance_File(@Optional("") String state) {
		super.createRemittanceFile();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Pay_Renewal_Bill_By_Remittance(@Optional("") String state) {
		super.payRenewalBillByRemittance();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Qualify_For_Manual_Renewal_Task_Created(@Optional("") String state) {
		super.qualifyForManualRenewalTaskCreated();
	}
}
