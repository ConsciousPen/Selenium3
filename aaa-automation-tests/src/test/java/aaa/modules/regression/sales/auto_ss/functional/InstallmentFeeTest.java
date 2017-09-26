package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class InstallmentFeeTest extends AutoSSBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void installmentfee() {

        mainApp().open();

        createCustomerIndividual();

        log.info("Policy Creation Started...");

        String paymentPlan = "Eleven Pay - Standard";
        //String paymentPlan = TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
        TestData premiumCoveragesAdjusted = getTestSpecificTD("TestData_UT").getTestData("PremiumAndCoveragesTab").adjust("Payment Plan", paymentPlan);

        TestData bigPolicy_td = getTestSpecificTD("TestData_UT").adjust("PremiumAndCoveragesTab", premiumCoveragesAdjusted);
        getPolicyType().get().createPolicy(bigPolicy_td);
    }
}
