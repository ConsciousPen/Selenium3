package aaa.modules.regression.billing_and_payments.template;

import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.RefundActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public abstract class PolicyBillingAddZellePaymentType extends PolicyBaseTest {

    private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
    private IBillingAccount billing = new BillingAccount();
    private BillingAccount billingAccount = new BillingAccount();


    /**
     * @name Test Billing - Add Payment Methods on Update Billing Account tab.
     * @scenario 1. Create a new customer
     * 2. Create a new policy.
     * 3. Navigate to Billing tab.
     * 4. Select 'Update' action in Take Action dropdown.
     * 5. Click on 'Add Credit Card/Bank Account' button in AutoPay Setup section.
     * 6. Add 'Zelle' payment type.
     * 7. Navigate back to Add Payment Method page and verify that Zelle payment methods are listed correctly.
     */
    public void testAddZellePaymentType() {
        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;
        String expectedZelleMobile = "Zelle " + tdBilling.getTestData("PaymentMethods").getValue("ZelleMobile", "Email Address or Mobile Number");
        String expectedZelleEmail = "Zelle " + tdBilling.getTestData("PaymentMethods").getValue("ZelleEmail", "Email Address or Mobile Number");

        //Navigate to Update Billing Account tab and add CC and EFT payment methods
        billing.update().perform(tdBilling.getTestData("Update", "TestData_AddPaymentMethod_Zelle"));

        //Navigate to Update Billing Account tab and verify that Zelle payment methods are listed correctly
        billing.update().start();

        AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
        String paymentMethodZelleMobile = AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Payment Method").getValue();
        String paymentMethodZelleEmail = AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(2).getCell("Payment Method").getValue();
        assertSoftly(softly -> {
            softly.assertThat(paymentMethodZelleMobile).as("Payment method is labeled incorrectly").contains(expectedZelleMobile);
            softly.assertThat(paymentMethodZelleEmail).as("Payment method is labeled incorrectly").contains(expectedZelleEmail);
        });
        Tab.buttonBack.click();

        //TODO Add additional test steps for performing a refund with Zelle once functionality is implemented. Also, add DB validation for storing Zelle info.
    }

    /**
     * @name Test Billing - Refund Payment Methods on Update Billing Account tab.
     * @scenario 1. Create a new customer
     * 2. Create a new policy.
     * 3. Navigate to Billing tab.
     * 4. Accept a payment for $100
     * 5. Initiate a refund
     * 6. Verify that Zelle option appears for Payment Method
     * */

    public void testRefundZellePaymentType(){

        RefundActionTab refundActionTab = new RefundActionTab();

        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;

        //Make a payment of $100
        billing.update().perform(tdBilling.getTestData("Update", "TestData_AddPaymentMethod_Zelle"));
        BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.equals(100);

        billingAccount.refund().start();

        assertThat(refundActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD)).containsOption("Zelle");


    }

}


