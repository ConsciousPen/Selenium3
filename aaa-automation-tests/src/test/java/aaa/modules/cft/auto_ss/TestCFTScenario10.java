package aaa.modules.cft.auto_ss;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
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
		// TODO move to EP3 generation date and acceptPayment -> wrap in method to Parent class
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		billingAccount.acceptPayment().perform(getTestSpecificTD("AcceptPayment50"));

	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getPolicyTestData() {
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", DEFAULT_TEST_DATA_KEY);
		td.adjust(GeneralTab.class.getSimpleName(), getTestSpecificTD("GeneralTab_DataGather"));
		// td.adjust(VehicleTab.class.getSimpleName(), getTestSpecificTD("VehicleTab_DataGather"));
		td.adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_DataGather"));
		td.adjust(PurchaseTab.class.getSimpleName(), getTestSpecificTD("PurchaseTab_DataGather"));
		return td.resolveLinks();
	}

}
