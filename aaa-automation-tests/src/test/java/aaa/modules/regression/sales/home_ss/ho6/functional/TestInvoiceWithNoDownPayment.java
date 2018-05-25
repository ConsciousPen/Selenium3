package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInvoiceWithNoDownPaymentTemplate;
import toolkit.utils.TestInfo;

public class TestInvoiceWithNoDownPayment extends TestInvoiceWithNoDownPaymentTemplate {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentNB_HO6(@Optional("") String state) {

        pas9001_testInvoiceWithNoDownPaymentNB(PolicyType.HOME_SS_HO6);

    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO6, testCaseId = {"PAS-9001"})
    public void pas9001_testInvoiceWithNoDownPaymentEndorsement_HO6(@Optional("") String state) {

        pas9001_testInvoiceWithNoDownPaymentEndorsement(PolicyType.HOME_SS_HO6);

    }

}
