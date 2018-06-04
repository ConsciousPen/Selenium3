package aaa.modules.regression.sales.home_ca.ho4.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInvoiceWithNoDownPaymentTemplate;
import toolkit.utils.TestInfo;

public class TestInvoiceWithNoDownPayment extends TestInvoiceWithNoDownPaymentTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO4;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentNB_HO4(@Optional("CA") String state) {

        pas9001_testInvoiceWithNoDownPaymentNB();

    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO4, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentEndorsement_HO4(@Optional("CA") String state) {

        pas9001_testInvoiceWithNoDownPaymentEndorsement();

    }

}
