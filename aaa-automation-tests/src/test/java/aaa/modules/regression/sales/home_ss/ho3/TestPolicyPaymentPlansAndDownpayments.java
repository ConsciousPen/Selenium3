package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;

@Test(groups = {Groups.REGRESSION, Groups.HIGH})
public class TestPolicyPaymentPlansAndDownpayments extends HomeSSHO3BaseTest {

    PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    BindTab bindTab = new BindTab();
    PurchaseTab purchaseTab = new PurchaseTab();

    /**
     * @author Jurij Kuznecov
     * @name Test HSS Policy Down payment calculations for different payment plans
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new HO3 policy
     * 3.  Change payment plan to 'Quarterly' and calculate premium
     * 4.  Navigate to 'Payment Tab' an verify:
     *      - Down payment  = Total Premium * 25.0%
     *      - Number of payments  =  3
     *      - Installment amount > 0
     *      - Remaining balance due today value = Down payment
     *      - Total remaining term premium = Total Premium - Down payment
     * 5.  Change payment plan to 'Eleven Pay' and calculate premium
     * 6.  Navigate to 'Payment Tab' an verify:
     *      - Down payment  = Total Premium * 16.67%
     *      - Number of payments  =  10
     *      - Installment amount > 0
     *      - Remaining balance due today value = Down payment
     *      - Total remaining term premium = Total Premium - Down payment
     * 9.  Change payment plan to 'Pay in Full' and calculate premium
     * 10. Navigate to 'Payment Tab' an verify:
     *      - Down payment  = Total Premium * 100.00%
     *      - Number of payments  =  0
     *      - Installment amount = 0
     *      - Remaining balance due today value = Down payment
     *      - Total remaining term premium = Total Premium - Down payment
     * 11. Change payment plan to 'Mortgagee Bill' and calculate premium
     * 12. Navigate to 'Payment Tab' an verify:
     *      - Down payment  = Total Premium * 0.00%
     *      - Number of payments  =  1
     *      - Installment amount > 0
     * 13. Change payment plan to 'Semi Annual' and calculate premium
     * 14. Navigate to 'Payment Tab' an verify:
     *      - Down payment  = Total Premium * 50.00%
     *      - Number of payments  =  1
     *      - Installment amount > 0
     *      - Remaining balance due today value = Down payment
     *      - Total remaining term premium = Total Premium - Down payment
     */

    @Test(enabled = true)
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void testPaymentsFiguresForDifferentPaymentPlans() {
        mainApp().open();
        createCustomerIndividual();
        createQuote();

        //   Quarterly
        changePlan(BillingConstants.PaymentPlan.QUARTERLY);
        verifyFigures(25.00, 3, true);

        //  Eleven Pay Standard
        changePlan(BillingConstants.PaymentPlan.ELEVEN_PAY);
        verifyFigures(16.67, 10, true);

        //  Pay in Full
        changePlan(BillingConstants.PaymentPlan.PAY_IN_FULL);
        verifyFigures(100.00, 0, false);

        //  Mortgagee Bill
        changePlan(BillingConstants.PaymentPlan.MORTGAGEE_BILL);
        verifyFigures(0.00, 1, true);

        //  Semi Annual
        changePlan(BillingConstants.PaymentPlan.SEMI_ANNUAL);
        verifyFigures(50.00, 1, true);
    }

    private void changePlan(String plan) {
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel(), ComboBox.class).setValue(plan);
        premiumsAndCoveragesQuoteTab.calculatePremium();
        if (plan == BillingConstants.PaymentPlan.MORTGAGEE_BILL) {
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
            new MortgageesTab().fillTab(getTestSpecificTD("TestData"));
        }
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();
    }

    private void verifyFigures(double percentOfTotalPremium, int numberOfPayments, boolean isMoreThenNull) {
        Dollar premium = new Dollar(purchaseTab.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.PREMIUM).getValue());
        Dollar downPayment = premium.multiply(percentOfTotalPremium).divide(100.0);
        new Dollar(purchaseTab.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.MINIMUM_DOWNPAYMENT).getValue()).verify.equals(downPayment);
        purchaseTab.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.NUMBER_OF_REMAINING_INSTALLMENTS).verify.value(String.valueOf(numberOfPayments));

        if (isMoreThenNull)
            new Dollar(purchaseTab.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.INSTALLMENT_AMOUNT).getValue()).verify.moreThan(new Dollar(0));
        else
            new Dollar(purchaseTab.tablePaymentPlan.getRow(1).getCell(PolicyConstants.PolicyPaymentPlanTable.INSTALLMENT_AMOUNT).getValue()).verify.equals(new Dollar(0));

        purchaseTab.remainingBalanceDueToday.verify.equals(downPayment);
        purchaseTab.fillTab(getPolicyTD().ksam(PurchaseTab.class.getSimpleName()));
        purchaseTab.totalRemainingTermPremium.verify.equals(premium.subtract(downPayment));

        PurchaseTab.buttonCancel.click();
    }
}
