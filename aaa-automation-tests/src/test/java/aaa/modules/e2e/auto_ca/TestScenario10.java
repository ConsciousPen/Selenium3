package aaa.modules.e2e.auto_ca;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario10;
import toolkit.datax.TestData;

public class TestScenario10 extends Scenario10 { 
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		//endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
		//		AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
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
	public void TC06_Generate_Third_Bill(@Optional("CA") String state) {
		super.generateThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Third_Bill(@Optional("CA") String state) {
		super.payThirdBill();
	}	

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Renewal_Image_Generation(@Optional("CA") String state) {
		super.renewalImageGeneration();
	}
	
		@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Preview_Generation(@Optional("CA") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.renewalOfferGeneration();
	}
		
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Activate_AutoPay(@Optional("CA") String state) {
		super.enableAutoPay();
	}


	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Change_Payment_Plan_For_Renewal(@Optional("CA") String state) {
		super.changePaymentPlanForCA();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_Renewal_Offer(@Optional("CA") String state) {
		super.payRenewalOffer();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Update_Policy_Status(@Optional("CA") String state) {
		super.updatePolicyStatus();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Generate_First_Bill_For_Renewal(@Optional("CA") String state) {
		super.generateFirstBillOfRenewal();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Pay_First_Bill_For_Renewal(@Optional("CA") String state) {
		super.payFirstBillOfRenewal();
	}

}
