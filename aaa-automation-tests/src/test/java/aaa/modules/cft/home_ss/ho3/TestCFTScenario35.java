package aaa.modules.cft.home_ss.ho3;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.cft.ControlledFinancialBaseTest;
import toolkit.utils.TestInfo;

public class TestCFTScenario35 extends ControlledFinancialBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Test(groups = {Groups.CFT})
	@TestInfo(component = Groups.CFT)
	@Parameters({STATE_PARAM})
	public void cftTestScenario35(@Optional(StringUtils.EMPTY) String state) {
		manualRenewalEntryOnStartDate();
		generateRenewalOffer();
		generateRenewalOfferBill();
		acceptMinDuePaymentOnUpdatePolicyStatusDate();
		verifyPolicyActiveOnUpdatePolicyStatusDate();
	}
}
