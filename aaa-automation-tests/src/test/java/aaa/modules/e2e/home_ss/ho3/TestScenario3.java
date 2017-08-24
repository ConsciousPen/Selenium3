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
		super.generateFirstBill();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC03_Generate_Cancelation_Notice() {
		super.generateCancelationNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC04_Cancel_Policy() {
		super.cancelPolicy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC05_Create_Remittance_File() {
		super.createRemittanceFile();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC06_Pay_Cancellation_Notice_By_Remittance() {
		super.payCancellationNoticeByRemittance();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC07_Renewal_Image_Generation() {
		super.renewalImageGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC08_Renewal_Preview_Generation() {
		super.renewalPreviewGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC09_Renewal_Offer_Generation() {
		super.renewalOfferGeneration();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC10_Renewal_Premium_Notice() {
		super.renewalPremiumNotice();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC11_Expire_Policy() {
		super.expirePolicy();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC12_Customer_Decline_Renewal() {
		super.customerDeclineRenewal();
	}

	@Test(dependsOnMethods = "TC01_createPolicy")
	public void TC13_Bind_Renew() {
		super.bindRenew();
	}
}
