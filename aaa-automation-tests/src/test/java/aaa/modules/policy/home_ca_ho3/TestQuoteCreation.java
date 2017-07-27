/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Ivan Kisly
 * @name Test Create Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home Quote
 * 3. Verify quote status is 'Premium Calculated'
 * @details
 */
public class TestQuoteCreation extends HomeCaHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testQuoteCreation() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
	}
}
