package aaa.modules.regression.billing_and_payments.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.Tab;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

public abstract class PolicyBillingAddPaymentMethod extends PolicyBaseTest{

	/** 
	 * @author oreva
	 * @name Test Billing - Add Payment Methods on Update Billing Account tab.
	 * @scenario
	 * 1. Create a new customer or retrieve existed. 
	 * 2. Create a new policy.
	 * 3. Navigate to Billing tab. 
	 * 4. Select 'Update' action in Take Action dropdown.
	 * 5. Click on 'Add Credit Card/Bank Account' button in AutoPay Setup section.
	 * 6. Add 'Credit/Debit Card' and 'Checking/Savings (ACH)' payment methods. 
	 * 7. Navigate back to Update Billing Account tab and verify that added payment methods present in 'Autopay Selection' dropdown.
	 * 8. Navigate to Accept Payment tab and verify that Payment Methods dropdown contains payment methods added on Update tab. 
	 * 9. Add one more payment method (CC-Master Card) from Accept Payment tab.
	 * 10. Verify that added payment method CC-Master Card available on Accept Payment and Update Billing Account tabs.
	 */
	public void testAddPaymentMethods() {		
		mainApp().open();
		getCopiedPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
		BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;
        
        //Navigate to Update Billing Account tab and add CC and EFT payment methods
        billing.update().perform(tdBilling.getTestData("Update", "TestData_AddPaymentMetods"));
        
        //Navigate to Update Billing Account tab and verify that AutoPay Selection dropdown contains added CC-Visa and EFT payment methods
        billing.update().start();
        UpdateBillingAccountActionTab updateBillingAccountTab = new UpdateBillingAccountActionTab();         
        AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
        String paymentMethodVisa = AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Payment Method").getValue();
        String paymentMethodEFT = AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(2).getCell("Payment Method").getValue();
        Tab.buttonBack.click();
        
        updateBillingAccountTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(true);
        
        ComboBox autopaySelectionCombobox = updateBillingAccountTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION);

        assertThat(autopaySelectionCombobox).as("AutoPay Selection dropdown doesn't contain Credit/Debit Card payment method").containsOption(paymentMethodVisa);
        assertThat(autopaySelectionCombobox).as("AutoPay Selection dropdown doesn't contain Checking/Savings (ACH) payment method").containsOption(paymentMethodEFT);
        
        updateBillingAccountTab.cancel();
        
        //Navigate to Accept Payment tab and verify that Payment Method dropdown contains added CC-Visa and EFT payment methods
        billing.acceptPayment().start();
        AcceptPaymentActionTab acceptPaymentTab = new AcceptPaymentActionTab(); 
        
        ComboBox paymentMethodCombobox = acceptPaymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD);
        assertThat(paymentMethodCombobox).as("Payment Method dropdown doesn't contain Credit/Debit Card payment method").containsOption(paymentMethodVisa);
        assertThat(paymentMethodCombobox).as("Payment Method dropdown doesn't contain Checking/Savings (ACH) payment method").containsOption(paymentMethodEFT);
        
        //Add one more payment method (Credit Card - Master Card) from Accept Payment tab
        acceptPaymentTab.fillTab(tdBilling.getTestData("AcceptPayment", "TestData_AddPaymentMethod"));
        
        //Verify that Payment Method dropdown on Accept Payment tab contains 3 payment methods
        AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
        String paymentMethodMasterCard = AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(3).getCell("Payment Method").getValue();
        Tab.buttonBack.click();
        
        assertThat(paymentMethodCombobox).as("Payment Method dropdown doesn't contain Credit/Debit Card - Visa payment method").containsOption(paymentMethodVisa);
        assertThat(paymentMethodCombobox).as("Payment Method dropdown doesn't contain Checking/Savings (ACH) payment method").containsOption(paymentMethodEFT);
        assertThat(paymentMethodCombobox).as("Payment Method dropdown doesn't contain Credit/Debit Card - MasterCard payment method").containsOption(paymentMethodMasterCard);       
        acceptPaymentTab.cancel();
        
        //Verify that AutoPay Selection dropdown on Update Billing Account tab contains 3 payment methods
        billing.update().start();
        updateBillingAccountTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).setValue(true);
        
        assertThat(autopaySelectionCombobox).as("Payment Method dropdown doesn't contain Credit/Debit Card - Visa payment method").containsOption(paymentMethodVisa);
        assertThat(autopaySelectionCombobox).as("Payment Method dropdown doesn't contain Checking/Savings (ACH) payment method").containsOption(paymentMethodEFT);
        assertThat(autopaySelectionCombobox).as("Payment Method dropdown doesn't contain Credit/Debit Card - MasterCard payment method").containsOption(paymentMethodMasterCard);
        updateBillingAccountTab.cancel();
	}

}
