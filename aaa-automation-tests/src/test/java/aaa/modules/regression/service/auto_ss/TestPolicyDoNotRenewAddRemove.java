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
import aaa.modules.regression.service.template.PolicyDoNotRenewAddRemove;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test Add and Remove 'Do Not Renew' for Auto Policy </b>
 * <p> Steps: 1. Create Customer
 * <p> 2. Create AutoSS Policy
 * <p> 3. Set Do Not Renew for Policy
 * <p> 4. Verify Policy status is 'Policy Active'
 * <p> 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * <p> 6. Remove Do Not Renew for Policy
 * <p> 7. Verify Policy status is 'Policy Active'
 * <p> 8. Verify 'Do Not Renew' flag isn't displayed in the policy overview header
 *
 */
public class TestPolicyDoNotRenewAddRemove extends PolicyDoNotRenewAddRemove {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void testPolicyDoNotRenewAddRemove(@Optional("") String state) {

		testPolicyDoNotRenewAddRemove();
	}
}
