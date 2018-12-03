package aaa.modules.cft.auto_ss;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import aaa.utils.StateList;
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

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	@StateList(statesExcept = {Constants.States.CA})
	public void cftTestScenario26(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		flatFutureCancellationOnDD1Minus20();
		approveRefundTotalPremiumOnDD1();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData
						.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
				getTestSpecificTD("GeneralTab_DataGather").getValue(
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
				"PremiumAndCoveragesTab_DataGather").getValue(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
		return td.resolveLinks();
	}

}
