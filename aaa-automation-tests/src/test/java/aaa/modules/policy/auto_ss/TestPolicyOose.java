/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;
import aaa.modules.policy.templates.PolicyOose;

import aaa.main.modules.policy.PolicyType;
import toolkit.utils.TestInfo;


public class TestPolicyOose extends PolicyOose {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testPolicyOose() {

		super.testPolicyOose();

	}
}
