/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyEndorsementDeclineByCustomer;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * <b> Test Endorsement decline by customer for Auto Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Auto (AAA) Policy
 * <p> 3. Create endorsement
 * <p> 4. Decline by customer
 * <p> 5. Verify Policy status is 'Customer Declined' in endorsement
 *
 */
public class TestPolicyEndorsementDeclineByCustomer extends PolicyEndorsementDeclineByCustomer {

	
	@Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testPolicyEndorsementDeclineByCustomer(@Optional("") String state) {
		testPolicyEndorsementDeclineByCustomer();
    }
}
