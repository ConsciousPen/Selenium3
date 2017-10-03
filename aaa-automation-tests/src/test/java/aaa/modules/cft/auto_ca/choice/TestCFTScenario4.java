package aaa.modules.cft.auto_ca.choice;

import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 4
 * NB_Down_Credit Card
 * 1st installment PT
 * Lapse w/o emp ben
 */
public class TestCFTScenario4 extends ControlledFinancialBaseTest {

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
		super.payInstallmentWithMinDue();
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
		super.acceptPaymentEffDatePlus25();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		td.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()),
				getTestSpecificTD("DriverTab_DataGather").getValue(AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()));
		td.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_ID.getLabel()),
				getTestSpecificTD("DriverTab_DataGather").getValue(AutoCaMetaData.DriverTab.EMPLOYEE_ID.getLabel()));
		return td.resolveLinks();
	}

}
