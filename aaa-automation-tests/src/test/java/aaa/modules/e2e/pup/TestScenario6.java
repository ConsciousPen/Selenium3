package aaa.modules.e2e.pup;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario6;

public class TestScenario6 extends Scenario6 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[]{new EndorsementActionTab().getMetaKey(), PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill(String state) {
		super.Generate_First_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Endorse_Policy(String state) {
		super.Endorse_Policy();

	}
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Pay_First_Bill(String state) {
		super.Pay_First_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Generate_CancellNotice(String state) {
		super.Generate_CancellNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Second_Bill(String state) {
		super.Generate_Second_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill(String state) {
		super.Pay_Second_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill(String state) {
		super.Generate_Third_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill(String state) {
		super.Pay_Third_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Set_Do_Not_Renew_Flag(String state) {
		super.Set_Do_Not_Renew_Flag();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation(String state) {
		super.Renewal_Image_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Preview_Generation(String state) {
		super.Renewal_Preview_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation(String state) {
		super.Renewal_Offer_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Update_Policy_Status(String state) {
		super.Update_Policy_Status();
	}
}
