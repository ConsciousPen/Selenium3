package aaa.modules.e2e.auto_ca;

import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario9;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario9 extends Scenario9 { 
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		super.createTestPolicy(policyCreationTD);		
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill(@Optional("CA") String state) {
		super.generateFirstBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Pay_First_Bill(@Optional("CA") String state) {
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
	public void TC06_Pay_Next_Seven_Installments(@Optional("CA") String state) {
		super.payNextSevenInstallments();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Third_Bill_Is_Not_Generated(@Optional("CA") String state) {
		super.verifyThirdBillNotGenerated();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Third_Payment_Is_Not_Generated(@Optional("CA") String state) {
		super.verifyPaymentNotGenerated();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Image_Generation(@Optional("CA") String state) {
		super.renewalImageGeneration();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Last_Bill(@Optional("CA") String state) {
		super.generateLastBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Pay_Last_Bill(@Optional("CA") String state) {
		super.payLastBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Remove_AutoPay(@Optional("CA") String state) {
		super.removeAutoPay();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Preview_Generation(@Optional("CA") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Endorsement_On_Current_Term(@Optional("CA") String state) {
		super.endorsementOnCurrentTerm();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Dont_Pay_Renewal_Bill(@Optional("CA") String state) {
		super.dontPayRenewalBill();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Update_Policy_Status(@Optional("CA") String state) {
		super.updatePolicyStatus();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Customer_Declined_Renewal(@Optional("CA") String state) {
		super.customerDeclineRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Generate_Earned_Premium_Write_Off(@Optional("CA") String state) {
		super.generateEarnedPremiumWriteOff();
	}
	
}
