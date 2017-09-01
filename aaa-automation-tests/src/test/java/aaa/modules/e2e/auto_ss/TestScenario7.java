package aaa.modules.e2e.auto_ss;

import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.e2e.templates.Scenario7;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario7 extends Scenario7 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumsAndCoveragesQuoteTab();
		errorTab = new ErrorTab();

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
	public void TC04_Generate_Second_Bill() {
		super.generateSecondBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Cant_Change_Payment_Plan() {
		super.cantChangePaymentPlan();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Pay_Total_Due() {
		super.payTotalDue();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Third_Bill() {
		super.generateThirdBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Tenth_Bill() {
		super.generateTenthBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Image_Generation() {
		super.renewalImageGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Preview_Generation() {
		super.renewalPreviewGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Endorsement_RP_Before_Renewal() {
		super.TC12_Endorsement_RP_Before_Renewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Endorsement_AP_Before_Renewal() {
		super.TC13_Endorsement_AP_Before_Renewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation() {
		super.renewalOfferGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Endorsement_RP_After_Renewal() {
		super.endorsementRPAfterRenewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Endorsement_AP_After_Renewal() {
		super.endorsementAPAfterRenewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Renewal_Premium_Notice() {
		super.renewalPremiumNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Check_Renewal_Status_And_Payment_Not_Generated() {
		super.checkRenewalStatusAndPaymentNotGenerated();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Expire_Policy() {
		super.expirePolicy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Generate_First_Renewal_Bill() {
		super.generateFirstRenewalBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC20_Customer_Decline_Renewal() {
		super.customerDeclineRenewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC21_Create_Remittance_File() {
		super.createRemittanceFile();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC22_Pay_Renewal_Bill_By_Remittance() {
		super.payRenewalBillByRemittance();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC23_Qualify_For_Manual_Renewal_Task_Created() {
		super.qualifyForManualRenewalTaskCreated();
	}
}
