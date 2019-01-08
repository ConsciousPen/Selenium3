package aaa.modules.regression.billing_and_payments.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.AddHoldActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

/** 
 * @author oreva
 * @name Test Billing - 'Hold Policies' action
 * @scenario
 * 1. Create a new customer or retrieve existed. 
 * 2. Create a new policy.
 * 3. Navigate to Billing tab. 
 * 4. Verify billing status is 'Active' in Billing Account Policies section.
 * 5. Select 'Hold Policices' in Take Action drop down. 
 * 6. Add billing hold for currect policy. 
 * 7. Verify that status of billing changed to 'On Hold' in Billing Account Policies section.
 * 8. Select 'Hold Policies' in Take Action drop down and remove hold added in previous steps.
 * 9. Verify that status of billing changed to 'Active' in Billing Account Policies section. 
 */
public abstract class PolicyBillingHoldPolicies extends PolicyBaseTest {
	
	public void testHoldPolicies() {
		if (getUserGroup().equals(UserGroups.B31.get())) {
			mainApp().open(getLoginTD(UserGroups.QA));
			createCustomerIndividual();
			createPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			mainApp().close();
			
			//re-login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openBilling(policyNumber);
			verifyNoButtonAddUpdateHold();
		}
		else {
			mainApp().open();
			getCopiedPolicy();
	        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	        BillingSummaryPage.open();
	        if (getUserGroup().equals(UserGroups.E34.get()) || getUserGroup().equals(UserGroups.F35.get()) 
	        		|| getUserGroup().equals(UserGroups.G36.get())) {
	        	verifyNoButtonAddUpdateHold();
	        }
	        else {
	        	addHoldAndRemoveHold();
	        }
		}
	}
	
	public void addHoldAndRemoveHold() {
		IBillingAccount billing = new BillingAccount();
        TestData tdBilling = testDataManager.billingAccount;        
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(
        		BillingConstants.BillingAccountPoliciesTable.BILLING_STATUS).getValue()).isEqualTo(BillingConstants.BillingAccountPoliciesBillingStatus.ACTIVE);
        
        //Perform 'Hold Policies' action to add hold and verify billing status changes to 'On Hold'
        billing.addHold().perform(tdBilling.getTestData("AddHold", "TestData"));        
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(
        		BillingConstants.BillingAccountPoliciesTable.BILLING_STATUS).getValue()).isEqualTo(BillingConstants.BillingAccountPoliciesBillingStatus.HOLD);
        
        //Remove hold from billing account and verify billing status changed to 'Active'
        billing.removeHold().perform(tdBilling.getTestData("RemoveHold", "TestData"));        
        assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(
        		BillingConstants.BillingAccountPoliciesTable.BILLING_STATUS).getValue()).isEqualTo(BillingConstants.BillingAccountPoliciesBillingStatus.ACTIVE);
	}
	
	public void verifyNoButtonAddUpdateHold() {
		log.info("Verifying 'Add/Update' button absent on Add Hold Tab");
		IBillingAccount billing = new BillingAccount();
		billing.addHold().start();
		assertThat(AddHoldActionTab.buttonAddUpdate).as("Button 'Add/Update' is present on Add Hold Tab").isAbsent();
	}
}
