package aaa.modules.cft.home_ss.ho3;

import aaa.common.enums.Constants;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
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
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 9
 * NB_FD_Emp Ben
 * NON EFT_W/OFF
 * SPLIT
 * Endorse_ADB (ADB only applies to auto)
 * Standard monthly
 * Policy Effective Date (Future Dated) = X+2
 * Cash Down Payment
 * Add 2I_2D_2V
 * Add Emp Ben
 */
public class TestCFTScenario9 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	@StateList(statesExcept = {Constants.States.CA})
	public void cftTestScenario9(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateInstallmentBill(1);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(ApplicantTab.class.getSimpleName(), getTestSpecificTD("ApplicantTab_DataGather"));
		td.adjust(ReportsTab.class.getSimpleName(), getTestSpecificTD("ReportsTab_DataGather"));
		td.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()),
				getTestSpecificTD("GeneralTab_DataGather").getValue(HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()),
				getTestSpecificTD("PremiumsAndCoveragesQuoteTab_DataGather").getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()));
		return td.resolveLinks();
	}
}
