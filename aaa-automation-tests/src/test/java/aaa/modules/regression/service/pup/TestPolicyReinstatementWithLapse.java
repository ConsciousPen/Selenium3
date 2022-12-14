/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyReinstatementWithLapse;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test Reinstatement Umbrella Policy with Lapse </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create  Personal Umbrella Policy Policy
 * <p> 3. Cancel the Policy
 * <p> 4. Verify Policy status is 'Policy Cancelled'
 * <p> 5. Reinstate Policy with Lapse
 * <p> 6. Verify Policy status is 'Policy Active'
 * <p> 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 *
 */
public class TestPolicyReinstatementWithLapse extends PolicyReinstatementWithLapse {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void testPolicyReinstatementWithLapse(@Optional("") String state) {

		testPolicyReinstatementWithLapse();
	}
}
