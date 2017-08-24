package aaa.modules.e2e.auto_ca;

import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario1;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(),
				AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());
		
		super.createTestPolicy(policyCreationTD);
	}
	
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		super.generateFirstBill();
	}
	
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Endorse_Policy() {
		super.endorsePolicy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Pay_First_Bill() {
		super.payFirstBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Generate_Second_Bill() {
		super.generateSecondBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Pay_Second_Bill() {
		super.paySecondBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Third_Bill() {
		super.generateThirdBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Third_Bill() {
		super.payThirdBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Early_Renew_Not_Generated() {
		super.earlyRenewNotGenerated();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Image_Generation() {
		super.renewalImageGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Preview_Generation() {
		super.renewalPreviewGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Offer_Generation() {
		super.renewalOfferGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Pay_Renewal_Bill() {
		super.payRenewalBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Update_Policy_Status() {
		super.updatePolicyStatus();
	}
}
