/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Xiaolan Ge
 * @name Test Issue Umbrella Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella Quote
 * 3. Issue Quote
 * 4. Verify policy status is 'Policy Active'
 * @details
 */
public class TestQuoteIssue extends PersonalUmbrellaBaseTest {
    String policyNumber;

    @Test(priority = 1)
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteIssue() {
        mainApp().open();

        createCustomerIndividual();

        createQuote();
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Issue Quote #" + policyNumber);
        policy.purchase(getPolicyTD("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
