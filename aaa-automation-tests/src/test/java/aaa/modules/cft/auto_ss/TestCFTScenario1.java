package aaa.modules.cft.auto_ss;

import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 1
 * For any product and any defined state from params
 * NB W/O Emp Ben
 * Down pay_Cash
 * 1st installment
 * Cancel with future date
 * Earned Premium Write off
 */
public class TestCFTScenario1 extends ControlledFinancialBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1CreateQuote(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1CreateQuoteDD0();
		super.testCFTScenario1EndorsementX_2();
		super.testCFTScenario1CheckInstallmentsBillsGeneratedDD1_20();
		super.testCFTScenario1AutomaticCancellation();
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1EndorsementX_2(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1EndorsementX_2();
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1CheckInstallmentsBillsGeneratedDD1_20(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1CheckInstallmentsBillsGeneratedDD1_20();
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void testCFTScenario1AutomaticCancellation(@Optional(StringUtils.EMPTY) String state) {
		super.testCFTScenario1AutomaticCancellation();
	}
}
