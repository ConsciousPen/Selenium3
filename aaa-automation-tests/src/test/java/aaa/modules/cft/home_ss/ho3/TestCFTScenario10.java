package aaa.modules.cft.home_ss.ho3;

import aaa.main.modules.policy.home_ss.defaulttabs.*;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * Controlled Financial Testing Scenario 10
 * NB_FD_W/O Emp Ben NSF FEE_W/OFF ENOC
 */

public class TestCFTScenario10 extends ControlledFinancialBaseTest {
	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario10(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateInstallmentBill(1);
		acceptMinDuePaymentDD1plus30();
		acceptPaymentEP3(1);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()), "/today+1M");
		td.adjust(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), getTestSpecificTD("PremiumsAndCoveragesQuoteTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		td.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.class.getSimpleName()),
				getTestSpecificTD("PublicProtectionClass_DataGather"));
		td.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.Riskmeter.class.getSimpleName()),
				getTestSpecificTD("Riskmeter_DataGather"));
		td.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AgentInfo.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.AgentInfo.AGENCY_LOCATION.getLabel()), "index=1");
		return td.resolveLinks();
	}

}
