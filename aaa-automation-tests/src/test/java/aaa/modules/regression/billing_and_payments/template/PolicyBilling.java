/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.billing_and_payments.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 * @author Jelena Dembovska
 * @name Test Billing functionality
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Auto Policy;
 * 3. Move to Billing tab
 * 4. Make 4 different payments(Accept Payment with Payments types: Cash, Check, Credit Card, EFT);
 * 6. Verify payments are displayed in Payments & Other Transactions section.
 * 7. Make Refund with Payment type Check
 * 8. Verify Refund is displayed in Payments & Other Transactions section.
 * @details
 */

public abstract class PolicyBilling extends PolicyBaseTest {
	
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData cash_payment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private TestData cc_payment = tdBilling.getTestData("AcceptPayment", "TestData_CC");
	private TestData eft_payment = tdBilling.getTestData("AcceptPayment", "TestData_EFT");
	//private TestData refund = tdBilling.getTestData("Refund", "TestData_Cash");
	private TestData refund = tdBilling.getTestData("Refund", "TestData_Check");  
    
    public void testBillingPayments() {
    	if(getUserGroup().equals(UserGroups.B31.get())) {
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
			assertThat(BillingSummaryPage.buttonAcceptPayment).as("Button 'Accept Payment' is enabled").isDisabled();
    	}
    	else {
    		 mainApp().open();
    		 getCopiedPolicy();
    		 assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    		 
    		 BillingSummaryPage.open();
    		 CustomSoftAssertions.assertSoftly(softly -> {
 			    IBillingAccount billing = new BillingAccount();

 			    //cash payment
 			    billing.acceptPayment().perform(cash_payment, new Dollar(200));
 			    checkPaymentIsGenerated(new Dollar(200), softly);

 			    //check payment
 			    billing.acceptPayment().perform(check_payment, new Dollar(250));
 			    checkPaymentIsGenerated(new Dollar(250), softly);

 			    //credit card payment
 			    billing.acceptPayment().perform(cc_payment, new Dollar(300));
 			    checkPaymentIsGenerated(new Dollar(300), softly);

 			    //EFT payment
 			    billing.acceptPayment().perform(eft_payment, new Dollar(350));
 			    checkPaymentIsGenerated(new Dollar(350), softly);
 		    });
    	}
    }
    
    public void testBillingRefund() {
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
			log.info("Verifying 'Refund' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Refund' is available").doesNotContainOption("Refund");			
		}
    	else {
    		mainApp().open();
            getCopiedPolicy();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

            BillingSummaryPage.open();
            IBillingAccount billing = new BillingAccount();
            billing.acceptPayment().perform(cash_payment, new Dollar(150));
            
            if(getUserGroup().equals(UserGroups.F35.get())||getUserGroup().equals(UserGroups.G36.get())) {
            	log.info("Verifying 'Refund' action");
    			assertThat(NavigationPage.comboBoxListAction).as("Action 'Refund' is available").doesNotContainOption("Refund");
    		}
            else {
            	CustomSoftAssertions.assertSoftly(softly -> {    		    
        		    //Refund
        		    Dollar refundAmount = new Dollar(150);
        		    billing.refund().perform(refund, refundAmount);

        		    new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
        				    .setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_REFUND)
        				    .setAmount(refundAmount).verifyPresent();
        	    });  
            }
    	}       
    }
    
    private void checkPaymentIsGenerated(Dollar amount, ETCSCoreSoftAssertions softly){
		new BillingPaymentsAndTransactionsVerifier(softly).setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setAmount(amount.negate()).verifyPresent();
    }
    
}
