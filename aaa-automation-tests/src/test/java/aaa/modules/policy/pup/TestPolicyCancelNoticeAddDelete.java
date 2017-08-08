/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.PolicyCancelNoticeAddDelete;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test Cancel and Delete Notice for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Cancel Notice for Umbrella Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Delete Cancel Notice for Policy
 * 6. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */
public class TestPolicyCancelNoticeAddDelete extends PolicyCancelNoticeAddDelete {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }
    
    @Override
    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyCancelNoticeAddDelete() {

        super.testPolicyCancelNoticeAddDelete();
    }
}
