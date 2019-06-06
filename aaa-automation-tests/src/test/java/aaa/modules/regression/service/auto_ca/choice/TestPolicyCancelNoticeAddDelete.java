/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelNoticeAddDelete;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test Policy Cancel Notice Add And Remove </b>
 * <p> Steps: 1. Create Customer
 * <p> 2. Create Auto_CA_Choice Policy
 * <p> 3. Cancel Notice for AutoSS Policy
 * <p> 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * <p> 5. Delete Cancel Notice for Policy
 * <p> 6. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 *
 */

public class TestPolicyCancelNoticeAddDelete extends PolicyCancelNoticeAddDelete {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE)
	public void testPolicyCancelNoticeAddDelete(@Optional("CA") String state) {

		testPolicyCancelNoticeAddDelete();
	}

}
