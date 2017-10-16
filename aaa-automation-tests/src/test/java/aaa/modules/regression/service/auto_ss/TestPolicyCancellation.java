/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.template.PolicyCancellation;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyCancellation extends PolicyCancellation {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	
	@Override
	protected TestData getBackDatedPolicyTD() {
		return new AutoSSBaseTest().getBackDatedPolicyTD();
	}
	
	/**
	 * @author Jelena Dembovska
	 * @name Test Midterm Cancellation 
	 * @scenario
	 * 1. Create customer
	 * 2. Create backdated policy
	 * 3. Cancel policy with current date
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testPolicyCancellationMidTerm(@Optional("") String state) {
		super.testPolicyCancellationMidTerm();
	}
}
