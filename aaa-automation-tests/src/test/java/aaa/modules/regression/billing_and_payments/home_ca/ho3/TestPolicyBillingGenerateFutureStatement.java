package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingGenerateFutureStatement;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyBillingGenerateFutureStatement extends PolicyBillingGenerateFutureStatement {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
	public void testGenerateFutureStatement(@Optional("CA") String state) {
		TestData td = getPolicyDefaultTD().adjust(
				TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName()), 
				getTestSpecificTD("TestData_PaymentPlan"));
		
		super.testGenerateFutureStatement(td);
	}

}
