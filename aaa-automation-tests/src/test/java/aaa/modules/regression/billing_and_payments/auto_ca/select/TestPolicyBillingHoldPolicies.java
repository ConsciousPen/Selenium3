package aaa.modules.regression.billing_and_payments.auto_ca.select;

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
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_SELECT)
	public void testBillingHoldPolicies(@Optional("CA") String state) {
		
		super.testHoldPolicies();
	}

}