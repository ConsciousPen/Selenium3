/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestRatingServicesAbstract;
import toolkit.utils.TestInfo;

public class TestRatingServices extends TestRatingServicesAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_DP3;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Retrieve Mmebership Discount % from rating algo
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10302"})
	public void pas10302_discountPercentageRetrieve(@Optional("VA") String state) {

		pas10302_discountPercentageRetrieveBody("DP3", "CA", "CovA", "1.0");
	}

}
