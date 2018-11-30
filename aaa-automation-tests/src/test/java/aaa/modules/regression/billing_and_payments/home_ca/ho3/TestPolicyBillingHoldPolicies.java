package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingHoldPolicies;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestPolicyBillingHoldPolicies extends PolicyBillingHoldPolicies {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
	public void testBillingHoldPolicies(@Optional("CA") String state) {
		super.testHoldPolicies();
	}

}
