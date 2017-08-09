/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.PolicyRewriteToNewNumber;

/**
 * @author Yonggang Sun
 * @name Test Rewrite to new number for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (AAA) Policy
 * 3. Cancel Policy
 * 4. Rewrite Policy
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Policy number different the cancelled policy number
 * @details
 */
public class TestPolicyRewriteToNewNumber extends PolicyRewriteToNewNumber {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyRewriteToNewNumber() {
        super.testPolicyRewriteToNewNumber();
    }
}
