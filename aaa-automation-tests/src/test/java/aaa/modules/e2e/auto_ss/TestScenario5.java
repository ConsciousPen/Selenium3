package aaa.modules.e2e.auto_ss;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;

public class TestScenario5 extends Scenario5 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		super.createTestPolicy(policyCreationTD);
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	// public void TC02_Generate_Bill_One_Day_Before() {
	//
	// }

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_1_Generate_First_Bill() {
		super.Generate_First_Bill();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	// public void TC03_Pay_Bill_One_Day_Before() {
	//
	// }

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_1_Pay_First_Bill() {
		super.Pay_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Generate_Second_Bill() {
		super.Generate_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Pay_Second_Bill() {
		super.Pay_Second_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	// TC6--6,5
	public void TC06_Decline_Payments() {
		super.Decline_Payments();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	// public void TC07_Generate_CancellNotice_One_Day_Before() {
	//
	// }

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_1_Generate_CancellNotice() {
		super.Generate_CancellNotice();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	// public void TC08_1_Cancel_Policy_One_Day_Before() {
	//
	// }

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_1_Cancel_Policy() {
		super.Cancel_Policy();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	// public void TC09_Generate_EP_Bill_One_Day_Before() {
	//
	// }

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_1_Generate_First_EP_Bill() {
		super.Generate_First_EP_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Generate_Second_EP_Bill() {
		super.Generate_Second_EP_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Generate_Third_EP_Bill() {
		super.Generate_Third_EP_Bill();
	}

	// @Test(dependsOnMethods = "TC01_createPolicy")
	// public void TC12_Generate_EP_Write_Off_One_Day_Before() {
	//
	// }

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_1_Generate_EP_Write_Off() {
		super.Generate_EP_Write_Off();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Renewal_Image_Generation() {
		super.Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC14_Renewal_Preview_Generation() {
		super.Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC15_Renewal_Offer_Generation() {
		super.Renewal_Offer_Generation();
	}
}
