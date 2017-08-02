/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.PolicyCancellationFlat;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * @name Test Cancellation flat Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home SS Policy
 * 3. Cancel policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * @details
 */
public class TestPolicyCancellationFlat extends PolicyCancellationFlat {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	@TestInfo(component = "Policy.HomeSS.Cancellation")
	public void testPolicyCancellationFlat() {
		super.testPolicyCancellationFlat();
	}
}
