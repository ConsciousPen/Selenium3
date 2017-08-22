package aaa.modules.e2e.home_ss.ho3;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario3;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario3 extends Scenario3 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());

		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		super.createTestPolicy(policyCreationTD);
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC02_Generate_First_Bill() {
		super.TC02_Generate_First_Bill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Generate_Cancelation_Notice() {
		super.TC03_Generate_Cancelation_Notice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Cancel_Policy() {
		super.TC04_Cancel_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Create_Remittance_File() {
		super.TC05_Create_Remittance_File();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Pay_Cancellation_Notice_By_Remittance() {
		super.TC06_Pay_Cancellation_Notice_By_Remittance();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Renewal_Image_Generation() {
		super.TC07_Renewal_Image_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Renewal_Preview_Generation() {
		super.TC08_Renewal_Preview_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Offer_Generation() {
		super.TC09_Renewal_Offer_Generation();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Premium_Notice() {
		super.TC10_Renewal_Premium_Notice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Expire_Policy() {
		super.TC11_Expire_Policy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Customer_Decline_Renewal() {
		super.TC12_Customer_Decline_Renewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Bind_Renew() {
		super.TC13_Bind_Renew();
	}
}
