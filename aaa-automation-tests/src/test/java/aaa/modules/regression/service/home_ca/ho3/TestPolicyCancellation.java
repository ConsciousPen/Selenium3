/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.modules.regression.service.template.PolicyCancellation;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyCancellation extends PolicyCancellation {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	@Override
	protected TestData getBackDatedPolicyTD() {
		return new HomeCaHO3BaseTest().getBackDatedPolicyTD();
	}

	/**
	 * @author Ryan Yu
	 * <b> Test Cancellation flat Home Policy </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Home CA Policy
	 * <p> 3. Cancel policy
	 * <p> 4. Verify Policy status is 'Policy Cancelled'
	 *
	 */
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyCancellationFlat(@Optional("CA") String state) {
		testPolicyCancellationFlat();
	}
	
	/**
	 * @author Ryan Yu
	 * <b> Test Midterm Cancellation Home Policy </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Home Policy
	 * <p> 3. Midterm Cancellation Policy
	 * <p> 4. Verify Policy status is "Policy Cancelled"
	 *
	 */
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyCancellationMidTerm(@Optional("CA") String state) {
		testPolicyCancellationMidTerm();
	}
}
