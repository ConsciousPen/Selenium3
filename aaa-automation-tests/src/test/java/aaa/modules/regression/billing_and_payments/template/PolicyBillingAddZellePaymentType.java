package aaa.modules.regression.billing_and_payments.template;

import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

import static toolkit.verification.CustomAssertions.assertThat;

public abstract class PolicyBillingAddZellePaymentType extends PolicyBaseTest {

    /**
     * @name Test Billing - Add Payment Methods on Update Billing Account tab.
     * @scenario 1. Create a new customer
     * 2. Create a new policy.
     * 3. Navigate to Billing tab.
     * 4. Select 'Update' action in Take Action dropdown.
     * 5. Click on 'Add Credit Card/Bank Account' button in AutoPay Setup section.
     * 6. Add 'Zelle' payment type.
     * 7. Navigate back to Update Billing Account tab and verify that added payment methods present in 'Autopay Selection' dropdown.
     */
    public void testAddZellePaymentType() {
        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;

        //Navigate to Update Billing Account tab and add CC and EFT payment methods
        billing.update().perform(tdBilling.getTestData("ZellePaymentType", "TestData_AddPaymentMethods"));

        //Navigate to Update Billing Account tab and verify that AutoPay Selection dropdown contains added Zelle payment method
        billing.update().start();
        UpdateBillingAccountActionTab updateBillingAccountTab = new UpdateBillingAccountActionTab();
        AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
        String paymentMethodZelle = AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Payment Method").getValue();
        Tab.buttonBack.click();

        updateBillingAccountTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(true);

        ComboBox autopaySelectionCombobox = updateBillingAccountTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION);

        assertThat(autopaySelectionCombobox).as("AutoPay Selection dropdown doesn't contain Zelle payment method").containsOption(paymentMethodZelle);

        //TODO Add additional test steps for performing a refund with Zelle once functionality is implemented. Also, add DB validation for storing Zelle info.
    }
}

