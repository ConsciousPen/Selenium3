/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import java.util.Map;

import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import toolkit.datax.TestData;

public class PersonalUmbrellaBaseTest extends PolicyBaseTest {

	public PersonalUmbrellaBaseTest() {
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	protected TestData adjustWithRealPolicies(TestData td, Map<String, String> policies) {
		PrefillTab prefillTab = new PrefillTab();
		return prefillTab.adjustWithRealPolicies(td, policies);
	}
}
