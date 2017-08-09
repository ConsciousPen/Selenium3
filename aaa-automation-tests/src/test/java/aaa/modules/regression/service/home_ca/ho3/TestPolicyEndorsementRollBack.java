/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementRollBack;
import toolkit.utils.TestInfo;


/**
 * @author Ryan Yu
 * @name Test Roll Back Endorsement for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home Policy
 * 3. Create Midterm Endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * 7. Roll Back Endorsement
 * 8. Verify Ending Premium was roll back
 * @details
 */
public class TestPolicyEndorsementRollBack extends PolicyEndorsementRollBack {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Test
	@TestInfo(component = "Policy.HomeCA")
	public void testPolicyEndormentRollBack() {

		super.testPolicyEndorsementRollBack();

	}
}
