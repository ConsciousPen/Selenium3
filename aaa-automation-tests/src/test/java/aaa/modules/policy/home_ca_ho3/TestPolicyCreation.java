/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Create CA Home Policy
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate CAH quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 4. Purchase policy.
 * 5. Verify policy status is Active on Consolidated policy view.
 * @details
 */
public class TestPolicyCreation extends HomeCaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCreation() {
        mainApp().open();

        createCustomerIndividual();
        policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
