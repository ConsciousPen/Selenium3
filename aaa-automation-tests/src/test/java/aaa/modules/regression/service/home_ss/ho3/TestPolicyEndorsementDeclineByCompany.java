/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementDeclineByCompany;

/**
 * @author Yonggang Sun
 * @name Test Endorsement decline by company for home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home SS(AAA) Policy
 * 3. Create endorsement
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class TestPolicyEndorsementDeclineByCompany extends PolicyEndorsementDeclineByCompany {
	
	@Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyEndorsementDeclineByCompany(@Optional("") String state) {
		super.testPolicyEndorsementDeclineByCompany();
	}
}
