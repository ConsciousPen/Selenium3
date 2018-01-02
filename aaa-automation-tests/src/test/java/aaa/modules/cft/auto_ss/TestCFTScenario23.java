package aaa.modules.cft.auto_ss;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * Controlled Financial Testing Scenario 23
 * PLIGA fee processing
 * [ Applicable only for NJ state ]
 * Annual Payment Plan
 * Full payment (Cash) 
 */
public class TestCFTScenario23 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario23(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		generateRenewalImage();
		generateRenewalOffer();
		validatePLIGAFeeOnRenewGenOfferDate();
		generateRenewalOfferBill();
		acceptMinDuePaymentOnUpdatePolicyStatusDate();
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
		return td.resolveLinks();
	}
}
