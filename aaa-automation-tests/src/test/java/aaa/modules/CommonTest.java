/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import aaa.main.modules.policy.PolicyType;

public class CommonTest extends BaseTest {
	private ThreadLocal<PolicyType> type;

	protected CommonTest() {
		final List<PolicyType> policyTypes = new ArrayList<>();
		policyTypes.add(PolicyType.HOME_CA_HO3);
		policyTypes.add(PolicyType.AUTO_SS);
		policyTypes.add(PolicyType.AUTO_CA_SELECT);
		//TODO-dchubkov: add all policy types to list

		type = ThreadLocal.withInitial(() -> policyTypes.get(new Random().nextInt(policyTypes.size())));
	}

	@Override
	protected PolicyType getPolicyType() {
		return type.get();
	}

}
