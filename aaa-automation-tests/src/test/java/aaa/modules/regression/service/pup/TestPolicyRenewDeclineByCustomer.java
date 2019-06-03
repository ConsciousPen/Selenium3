/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyRenewDeclineByCustomer;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test renew decline by customer for Umbrella Policy </b>
 * <p> Steps: 1. Create Customer
 * <p> 2. Create Personal Umbrella Policy
 * <p> 3. Renew Policy
 * <p> 4. Decline by customer
 * <p> 5. Verify Policy status is 'Customer Declined'
 *
 */
public class TestPolicyRenewDeclineByCustomer extends PolicyRenewDeclineByCustomer {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void testPolicyRenewDeclineByCustomer(@Optional("") String state) {

		testPolicyRenewDeclineByCustomer();
	}

}
