package aaa.modules.regression.billing_and_payments.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.Tab;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class PolicyBillingUpdate extends PolicyBaseTest {
	
	/**
	 * @author oreva
	 * @param td
	 * @name Test Update Billing Account - Activate AutoPay option
	 * @scenario 
	 * 1. Create a new Customer. 
	 * 2. Create a new policy with added Payment Method - Credit Card but disabled AutoPay on Purchase tab. 
	 * 3. Navigate to Billing tab. 
	 * 4. Select 'Update' option in 'Take Action' dropdown. 
	 * 5. On Update Billing Account tab activate AutoPay option and select payment method 'Credit Card'.
	 */
	public void testUpdate_enableAutoPay(TestData td) {
		mainApp().open();
		createCustomerIndividual(); 
		createPolicy(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;
        
        //Activate AutoPay option
        billing.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));
        
        //verify that AutoPay is enabled on Update Billing Account tab
        billing.update().start();
        assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(true);
        Tab.buttonCancel.click();
	}
	
	/**
	 * @author oreva
	 * @name Test Update Billing Account - add payment method and activate AutoPay 
	 * @scenario
	 * 1. Create new or open existent Customer. 
	 * 2. Create a new policy. 
	 * 3. Navigate to Billing tab. 
	 * 4. Select 'Update' option in 'Take Action' dropdown.
	 * 5. Add payment method for AutoPay on Update Billing Account tab. 
	 * 6. Activate AutoPay option and select added Payment method on Update Billing Account tab.
	 */
	public void testUpdate_addPaymentMethodAndEnableAutoPay() {
		mainApp().open();
		getCopiedPolicy();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
		BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;
        
        //Navigate to Update Billing Account tab and add Credit Card payment method
        billing.update().perform(tdBilling.getTestData("Update", "TestData_AddAutopay"));
        
        //verify that AutoPay is enabled on Update Billing Account tab
        billing.update().start();
        assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(true);
        Tab.buttonCancel.click();
		
	}
	
	/**
	 * @author oreva
	 * @param td
	 * @name Test Update Billing Account - disable AutoPay option
	 * @scenario
	 * 1. Create a new Customer. 
	 * 2. Create a new policy with added Payment Method - Credit Card and enabled AutoPay on Purchase tab. 
	 * 3. Navigate to Billing tab. 
	 * 4. Select 'Update' option in 'Take Action' dropdown.
	 * 5. Disable AutoPay option on Update Billing Account tab. 
	 */
	public void testUpdate_disableAutoPay(TestData td) {
		mainApp().open();
		createCustomerIndividual(); 
		createPolicy(td);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        BillingSummaryPage.open();
        IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;
        
        //Disable AutoPay option
        billing.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
        
        //verify that AutoPay is disabled on Update Billing Account tab
		billing.update().start();
		assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(false);
		Tab.buttonCancel.click();
	}

}
