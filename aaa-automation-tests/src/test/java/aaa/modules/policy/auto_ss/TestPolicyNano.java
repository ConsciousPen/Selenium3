/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Named-non-owner auto policy creation
 * @scenario
 * @details
 */
public class TestPolicyNano extends AutoSSBaseTest {

	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testPolicyNano() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Nano Policy Creation Started...");

		TestData td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
		getPolicyType().get().createPolicy(td);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}

}
