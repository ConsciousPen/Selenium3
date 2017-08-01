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
 * @name Test Issue Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home Quote
 * 3. Issue Quote
 * 4. Verify policy status is 'Policy Active'
 * @details
 */
public class TestQuoteIssue extends HomeCaHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testQuoteIssue() {
		mainApp().open();

		createCustomerIndividual();
		createQuote();

		log.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.purchase(getPolicyTD("DataGather", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
}
