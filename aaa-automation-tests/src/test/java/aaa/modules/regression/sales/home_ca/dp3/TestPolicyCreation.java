/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.dp3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Automation team
 * @name Test Create Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home CA DP3 policy
 * 3. Verify policy status is 'Policy Active'
 * @details
 */
public class TestPolicyCreation extends HomeCaDP3BaseTest {

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void testPolicyCreation(@Optional("CA") String state) {
        mainApp().open();

        createCustomerIndividual();
        createPolicy();

		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
