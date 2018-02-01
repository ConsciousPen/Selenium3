package aaa.modules.cft.auto_ca.choice;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.EndorsementActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

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
		endorsePolicyOnStartDatePlus2();
		endorseOOSPolicyOnStartDatePlus16(new String[]{new EndorsementActionTab().getMetaKey(), AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()});
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()),
			getTestSpecificTD("DriverTab_DataGather").getValue(AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()));
		td.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_ID.getLabel()),
			getTestSpecificTD("DriverTab_DataGather").getValue(AutoCaMetaData.DriverTab.EMPLOYEE_ID.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
			"PremiumAndCoveragesTab_DataGather").getValue(AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}
