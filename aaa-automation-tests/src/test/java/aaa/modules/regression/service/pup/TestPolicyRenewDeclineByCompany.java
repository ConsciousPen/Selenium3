/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Test;


import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyRenewDeclineByCompany;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test renew decline by company for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Personal Umbrella Policy 
 * 3. Renew Policy
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class TestPolicyRenewDeclineByCompany extends PolicyRenewDeclineByCompany {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }
    
    @Override
    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyRenewDeclineByCompany() {

        super.testPolicyRenewDeclineByCompany();
    }
}