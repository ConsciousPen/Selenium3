/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyRenewDeletePending;
import toolkit.utils.TestInfo;

/**
 * @author Xiaolan Ge
 * @name Test renew delete pended transaction for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto_CA_Select Policy
 * 3. Renew Policy
 * 4. Delete Pended Transaction
 * 5. Verify 'Renewals' button is disabled
 * @details
 */
public class TestPolicyRenewDeletePending extends PolicyRenewDeletePending {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyRenewDeletePending() {

		super.testPolicyRenewDeletePending();

	}
}
