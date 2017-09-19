/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.service.template.PolicyEndorsementMidTerm;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;




/**
 * @author Xiaolan Ge
 * @name Test Midterm Endorsement for Auto Policy
 * @scenario
 * 1. Create customer
 * 2. Create backdated policy
 * 3. Endorse policy with current date
 * @details
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyEndorsementMidTerm(@Optional("CA") String state) {
		super.testPolicyEndorsementMidTerm();
	}
}
