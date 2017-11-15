package aaa.modules.e2e.auto_ss;

import aaa.common.enums.Constants;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.modules.e2e.templates.Scenario7;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumAndCoveragesTab();
		errorTab = new ErrorTab();
		tableDiscounts = PremiumAndCoveragesTab.tableDiscounts;

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
	public void TC04_Generate_Second_Bill(@Optional("") String state) {
		super.generateSecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Total_Due(@Optional("") String state) {
		super.payTotalDue();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Third_Bill(@Optional("") String state) {
		super.generateThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Renewal_Image_Generation_And_Renewal_Preview_Generation(@Optional("") String state) {
		super.renewalImageGeneration();
		if (getState().equals(Constants.States.KY)) {
			super.renewalPreviewGeneration();
		}
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Tenth_Bill(@Optional("") String state) {
		super.generateTenthBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Cant_Change_Payment_Plan(@Optional("") String state) {
		super.cantChangePaymentPlan();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Preview_Generation(@Optional("") String state) {
		if (!getState().equals(Constants.States.KY)) {
			super.renewalPreviewGeneration();
		}
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Endorsement_RP_Before_Renewal(@Optional("") String state) {
		super.endorsementRPBeforeRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Endorsement_AP_Before_Renewal(@Optional("") String state) {
		super.endorsementAPBeforeRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation(@Optional("") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Endorsement_RP_After_Renewal(@Optional("") String state) {
		super.endorsementRPAfterRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Endorsement_AP_After_Renewal(@Optional("") String state) {
		super.endorsementAPAfterRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Renewal_Premium_Notice(@Optional("") String state) {
		super.renewalPremiumNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Check_Renewal_Status_And_Payment_Not_Generated(@Optional("") String state) {
		super.checkRenewalStatusAndPaymentNotGenerated();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Expire_Policy(@Optional("") String state) {
		super.expirePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Generate_First_Renewal_Bill(@Optional("") String state) {
		super.generateFirstRenewalBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Customer_Decline_Renewal(@Optional("") String state) {
		super.customerDeclineRenewal();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Create_Remittance_File(@Optional("") String state) {
		super.createRemittanceFile();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Pay_Renewal_Bill_By_Remittance(@Optional("") String state) {
		super.payRenewalBillByRemittance();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC23_Qualify_For_Manual_Renewal_Task_Created(@Optional("") String state) {
		super.qualifyForManualRenewalTaskCreated();
	}
}
