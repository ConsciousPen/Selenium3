package aaa.modules.cft.home_ss.ho3;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.cft.ControlledFinancialBaseTest;

/**
 * Controlled Financial Testing Scenario 11
 * Automatic Cancel
 * Collections
 * Standard Monthly
 * Cash $ 10 as down payment
 */
public class TestCFTScenario11 extends ControlledFinancialBaseTest {
	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario11(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		endorsePolicyOnStartDatePlus2();
		generateInstallmentBill(1);
		automaticCancellationNotice(1);
		automaticCancellation(1);
		generateFirstEarnedPremiumBill(1);
		generateSecondEarnedPremiumBill(1);
		generateThirdEarnedPremiumBill(1);
		try {
			generateCollection(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), getTestSpecificTD(
			"PremiumsAndCoveragesQuoteTab_DataGather").getValue(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}
}
