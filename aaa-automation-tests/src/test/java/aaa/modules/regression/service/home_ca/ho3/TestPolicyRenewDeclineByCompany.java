/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyRenewDeclineByCompany;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test renew decline by company for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create HomeCA Policy
 * 3. Renew Policy
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class TestPolicyRenewDeclineByCompany extends PolicyRenewDeclineByCompany {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyRenewDeclineByCompany(String state) {

		super.testPolicyRenewDeclineByCompany();
	}
}
