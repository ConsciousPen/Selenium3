/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.service.template.PolicyCancellation;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyCancellation extends PolicyCancellation {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Override
	protected TestData getBackDatedPolicyTD() {
		return new HomeSSHO3BaseTest().getBackDatedPolicyTD();
	}

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
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyCancellationFlat(@Optional("") String state) {
		super.testPolicyCancellationFlat();
	}	
	
	/**
	 * @author Ryan Yu
	 * @name Test Midterm Cancellation Home Policy
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Home (Preconfigured) Policy
	 * 3. Midterm Cancellation Policy
	 * 4. Verify Policy status is "Policy Cancelled"
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyCancellationMidTerm(@Optional("") String state) {
		super.testPolicyCancellationMidTerm();
	}
}
