package aaa.modules.regression.billing_and_payments.home_ss.ho3.functional;

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
        return PolicyType.HOME_SS_HO3;
    }

    @Parameters({"state"})
    @StateList(statesExcept = Constants.States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.HOME_SS_HO3, testCaseId = {"PAS-29419"})
    public void test_addPaymentMethods(@Optional("AZ") String state) {

        super.testAddZellePaymentType();
    }

}
