package aaa.modules.cft.auto_ss;

import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 3
 * For any product and any defined state from params
 * NB_Down_Cash
 * Policy Write off

 */
public class TestCFTScenario3 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void createPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.createPolicyForTest();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "createPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateBillForFirstInstallment(@Optional(StringUtils.EMPTY) String state) {
		super.generateFirstInstallmentBill();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "generateBillForFirstInstallment")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void endorsePolicy(@Optional(StringUtils.EMPTY) String state) {
		super.endorsePolicyEffDatePlus16Days();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "endorsePolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void acceptPayment(@Optional(StringUtils.EMPTY) String state) {
		super.acceptPayment10DollarsEffDatePlus25();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "acceptPayment")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void declinePayment(@Optional(StringUtils.EMPTY) String state) {
		super.decline10DollarsPayment();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "declinePayment")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cancelPolicy(@Optional(StringUtils.EMPTY) String state) {
		super.automaticCancellation();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "cancelPolicy")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateFirstEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.generateFirstEarnedPremiumBill();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "generateFirstEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateSecondEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.generateSecondEarnedPremiumBill();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "generateSecondEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void generateThirdEPBill(@Optional(StringUtils.EMPTY) String state) {
		super.generateThirdEarnedPremiumBill();
	}

	@Test(groups = {Groups.CFT}, dependsOnMethods = "generateThirdEPBill")
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1WriteOff(@Optional(StringUtils.EMPTY) String state) {
		super.writeOff();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}

}
