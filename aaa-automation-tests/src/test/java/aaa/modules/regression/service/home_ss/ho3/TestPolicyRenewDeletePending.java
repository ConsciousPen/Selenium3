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
import aaa.modules.regression.service.template.PolicyRenewDeletePending;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * @name Test renew delete pended transaction for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home_SS_HO3 Policy
 * 3. Renew Policy
 * 4. Delete Pended Transaction
 * 5. Verify 'Renewals' button is disabled
 * @details
 */
public class TestPolicyRenewDeletePending extends PolicyRenewDeletePending {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyRenewDeletePending(@Optional("") String state) {

		super.testPolicyRenewDeletePending();

	}
}
