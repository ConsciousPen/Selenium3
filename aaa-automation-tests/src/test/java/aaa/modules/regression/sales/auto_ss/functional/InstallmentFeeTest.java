package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class InstallmentFeeTest extends AutoSSBaseTest
{

    @Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void installmentfee() {

        mainApp().open();

        createCustomerIndividual();

        log.info("Policy Creation Started...");

        TestData bigPolicy_td = getTestSpecificTD("TestData_UT");
        getPolicyType().get().createPolicy(bigPolicy_td);
    }}
