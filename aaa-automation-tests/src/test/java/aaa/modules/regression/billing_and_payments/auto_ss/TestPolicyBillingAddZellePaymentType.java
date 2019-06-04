package aaa.modules.regression.billing_and_payments.auto_ss;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingAddZellePaymentType;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestPolicyBillingAddZellePaymentType extends PolicyBillingAddZellePaymentType {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS)
	public void test_addPaymentMethods(@Optional("") String state) {
		
		super.testAddZellePaymentType();
	}

}
