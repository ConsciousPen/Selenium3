/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import aaa.common.Constants;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO3BaseTest extends PolicyBaseTest {

	public HomeCaHO3BaseTest() {
		setState(Constants.States.CA.get());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

}
