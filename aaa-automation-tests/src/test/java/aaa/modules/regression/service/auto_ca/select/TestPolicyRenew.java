/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Create CA Select Auto Policy renewal
 * @scenario
 * @details
 */
public class TestPolicyRenew extends AutoCaSelectBaseTest {

	@Test
	@TestInfo(component = "Policy.AutoCA")
	public void testQuoteCreation() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.createRenewal(getPolicyTD(this.getClass().getSimpleName(), "TestData"));

		PolicySummaryPage.buttonRenewals.verify.enabled();

		NotesAndAlertsSummaryPage.alert.verify.contains("This Policy is Pending Renewal");

	}
}
