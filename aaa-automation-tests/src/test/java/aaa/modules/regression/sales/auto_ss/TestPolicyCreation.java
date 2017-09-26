/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Create Auto SS policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy
 * 3. Verify policy status is active
 * @details
 */
public class TestPolicyCreation extends AutoSSBaseTest {

    @Parameters({"state"})
	@Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyCreation(@Optional("") String state) {
    	
        mainApp().open();

        createCustomerIndividual(getCustomerIndividualTD("DataGather", "TestData_AZ").resolveLinks().adjust(getTestSpecificTD("TestData_AZ")));
        //createCustomerIndividual();
        createPolicy();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
    }
}
