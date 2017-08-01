/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test Cancellation flat Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella  Policy
 * 3. Cancel policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * @details
 */
public class TestPolicyCancellationFlat extends PersonalUmbrellaBaseTest {

	@Test
	@TestInfo(component = "Policy.PUP.Cancellation")
	public void testPolicyCancellationFlat() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();
		log.info("TEST: Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}
}
