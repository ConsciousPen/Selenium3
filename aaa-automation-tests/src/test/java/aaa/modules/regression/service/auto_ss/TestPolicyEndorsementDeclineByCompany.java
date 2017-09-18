/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementDeclineByCompany;

/**
 * @author Yonggang Sun
 * @name Test endorsement decline by company for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (AAA) Policy
 * 3. Create endorsement
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined' in endorsement
 * @details
 */
public class TestPolicyEndorsementDeclineByCompany extends PolicyEndorsementDeclineByCompany {

	@Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testPolicyEndorsementDeclineByCompany(String state) {
		super.testPolicyEndorsementDeclineByCompany();
    }
}
