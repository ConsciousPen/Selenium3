/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.templates;


import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;

/**
 * @author Lina Li
 * @name Test Policy Cancel Notice Add and Delete
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Cancel Notice for Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Delete Cancel Notice for Policy
 * 6. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */

public abstract class PolicyCancelNoticeAddDelete extends PolicyBaseTest {
	   
    public void testPolicyCancelNoticeAddDelete() {
    	
        mainApp().open();
        
      
       getCopiedPolicy();
        
//        createCustomerIndividual();
//        
//        createPolicy();
        
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        CustomAssert.enableSoftMode();
        
        log.info("TEST: Cancel Notice for Policy #" + policyNumber);
        policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
        PolicySummaryPage.labelCancelNotice.verify.present();
        
        log.info("TEST: Delete Cancel Notice for Policy #" + policyNumber);
		policy.deleteCancelNotice().perform(new SimpleDataProvider());
		PolicySummaryPage.labelCancelNotice.verify.present(false);
        
		CustomAssert.assertAll();

    }   
}
