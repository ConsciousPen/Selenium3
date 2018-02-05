package aaa.modules.cft.home_ss.ho3;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 26
 * NB - future dated - payment in advance
 * refund from Premium in advance
 * Annual Payment Plan
 * Full payment (Cash) 
 */
public class TestCFTScenario26 extends ControlledFinancialBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData
						.makeKeyPath(GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()),
				getTestSpecificTD("GeneralTab_DataGather").getValue(HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
				"PremiumsAndCoveragesQuoteTab_DataGather").getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()));
		return td.resolveLinks();
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario26(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		flatFutureCancellationOnDD1Minus20();
		approveRefundTotalPremiumOnDD1();
	}
}
