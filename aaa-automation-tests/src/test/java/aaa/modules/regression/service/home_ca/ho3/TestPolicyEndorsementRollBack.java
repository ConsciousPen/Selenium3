/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementRollBack;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;


/**
 * @author Ryan Yu
 * <b> Test Roll Back Endorsement for Home Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Home Policy
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
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyEndormentRollBack(@Optional("CA") String state) {

		testPolicyEndorsementRollBack();

	}
}
