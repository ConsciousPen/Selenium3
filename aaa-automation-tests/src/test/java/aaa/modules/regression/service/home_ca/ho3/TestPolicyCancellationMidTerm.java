/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @Author Ryan Yu
 * @name Test Midterm Cancellation Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home Policy
 * 3. Midterm Cancellation Policy
 * 4. Verify Policy status is "Policy Cancelled"
 * @details
 */
public class TestPolicyCancellationMidTerm extends HomeCaHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeCA.Cancellation")
	public void testPolicyCancellationMidTerm() {
		mainApp().open();

		createCustomerIndividual();
		
		createPolicy(getBackDatedPolicyTD());

		log.info("TEST: MidTerm Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}
}
