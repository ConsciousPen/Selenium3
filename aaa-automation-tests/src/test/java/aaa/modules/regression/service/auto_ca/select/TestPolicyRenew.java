/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
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

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyRenew(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policy.createRenewal(getTestSpecificTD("TestData"));

		PolicySummaryPage.buttonRenewals.verify.enabled();

		NotesAndAlertsSummaryPage.alert.verify.contains("This Policy is Pending Renewal");

	}
}
