/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementDeletePending;
import toolkit.utils.TestInfo;

/**
 * @author Xiaolan Ge
 * @name Test Delete Pending Endorsement for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto_CA_Select Policy
 * 3. Create endorsement
 * 4. Delete Pended Transaction for Policy
 * 5. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public class TestPolicyEndorsementDeletePending extends PolicyEndorsementDeletePending {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyEndorsementDeletePending(@Optional("CA") String state) {

		super.testPolicyEndorsementDeletePending();

	}
}
