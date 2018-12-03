package aaa.modules.cft.auto_ss;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestCFTScenario35 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT, Groups.TIMEPOINT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	@StateList(states = {States.MD, States.PA})
	public void cftTestScenario35(@Optional(StringUtils.EMPTY) String state) {
		maigConversionOnStartDatePlus3(state);
		generateRenewalOffer();
		generateRenewalOfferBill();
		acceptMinDuePaymentOnUpdatePolicyStatusDate();
		verifyPolicyActiveOnUpdatePolicyStatusDate();
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
}
