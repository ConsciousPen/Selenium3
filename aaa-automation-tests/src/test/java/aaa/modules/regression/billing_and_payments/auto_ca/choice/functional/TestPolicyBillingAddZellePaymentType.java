package aaa.modules.regression.billing_and_payments.auto_ca.choice.functional;

import aaa.common.enums.Constants;
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
		return PolicyType.AUTO_CA_CHOICE;
	}
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_CHOICE, testCaseId = {"PAS-29419"})
	public void test_addPaymentMethods(@Optional("CA") String state) {
		
		super.testAddZellePaymentType();
	}


	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_CA_CHOICE, testCaseId = {"PAS-29419"})
	public void test_RefundPaymentMethods(@Optional("") String state) {

		super.testRefundZellePaymentType();
	}

}
