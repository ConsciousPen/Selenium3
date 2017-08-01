/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.templates;


import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.verification.CustomAssert;

/**
 * @author Lina Li
 * @name Test Policy Reinstate with lapse
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Cancel the Policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * 5. Reinstate Policy with Lapse
 * 6. Verify Policy status is 'Policy Active'
 * 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 * @details
 */

public abstract class PolicyReinstatementWithLapse extends PolicyBaseTest {
	   
    public void testPolicyReinstatementWithLapse() {
    	
        mainApp().open();
        
      
       getCopiedPolicy();
                
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        CustomAssert.enableSoftMode();
        
        log.info("Cancelling Policy #" + policyNumber);
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        log.info("TEST: Reinstate Policy With Lapse #" + policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus14Days"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();
        
		CustomAssert.assertAll();

    }   
}
