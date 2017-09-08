package aaa.modules.e2e.home_ss.ho3;

import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.e2e.templates.Scenario8;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestScenario8 extends Scenario8 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test
	public void TC01_createPolicy() {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		premiumTab = new PremiumsAndCoveragesQuoteTab();
		errorTab = new ErrorTab();

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
		super.TC03_Endorse_Policy();
	}
}
