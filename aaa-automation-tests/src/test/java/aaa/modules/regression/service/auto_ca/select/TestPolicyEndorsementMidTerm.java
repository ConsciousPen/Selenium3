/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.service.template.PolicyEndorsementMidTerm;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;




/**
 * @author Xiaolan Ge
 * <b> Test Midterm Endorsement for Auto Policy </b>
 * <p> Steps:
 * <p> 1. Create customer
 * <p> 2. Create backdated policy
 * <p> 3. Endorse policy with current date
 *
 */
public class TestPolicyEndorsementMidTerm extends PolicyEndorsementMidTerm {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Override
	protected TestData getBackDatedPolicyTD() {
		return new AutoCaSelectBaseTest().getBackDatedPolicyTD();
	}
	
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyEndorsementMidTerm(@Optional("CA") String state) {
		testPolicyEndorsementMidTerm();
	}
}
