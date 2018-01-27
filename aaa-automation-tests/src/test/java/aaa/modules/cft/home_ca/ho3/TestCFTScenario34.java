package aaa.modules.cft.home_ca.ho3;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCFTScenario34 extends ControlledFinancialBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
				"PremiumsAndCoveragesQuoteTab_DataGather").getValue(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()));
		return td.resolveLinks();
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario34(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		endorsePolicyOnStartDatePlus2();
	}
}
