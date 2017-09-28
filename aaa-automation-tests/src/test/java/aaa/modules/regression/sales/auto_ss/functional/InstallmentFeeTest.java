package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Megha Gubbala
 * @name Test presence/status of Installment Fees on P&C and consolidated pages
 * @scenario 1. Create customer
 * 2. Create active policy with next conditions:
 * TS1: Current Payment Methd = 'Yes', Current Carrier = 'AAA NCNU - 500001005'
 * 3.Change payment method from annual to elevent pay
 * 3. Verify that on P&C page 'installment fees' are present in Payment section under view fee link and installment fee table   .
 * 4. Bind policy.
 * @details
 */


public class InstallmentFeeTest extends AutoSSBaseTest {

    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void installmentFee() {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();


        log.info("Policy Creation Started...");

        //TestData bigPolicy_td = getTestSpecificTD("TestData_UT");

        TestData bigPolicy_td = getTestSpecificTD("TestData_UT");

        policy.getDefaultView().fillUpTo(bigPolicy_td, PremiumAndCoveragesTab.class, true);

        //getPolicyType().get().createPolicy(bigPolicy_td);

        PremiumAndCoveragesTab.linkPaymentPlan.click();
        PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();

       /* Map<String, String> installmentFee_dataRow = new HashMap<>();
        installmentFee_dataRow.put("Payment Method", "Any");
        installmentFee_dataRow.put("Installment Fee", "$5.00");
        PremiumAndCoveragesTab.tableInstallmenFee.getRow(installmentFee_dataRow).verify.present();*/
        PremiumAndCoveragesTab.tableInstallmenFee.getRowContains(PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD, "Any").getCell(PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE).verify.value("$5.00");

        PremiumAndCoveragesTab.tableInstallmenFee.getRowContains(PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE).verify.value("$2.00");

        PremiumAndCoveragesTab.tableInstallmenFee.getRowContains(PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD, "Credit Card").getCell(PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE).verify.value("$2.00");

        PremiumAndCoveragesTab.tableInstallmenFee.getRowContains(PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD, "Debit Card").getCell(PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE).verify.value("$2.00");

    }
}
