package aaa.modules.regression.sales.auto_ca.select.functional;

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

        return PolicyType.AUTO_CA_SELECT;
    }


    /**
     * @author Sreekanth Kopparapu
     * @name Auto CA Select - PayPlan is enabled on Billing tab once NB or endorsement is bound
     * @scenario 1. Create Customer
     * 2. Initiate Auto CA Select  Quote
     * 3. On P*C Page select any payplan available - say Annual and bind the policy
     * 4. Navigate to Billing tab
     * 5. On the page for 'Billing Account Policies' section the Payment plan value is enabled
     * 7. Follow the above steps for CA and the payment plan is enabled
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-21539")
    public void pas21539_testPayPlanIsDisabledInBillingTabCA(@Optional("CA") String state) {


        pas21539_BillingTabPaymentPlanCA();
    }

}
