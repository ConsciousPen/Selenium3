package aaa.modules.cft.home_ss.ho3;


import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCFTScenario1 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void createPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.createPolicyForTest();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "createPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void endorsePolicy(@Optional(StringUtils.EMPTY) String state) {
		super.endorsePolicyEffDatePlus2Days();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "endorsePolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateBillForFirstInstallment(@Optional(StringUtils.EMPTY) String state) {
		super.generateFirstInstallmentBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateBillForFirstInstallment")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateCancellationNotice(@Optional(StringUtils.EMPTY) String state) {
		super.automaticCancellationNotice();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateCancellationNotice")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cancelPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.automaticCancellation();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "cancelPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateFirstEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.generateFirstEPBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateFirstEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateSecondEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.generateSecondEPBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateSecondEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateThirdEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.generateThirdEPBill();
	}

	@Test(groups = {Groups.CFT},dependsOnMethods = "generateThirdEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1WriteOff(@Optional(StringUtils.EMPTY) String state) {
		super.writeOff();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), getTestSpecificTD("PremiumsAndCoveragesQuoteTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}
