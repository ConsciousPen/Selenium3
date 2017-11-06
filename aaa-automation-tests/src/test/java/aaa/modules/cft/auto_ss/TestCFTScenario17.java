package aaa.modules.cft.auto_ss;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * Controlled Financial Testing Scenario 17
 * Authorized Refund - approved Lapse - Waive fee
 * Standard Monthly
 * Check Down Payment
 */
public class TestCFTScenario17 extends ControlledFinancialBaseTest {
	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario17(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		acceptTotalDuePlus600StartDatePlus16();
		refundPaymentAndApproveStartDatePlus25();
		endorsePolicyCancellationDate();
		endorseFirstEPBillDate(TestData.makeKeyPath(EndorsementActionTab.class.getSimpleName(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()));
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
			"PremiumAndCoveragesTab_DataGather").getValue(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}