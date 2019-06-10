package aaa.modules.regression.billing_and_payments.pup.functional;

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
        return PolicyType.PUP;
    }

    @Parameters({"state"})
    @StateList(statesExcept = Constants.States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.BillingAndPayments.PUP)
    public void test_addPaymentMethods(@Optional("AZ") String state) {

        super.testAddZellePaymentType();
    }
}

