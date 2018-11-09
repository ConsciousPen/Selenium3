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
	
	public void testUpdate_enableAutoPay(TestData td) {
		mainApp().open();
		//getCopiedPolicy();
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

}
