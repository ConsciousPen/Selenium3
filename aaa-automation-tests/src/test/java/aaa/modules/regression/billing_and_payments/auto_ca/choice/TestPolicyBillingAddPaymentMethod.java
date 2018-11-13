package aaa.modules.regression.billing_and_payments.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingAddPaymentMethod;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestPolicyBillingAddPaymentMethod extends PolicyBillingAddPaymentMethod {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_CHOICE)
	public void test_addPaymentMethods(@Optional("CA") String state) {
		
		super.testAddPaymentMethods();
	}

}
