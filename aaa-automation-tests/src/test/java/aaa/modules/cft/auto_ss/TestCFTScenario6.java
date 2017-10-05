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
 * Controlled Financial Testing Scenario 6
 * NB With EMP BEN_Quarterly
 * Down pay_ACH
 * Pending_OOSE
 * EFT _W/OFF
 * SR22 (SR22 only applies to auto)
 *
 */
public class TestCFTScenario6 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario6(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		endorsePolicyEffDatePlus2Days();
		endorseOOSPolicyEffDatePlus16Days();

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
