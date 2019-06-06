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
import aaa.modules.regression.service.template.PolicyRenewDeclineByCompany;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test renew decline by company for Auto Policy </b>
 * <p> Steps: 1. Create Customer
 * <p> 2. Create AutoCA Select Policy
 * <p> 3. Renew Policy
 * <p> 4. Decline by company
 * <p> 5. Verify Policy status is 'Company Declined'
 *
 */
public class TestPolicyRenewDeclineByCompany extends PolicyRenewDeclineByCompany {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyRenewDeclineByCompany(@Optional("CA") String state) {
		testPolicyRenewDeclineByCompany();
	}
}
