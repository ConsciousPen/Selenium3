package aaa.modules.cft.auto_ss;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;

/**
 * Halfyearly Renewal
 * Authorized Refund
 * Void Refund
 * Lapse with Emp Ben
 * Halfyearly 
 * Add 2V (Auto only)
 * Cash payment
 *
 */
public class TestCFTScenario20 extends ControlledFinancialBaseTest {
	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario20(@Optional(StringUtils.EMPTY) String state) {
		createPolicyForTest();
		acceptTotalDuePlusOverpaymentOnStartDatePlus2(new Dollar(200));
		issuedRefundOnStartDatePlus16(new Dollar(200));
		voidRefundOnStartDatePlus25();
		futureEndorsePolicyCancellationNoticeDate();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Override
	protected TestData getPolicyTestData() {
		return getTestSpecificTD("TestData_DataGather");
	}
}
