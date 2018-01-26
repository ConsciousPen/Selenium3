package aaa.modules.cft.auto_ca.select;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * Controlled Financial Testing Scenario 2
 * For any product and any defined state from params
 * NB With Emp Ben
 * Down pay_ Check
 * Cancel
 * waive fee
 */
public class TestCFTScenario2 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario2(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		endorsePolicyOnStartDatePlus2();
		generateInstallmentBill(1);
		waiveFeeOnStartDatePlus16();
		manualFutureCancellationStartDatePlus25Days();
		updatePolicyStatusForPendedCancellation();
		manualReinstatement();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(DriverTab.class.getSimpleName(), getTestSpecificTD("DriverTab_DataGather"));
		td.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}
