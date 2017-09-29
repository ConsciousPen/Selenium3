package aaa.modules.cft.home_ss.ho3;


import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestCFTScenario1 extends ControlledFinancialBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void createPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1CreatePolicy();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "createPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void endorsePolicy(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1Endorsement();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "endorsePolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateBillForFirstInstallment(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1FirstInstallmentBillGeneration();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateBillForFirstInstallment")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateCancellationNotice(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1AutomaticCancellationNotice();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateCancellationNotice")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cancelPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1AutomaticCancellation();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "cancelPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateFirstEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1GenerateFirstEPBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateFirstEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateSecondEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1GenerateSecondEPBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateSecondEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateThirdEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1GenerateThirdEPBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateThirdEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1WriteOff(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1WriteOff();
	}

}
