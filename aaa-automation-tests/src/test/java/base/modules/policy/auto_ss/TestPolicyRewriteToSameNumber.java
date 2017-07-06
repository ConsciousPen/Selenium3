/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Rewrite to same number for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (Preconfigured) Policy
 * 3. Cancel Policy
 * 4. Rewrite Policy
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Policy number is the same cancelled policy number
 * @details
 */
public class TestPolicyRewriteToSameNumber extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRewriteToSameNumber() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));

        log.info("Cancelled Policy #" + policyNumber);

        log.info("TEST: Rewrite Policy #" + policyNumber);
        policy.rewrite().perform(tdPolicy.getTestData("Rewrite", "TestDataSameNumber"));
        policy.calculatePremiumAndPurchase(tdPolicy.getTestData("DataGather", "TestData"));

        String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Rewritten Policy #" + rewrittenPolicyNumber);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelPolicyNumber.verify.value(policyNumber);
    }
}
