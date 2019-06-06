/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
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
	 * <b> Test Cancellation flat Umbrella Policy </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Umbrella  Policy
	 * <p> 3. Cancel policy
	 * <p> 4. Verify Policy status is 'Policy Cancelled'
	 *
	 */
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyCancellationFlat(@Optional("") String state) {
		testPolicyCancellationFlat();
	}
	
	/**
	 * @author Yongagng Sun
	 * <b> Test Midterm Cancellation Umbrella Policy </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Umbrella (AAA) Policy
	 * <p> 3. Midterm Cancellation Policy
	 * <p> 4. Verify Policy status is "Policy Cancelled"
	 *
	 */
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyCancellationMidTerm(@Optional("") String state) {
		testPolicyCancellationMidTerm();
	}
}
