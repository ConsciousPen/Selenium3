package aaa.modules.regression.billing_and_payments.home_ca.ho3.functional;

import aaa.common.enums.Constants;
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
        return PolicyType.HOME_CA_HO3;
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3, testCaseId = {"PAS-29419"})
    public void test_addPaymentMethods(@Optional("CA") String state) {

        super.testAddZellePaymentType();
    }

    @Parameters({"state"})
    @StateList(states = Constants.States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_CA_HO3, testCaseId = {"PAS-29419"})
    public void test_RefundPaymentMethods(@Optional("") String state) {

        super.testRefundZellePaymentType();
    }

}
