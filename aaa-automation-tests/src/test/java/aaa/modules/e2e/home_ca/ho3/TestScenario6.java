package aaa.modules.e2e.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario6;

public class TestScenario6 extends Scenario6 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[]{new EndorsementActionTab().getMetaKey(), HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill(@Optional("CA") String state) {
		super.Generate_First_Bill();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Verify_Form_AHIBXX(@Optional("CA") String state) {
		super.Verify_Form_AHIBXX();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Endorse_Policy(@Optional("CA") String state) {
		super.Endorse_Policy();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_First_Bill(@Optional("CA") String state) {
		super.Pay_First_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_CancellNotice(@Optional("CA") String state) {
		super.Generate_CancellNotice();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Second_Bill(@Optional("CA") String state) {
		super.Generate_Second_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Second_Bill(@Optional("CA") String state) {
		super.Pay_Second_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Third_Bill(@Optional("CA") String state) {
		super.Generate_Third_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Pay_Third_Bill(@Optional("CA") String state) {
		super.Pay_Third_Bill();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Set_Do_Not_Renew_Flag(@Optional("CA") String state) {
		super.Set_Do_Not_Renew_Flag();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Image_Generation(@Optional("CA") String state) {
		super.Renewal_Image_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Preview_Generation(@Optional("CA") String state) {
		super.Renewal_Preview_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Offer_Generation(@Optional("CA") String state) {
		super.Renewal_Offer_Generation();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Manual_Renew_Policy(@Optional("CA") String state) {
		super.Manual_Renew_Policy();
	}

	// @Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Verify_Form_AHR1XX_And_HSRNXX(@Optional("CA") String state) {
		super.Verify_Form_AHR1XX_And_HSRNXX();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Renew_Offer(@Optional("CA") String state) {
		super.Pay_Renew_Offer();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Update_Policy_Status(@Optional("CA") String state) {
		super.Update_Policy_Status();
	}

	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Automatic_Refund_Not_Generated(@Optional("CA") String state) {
		super.Automatic_Refund_Not_Generated();
	}
}
