package aaa.modules.cft.home_ss.ho3;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * Controlled Financial Testing Scenario 15
 * Suspense_Allocate
 */
public class TestCFTScenario15 extends ControlledFinancialBaseTest {
	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario15(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		addSuspenseEffDatePlus2();
		clearSuspenseEffDatePlus16();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), getTestSpecificTD("PremiumsAndCoveragesQuoteTab_DataGather"));
		return td.resolveLinks();
	}
}
