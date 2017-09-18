package aaa.modules.e2e.pup;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.actiontabs.EndorsementActionTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.modules.e2e.templates.Scenario6;

public class TestScenario6 extends Scenario6 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[]{new EndorsementActionTab().getMetaKey(), PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		super.Generate_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Endorse_Policy() {
		super.Endorse_Policy();

	}
	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Pay_First_Bill() {
		super.Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Generate_CancellNotice() {
		super.Generate_CancellNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Generate_Second_Bill() {
		super.Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Pay_Second_Bill() {
		super.Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Generate_Third_Bill() {
		super.Generate_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Pay_Third_Bill() {
		super.Pay_Third_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Set_Do_Not_Renew_Flag() {
		super.Set_Do_Not_Renew_Flag();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Renewal_Image_Generation() {
		super.Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Renewal_Preview_Generation() {
		super.Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Offer_Generation() {
		super.Renewal_Offer_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Update_Policy_Status() {
		super.Update_Policy_Status();
	}

	@Override
	protected void removeSecondVehicle() {
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		UnderlyingRisksAutoTab.tableAutomobiles.getRow(2).getCell(9).controls.links.get("Remove").click();
		Page.dialogConfirmation.confirm();
	}
}