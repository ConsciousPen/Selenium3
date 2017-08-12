/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.service.template.PolicyRenewFlatCancellation;

/**
 * @author Xiaolan Ge
 * @name Test renew flat cancellation for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Manual Renew for Policy
 * 4. Cancellation Policy Renewal
 * 5. Verify Policy status is 'Cancellation Pending'
 * @details
 */
public class TestPolicyRenewFlatCancellation extends PolicyRenewFlatCancellation {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	@Override
	protected TestData getBackDatedPolicyTD() {
		return new HomeSSHO3BaseTest().getBackDatedPolicyTD();
	}
	
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3 )
    public void testPolicyRenewFlatCancellation() {
        super.testPolicyRenewFlatCancellation();
    }
}