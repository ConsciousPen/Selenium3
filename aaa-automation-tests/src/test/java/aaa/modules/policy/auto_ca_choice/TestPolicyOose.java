/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_choice;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.PolicyOose;
import toolkit.utils.TestInfo;

public class TestPolicyOose extends PolicyOose {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Test
	@TestInfo(component = "Policy.AutoCA")
	public void testPolicyOose() {

		super.testPolicyOose();

	}
}
