/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.templates;

import java.util.HashMap;
import java.util.Map;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

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
	private TestData refund = tdBilling.getTestData("Refund", "TestData_Cash");
	
    
    
   // @TestInfo(component = "Policy.PersonalLines")
    public void testBilling() {
    	
        mainApp().open();

        getCopiedPolicy();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        
        BillingSummaryPage.open();
        
        CustomAssert.enableSoftMode();
        
        //cash payment
        new BillingAccount().acceptPayment().perform(cash_payment, "200");      
        checkPaymentIsGenerated("200.00");
			
        //check payment
        new BillingAccount().acceptPayment().perform(check_payment, "250");        
        checkPaymentIsGenerated("250.00");
			
		//credit card payment
		new BillingAccount().acceptPayment().perform(cc_payment, "300"); 		
		checkPaymentIsGenerated("300.00");
		 
		//EFT payment
        new BillingAccount().acceptPayment().perform(eft_payment, "350"); 
        checkPaymentIsGenerated("350.00");
        
        //Refund
        new BillingAccount().refund().perform(refund, "150"); 
        
		Map<String, String> query = new HashMap<>();
		query.put("Type", "Refund");
		query.put("Subtype/Reason", "Manual Refund");
		query.put("Amount", "$150.00");
		
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).verify.present();
		
		CustomAssert.assertAll();
    }
    
    private void checkPaymentIsGenerated(String amount){
    	
		Map<String, String> query = new HashMap<>();
		query.put("Type", "Payment");
		query.put("Subtype/Reason", "Manual Payment");
		query.put("Amount", "($" + amount + ")");
			
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(query).verify.present();
	
    }
    
}
