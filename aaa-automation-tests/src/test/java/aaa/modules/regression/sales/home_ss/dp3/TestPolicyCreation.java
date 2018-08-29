/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.dp3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Automation team
 * @name Test Create Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home DP3 policy
 * 3. Verify policy status is 'Active'
 * @details
 */
public class TestPolicyCreation extends HomeSSDP3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
	public void testPolicyCreation(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
	}
}
