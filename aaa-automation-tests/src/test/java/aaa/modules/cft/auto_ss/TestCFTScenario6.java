package aaa.modules.cft.auto_ss;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import aaa.utils.StateList;
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

	String[] endorsementEffDateDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()};

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	@StateList(statesExcept = {Constants.States.CA})
	public void cftTestScenario6(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		futureEndorsePolicyOnStartDatePlus2(endorsementEffDateDataKeys);
		endorseOOSPolicyOnStartDatePlus16(endorsementEffDateDataKeys);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel()),
				getTestSpecificTD("DriverTab_DataGather").getValue(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel()));
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
				"PremiumAndCoveragesTab_DataGather").getValue(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}
