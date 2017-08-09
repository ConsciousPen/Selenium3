/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Test;


import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyRenewDeclineByCustomer;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test renew decline by customer for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create AutoCA Choice Policy
 * 3. Renew Policy
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined'
 * @details
 */
public class TestPolicyRenewDeclineByCustomer extends PolicyRenewDeclineByCustomer {

	 @Override
	    protected PolicyType getPolicyType() {
	        return PolicyType.AUTO_CA_CHOICE;
	    }
	    
	    @Override
	    @Test
	    @TestInfo(component = "Policy.AutoCA")
	    public void testPolicyRenewDeclineByCustomer() {

	        super.testPolicyRenewDeclineByCustomer();
	    }
}