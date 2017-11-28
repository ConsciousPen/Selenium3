package aaa.modules.e2e.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario11;
import toolkit.datax.TestData;

public class TestScenario11 extends Scenario11 { 
	
	private Dollar toleranceAmount = new Dollar(10.00);
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		super.createTestPolicy(policyCreationTD);		
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Make_First_Endorsement(@Optional("") String state) {
		super.makeFirstEndorsement();
	} 

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Generate_First_Off_Cycle_Bill(@Optional("") String state) {
		super.generateFirstOffCycleBill();
	} 
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Pay_First_Off_Cycle_Bill(@Optional("") String state) {
		super.payFirstOffCycleBill();
	} 
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Make_Second_Endorsement(@Optional("") String state) {
		super.makeSecondEndorsement();
	} 

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Second_Off_Cycle_Bill(@Optional("") String state) {
		super.generateSecondOffCycleBill();
	} 
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Off_Cycle_Bill(@Optional("") String state) {
		super.paySecondOffCycleBill();
	} 
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Renewal_Image(@Optional("") String state) {
		super.renewalImageGeneration();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Renewal_Preview(@Optional("") String state) {
		super.renewalPreviewGeneration();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Renewal_Offer(@Optional("") String state) {
		super.renewalOfferGeneration();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Renewal_Bill(@Optional("") String state) {
		super.generateRenewalBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Update_Policy_Status(@Optional("") String state) {
		super.updatePolicyStatus();
	}
 	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_Renewal_Bill_Not_In_Full_Amount(@Optional("") String state) {
		super.payRenewalBillNotInFullAmount(toleranceAmount);
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Pay_Renewal_Offer_In_Full_Amount(@Optional("") String state) {
		super.payRenewalOfferInFullAmount(toleranceAmount);
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Make_Overpayment(@Optional("") String state) {
		super.makeOverpayment(); 
	} 
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Cancellation_Policy(@Optional("") String state) {
		super.cancellationPolicy();
	} 
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Generate_Refund(@Optional("") String state) {
		super.refundGeneration();
	} 
	
}
