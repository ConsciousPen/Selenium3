package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestBillingTabPaymentPlanBehaviorTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestBillingTabPayPlanDisabled extends TestBillingTabPaymentPlanBehaviorTemplate {

    @Override
    protected PolicyType getPolicyType() {

        return PolicyType.AUTO_SS;
    }


    /**
     * @author Sreekanth Kopparapu
     * @name Auto SS - PayPlan is disabled for SS States Alone on Billing tab once NB is bound
     * @scenario 1. Create Customer
     * 2. Initiate Auto SS  Quote
     * 3. On P*C Page select any payplan available and bind the policy
     * 4. Navigate to Billing tab
     * 5. On the page for 'Billing Account Policies' section the Payment plan value is disabled
     * 7. Follow the above steps for CA and the payment plan is enabled
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21539")
    public void pas21539_testPayPlanIsDisabledInBillingTabSS(@Optional("") String state) {


        pas21539_BillingTabPaymentPlanSS();
    }
}

