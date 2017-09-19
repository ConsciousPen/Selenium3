/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelNoticeWithCancellation;

/**
 * @author Ryan Yu
 * @name Test Cancellation Notice with Cancellation for PUP
 * @scenario
 * 1. Create Customer
 * 2. Create Policy
 * 3. Cancel Notice for Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Shift Time to Days Of Notice + Cancellation Date
 * 6. Run 'aaaCancellationConfirmationAsyncJob'
 * 7. Verify Policy status is 'Policy Cancelled'
 * 8. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */
public class TestPolicyCancelNoticeWithCancellation extends PolicyCancelNoticeWithCancellation {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, enabled = false)
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void TC01_CreatePolicyAndCancelNotice(@Optional("") String state) {
		super.TC01_CreatePolicyAndCancelNotice();
	}
	
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_CreatePolicyAndCancelNotice",groups = {Groups.REGRESSION, Groups.CRITICAL}, enabled = false)
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void TC02_CancellationPolicy(@Optional("") String state) {
		super.TC02_CancellationPolicy();
	}
}
