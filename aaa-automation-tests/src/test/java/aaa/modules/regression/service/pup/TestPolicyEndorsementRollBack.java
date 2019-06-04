/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementRollBack;
import toolkit.utils.TestInfo;

/**
 * @author
 * <b> Test Roll Back Endorsement for Umbrella Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Umbrella (Preconfigured) Policy
 * <p> 3. Create Midterm Endorsement
 * <p> 4. Verify 'Pended Endorsement' button is disabled
 * <p> 5. Verify Policy status is 'Policy Active'
 * <p> 6. Verify Ending Premium is changed
 * <p> 7. Roll Back Endorsement
 * <p> 8. Verify Ending Premium was roll back
 *
 */
public class TestPolicyEndorsementRollBack extends PolicyEndorsementRollBack {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyEndorsementRollBack(@Optional("") String state) {

		testPolicyEndorsementRollBack();

	}
}