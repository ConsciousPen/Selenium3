/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Create Umbrella Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Personal Umbrella Policy Quote
 * 3. Verify quote status is 'Premium Calculated'
 * @details
 */
public class TestQuoteCreation extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP.Quote Create")
    public void testQuoteCreation() {
        mainApp().open();

        createCustomerIndividual();
        
        TestData td = getPolicyTD("DataGather", "TestData");
        
        createQuote(td);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}
