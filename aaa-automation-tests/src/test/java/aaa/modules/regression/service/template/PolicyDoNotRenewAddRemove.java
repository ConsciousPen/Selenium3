/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;


import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.verification.CustomAssert;

/**
 * @author Lina Li
 * @name Test Add and Remove 'Do Not Renew' for Policy
 * @scenario
 * 1. Create Customer
 * 2. Create a Policy
 * 3. Set Do Not Renew for Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Remove Do Not Renew for Policy
 * 7. Verify Policy status is 'Policy Active'
 * 8. Verify 'Do Not Renew' flag isn't displayed in the policy overview header
 * @details
 */

public abstract class PolicyDoNotRenewAddRemove extends PolicyBaseTest {
	   
    public void testPolicyDoNotRenewAddRemove() {
    	
        mainApp().open();
        
      
        getCopiedPolicy();
        
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        CustomAssert.enableSoftMode();
        
        log.info("TEST: Add Do Not Renew for Policy #" + policyNumber);
        policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
        
        PolicySummaryPage.labelDoNotRenew.verify.present();
        
        log.info("TEST: Remove Do Not Rene for Policy #" + policyNumber);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		PolicySummaryPage.labelDoNotRenew.verify.present(false);
        
		CustomAssert.assertAll();

    }   
}
