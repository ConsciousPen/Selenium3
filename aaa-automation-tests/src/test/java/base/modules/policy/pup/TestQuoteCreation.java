/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Deivydas Piliukaitis
 * @name Test Create Umbrella Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Quote
 * 3. Verify quote status is 'Premium Calculated'
 * @details
 */
public class TestQuoteCreation extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteCreation() {
        mainApp().open();

        createCustomerIndividual();

        policy.createQuote(getPolicyTD("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}
