/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Endorsement decline by customer for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined' in endorsement
 * @details
 */
public class TestPolicyEndorsementDeclineByCustomer extends HomeSSBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyEndorsementDeclineByCustomer() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		log.info("TEST: Decline By Customer Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.endorse().performAndExit(tdPolicy.getTestData("Endorsement", "TestData"));
		PolicySummaryPage.buttonPendedEndorsement.click();

		policy.declineByCustomerQuote().perform(tdPolicy.getTestData("DeclineByCustomer", "TestData"));
		PolicySummaryPage.buttonPendedEndorsement.click();

		PolicySummaryPage.tableEndorsements.getRow(1).getCell(3).verify.value(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
	}
}
