/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;


import org.testng.annotations.Test;

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
public class AAATestPolicyCreation extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCreation() {
        mainApp().open();

        //createCustomerIndividual();

        //policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData"));
        //createPolicy(tdPolicy.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), LICENSE_NUMBER.getLabel()), "A12345678"));
        
        //TestData stateTestData = getStateTestData(tdPolicy, "DataGather", "TestData"); 
        //createPolicy(stateTestData.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), LICENSE_NUMBER.getLabel()), licenseNumber));
        
        //policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData").adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), LICENSE_NUMBER.getLabel()), licenseNumber));
        
        createPolicy();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
    }
}
