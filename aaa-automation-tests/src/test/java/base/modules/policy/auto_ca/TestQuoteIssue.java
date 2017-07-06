/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Issue Auto Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Auto Quote
 * 3. Issue Quote
 * 4. Verify policy status is 'Policy Active'
 * @details
 */
public class TestQuoteIssue extends AutoCaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteIssue() {
        mainApp().open();

        createCustomerIndividual();

        createQuote();

        log.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.purchase(tdPolicy.getTestData("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
