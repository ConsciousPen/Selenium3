/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelNoticeAddDelete;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Policy Cancel Notice Add And Remove
 * @scenario
 * 1. Create Customer
 * 2. Create Home_CA Policy
 * 3. Cancel Notice for AutoSS Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Delete Cancel Notice for Policy
 * 6. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */

public class TestPolicyCancelNoticeAddDelete extends PolicyCancelNoticeAddDelete {

	
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }
    
    @Override
    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testPolicyCancelNoticeAddDelete() {

        super.testPolicyCancelNoticeAddDelete();
    }
    
}
