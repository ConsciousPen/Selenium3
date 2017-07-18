/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import aaa.rest.productfactory.ProductFactoryRESTMethods;
import toolkit.rest.ResponseWrapper;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Issue Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Quote
 * 3. Issue Quote
 * 4. Verify policy status is 'Policy Active'
 * @details
 */
public class TestQuoteIssue extends HomeSSBaseTest {
    String policyNumber;

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteIssue() {
        mainApp().open();

        createQuote();
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Issue Quote #" + policyNumber);
        policy.purchase(tdPolicy.getTestData("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    @Test(priority = 2)
    @TestInfo(component = "Policy.PersonalLines")
    public void testRestGetAll() {
        ResponseWrapper response = new ProductFactoryRESTMethods().postProductOperation("PREC-HO", policyNumber);
        response.jsonVerifier().verify.status(200);
        response.jsonVerifier().verify.value("$..policyNumber", policyNumber);
    }
}
