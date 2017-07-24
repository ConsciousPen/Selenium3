/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Rewrite to new number for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto Policy
 * 3. Cancel Policy
 * 4. Rewrite Policy
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Policy number different the cancelled policy number
 * @details
 */
public class TestPolicyRewriteToNewNumber extends AutoCaSelectBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRewriteToNewNumber() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));

        log.info("Cancelled Policy #" + policyNumber);

        log.info("TEST: Rewrite Policy #" + policyNumber);
        policy.rewrite().perform(tdPolicy.getTestData("Rewrite", "TestDataNewNumber"));
        policy.calculatePremiumAndPurchase(tdPolicy.getTestData("DataGather", "TestData"));

        String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Rewritten Policy #" + rewrittenPolicyNumber);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        CustomAssert.assertFalse(String.format("Rewritten Policy Number #%s is the same as Initial Policy Number #%s",
                policyNumber, rewrittenPolicyNumber), policyNumber.equals(rewrittenPolicyNumber));
    }
}
