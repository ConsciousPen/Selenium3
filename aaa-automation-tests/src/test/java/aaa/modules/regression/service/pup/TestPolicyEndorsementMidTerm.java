/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementMidTerm;
import toolkit.utils.TestInfo;


/**
 * @author Lina Li
 * <b> Test Midterm Endorsement for Umbrella Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create UPersonal Umbrella Policy
 * <p> 3. Create endorsement
 * <p> 4. Verify 'Pended Endorsement' button is disabled
 * <p> 5. Verify Policy status is 'Policy Active'
 * <p> 6. Verify Ending Premium is changed
 *
 */
public class TestPolicyEndorsementMidTerm extends PolicyEndorsementMidTerm {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
	public void testPolicyEndorsementMidTerm(@Optional("") String state) {
		testPolicyEndorsementMidTerm();
	}
}
