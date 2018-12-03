package aaa.modules.regression.billing_and_payments.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBillingUpdate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyBillingUpdate extends PolicyBillingUpdate {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
	public void testUpdate_enableAutoPay(@Optional("CA") String state) {
		TestData td = getPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName()), getTestSpecificTD("TestData_PaymentPlan"))
				.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), getTestSpecificTD("TestData_PurchaseTab"));
		
		super.testUpdate_enableAutoPay(td);
	}
	
	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
	public void testUpdate_addPaymentMetodAndEnableAutoPay(@Optional("CA") String state) {
		
		super.testUpdate_addPaymentMethodAndEnableAutoPay();
	}

	
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3)
	public void testUpdate_disableAutoPay(@Optional("CA") String state) {
		TestData td = getPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName()), getTestSpecificTD("TestData_PaymentPlan"))
				.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), getTestSpecificTD("TestData_PurchaseTab_WithEnabledAutoPay"));
		
		super.testUpdate_disableAutoPay(td);
	}
}
