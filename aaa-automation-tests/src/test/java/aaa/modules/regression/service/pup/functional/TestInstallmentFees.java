/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestInstallmentFeesAbstract;
import toolkit.utils.TestInfo;

public class TestInstallmentFees extends TestInstallmentFeesAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check installment fees service returns correct values for Product/State
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP, testCaseId = {"PAS-240", "PAS-242"})
	public void pas240_installmentFeesService(@Optional("VA") String state) {
		if("CA".equals(state)) {
			pas240_installmentFeesServiceBody("AAA_PUP_SS", state, "3", "3", "7", "3");
		}else{
			pas240_installmentFeesServiceBody("AAA_PUP_SS", state, "2", "2", "5", "2");
		}
	}
}
