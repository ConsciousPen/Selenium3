/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;


import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.impl.SimpleDataProvider;


/**
 * @author Xiaolan Ge
 * @name Test delete pending renewals
 * @scenario
 * 1. Create Customer
 * 2. Create Policy 
 * 3. Renew Policy
 * 4. Delete Pended Transaction
 * 5. Verify 'Renewals' button is disabled
 * @details
 */
public abstract class PolicyRenewDeletePending extends PolicyBaseTest {


    public void testPolicyRenewDeletePending() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Delete Pending Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        if (getPolicyType().equals(PolicyType.AUTO_SS) || getPolicyType().equals(PolicyType.AUTO_CA_SELECT) || getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)){
	    	 policy.renew().perform(new SimpleDataProvider());
	     }	     
	     else {
	    	  policy.renew().performAndExit(new SimpleDataProvider());
	     }
        
        PolicySummaryPage.buttonRenewals.click();

        policy.deletePendingRenwals().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonRenewals.verify.enabled(false);
    }
}
