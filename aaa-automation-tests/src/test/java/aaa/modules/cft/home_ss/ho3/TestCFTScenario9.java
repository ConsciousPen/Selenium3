package aaa.modules.cft.home_ss.ho3;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;

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

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario9(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateInstallmentBill(1);
		// No split policy for property
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		// TestData td = getTestSpecificTD("TestData_DataGather");
		// td.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.class.getSimpleName()),
		// getTestSpecificTD("PublicProtectionClass_DataGather"));
		// td.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.Riskmeter.class.getSimpleName()),
		// getTestSpecificTD("Riskmeter_DataGather"));
		// td.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AgentInfo.class.getSimpleName(),
		// HomeSSMetaData.ApplicantTab.AgentInfo.AGENCY_LOCATION.getLabel()), "index=1");
		return getTestSpecificTD("TestData_DataGather");
	}

}
