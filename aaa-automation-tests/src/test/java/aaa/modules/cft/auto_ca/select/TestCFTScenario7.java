package aaa.modules.cft.auto_ca.select;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 7
 * Existing policy - installment payment cash
 */
public class TestCFTScenario7 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	@StateList(states = {Constants.States.CA})
	public void cftTestScenario7(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateInstallmentBill(1);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
			"PremiumAndCoveragesTab_DataGather").getValue(AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}
