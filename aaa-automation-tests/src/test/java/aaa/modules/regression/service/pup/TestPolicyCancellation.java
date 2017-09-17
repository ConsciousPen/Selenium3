/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancellation;
import toolkit.utils.TestInfo;


public class TestPolicyCancellation extends PolicyCancellation {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	/**
	 * @author Yonggang Sun
	 * @name Test Cancellation flat Umbrella Policy
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Umbrella  Policy
	 * 3. Cancel policy
	 * 4. Verify Policy status is 'Policy Cancelled'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyCancellationFlat(String state) {
		super.testPolicyCancellationFlat();
	}
	
	/**
	 * @author Yongagng Sun
	 * @name Test Midterm Cancellation Umbrella Policy
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Umbrella (AAA) Policy
	 * 3. Midterm Cancellation Policy
	 * 4. Verify Policy status is "Policy Cancelled"
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyCancellationMidTerm(String state) {
		super.testPolicyCancellationMidTerm();
	}
}
