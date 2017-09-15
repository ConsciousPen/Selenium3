package aaa.modules.e2e.auto_ss;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.e2e.templates.Scenario6;

public class TestScenario6 extends Scenario6 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[]{new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		super.Generate_First_Bill();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Verify_Form_AHIBXX() {
		super.Verify_Form_AHIBXX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Endorse_Policy() {
		super.Endorse_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_First_Bill() {
		super.Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_CancellNotice() {
		super.Generate_CancellNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Generate_Second_Bill() {
		super.Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Pay_Second_Bill() {
		super.Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Generate_Third_Bill() {
		super.Generate_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Pay_Third_Bill() {
		super.Pay_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Set_Do_Not_Renew_Flag() {
		super.Set_Do_Not_Renew_Flag();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Image_Generation() {
		super.Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Preview_Generation() {
		super.Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Offer_Generation() {
		super.Renewal_Offer_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Manual_Renew_Policy() {
		super.Manual_Renew_Policy();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	public void TC16_Verify_Form_AHR1XX_And_HSRNXX() {
		super.Verify_Form_AHR1XX_And_HSRNXX();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC17_Pay_Renew_Offer() {
		super.Pay_Renew_Offer();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC18_Update_Policy_Status() {
		super.Update_Policy_Status();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC19_Automatic_Refund_Not_Generated() {
		super.Automatic_Refund_Not_Generated();
	}

	@Override
	protected void removeSecondVehicle() {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.getRow(2).getCell(5).controls.links.get("Remove").click();
		Page.dialogConfirmation.confirm();
	}

}
