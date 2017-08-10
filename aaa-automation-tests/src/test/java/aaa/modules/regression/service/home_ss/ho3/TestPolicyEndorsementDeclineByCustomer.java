/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementDeclineByCustomer;

/**
 * @author Yonggang Sun
 * @name Test Endorsement decline by customer for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined' in endorsement
 * @details
 */
public class TestPolicyEndorsementDeclineByCustomer extends PolicyEndorsementDeclineByCustomer {
	
	@Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyEndorsementDeclineByCustomer() {
		super.testPolicyEndorsementDeclineByCustomer();
	}
}
