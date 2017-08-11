/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho6;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Create Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Quote
 * 3. Verify quote status is 'Premium Calculated'
 * @details
 */
public class TestPolicyCreation extends HomeSSHO6BaseTest {

    @Test(groups= {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6)
    public void testPolicyCreation() {
        mainApp().open();
        createCustomerIndividual();
        createPolicy();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: HSS06 Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
    }
}
