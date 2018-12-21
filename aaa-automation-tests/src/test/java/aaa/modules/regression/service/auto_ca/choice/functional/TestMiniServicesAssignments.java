/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesAssignmentsHelper;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesAssignments extends TestMiniServicesAssignmentsHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}


	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = {"PAS-11684"})
	public void pas11684_DriverAssignmentExistsForState(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas11684_DriverAssignmentExistsForStateBody(state, softly)
		);
	}
}
