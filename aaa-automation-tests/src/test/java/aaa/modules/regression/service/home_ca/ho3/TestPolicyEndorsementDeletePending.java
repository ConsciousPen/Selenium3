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
import aaa.modules.regression.service.template.PolicyEndorsementDeletePending;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Xiaolan Ge
 * <b> Test Delete Pending Endorsement for Home Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Home (Preconfigured) Policy
 * <p> 3. Create endorsement
 * <p> 4. Delete Pended Transaction for Home Policy
 * <p> 5. Verify 'Pended Endorsement' button is disabled
 *
 */
public class TestPolicyEndorsementDeletePending extends PolicyEndorsementDeletePending {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyEndorsementDeletePending(@Optional("CA") String state) {

		testPolicyEndorsementDeletePending();

	}
}
