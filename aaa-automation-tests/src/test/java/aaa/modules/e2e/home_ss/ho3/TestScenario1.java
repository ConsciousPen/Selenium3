package aaa.modules.e2e.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario1;
import toolkit.datax.TestData;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), 
				HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};

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
	public void TC03_Endorse_Policy(@Optional("") String state) {
		super.endorsePolicy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Pay_First_Bill(@Optional("") String state) {
		super.payFirstBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Generate_Second_Bill(@Optional("") String state) {
		super.generateSecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Pay_Second_Bill(@Optional("") String state) {
		super.paySecondBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Third_Bill(@Optional("") String state) {
		super.generateThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Third_Bill(@Optional("") String state) {
		super.payThirdBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Early_Renew_Not_Generated(@Optional("") String state) {
		super.earlyRenewNotGenerated();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Image_Generation(@Optional("") String state) {
		super.renewalImageGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Preview_Generation(@Optional("") String state) {
		super.renewalPreviewGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Offer_Generation(@Optional("") String state) {
		super.renewalOfferGeneration();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Premium_Notice(@Optional("") String state) {
		super.renewalPremiumNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Pay_Renewal_Bill(@Optional("") String state) {
		super.payRenewalBill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Update_Policy_Status(@Optional("") String state) {
		super.updatePolicyStatus();
	}
}
