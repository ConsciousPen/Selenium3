/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Polina Kaziuchyts
 * @name Test Bind Umbrella Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Quote
 * 3. Bind Quote
 * 4. Verify Quote status is 'Bound'
 * @details
 */
public class TestQuoteBind extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteBind() {
        mainApp().open();

        createCustomerIndividual();

        createQuote();

        log.info("TEST: Bind Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.bind().perform(getPolicyTD("Bind", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.BOUND);
    }
}
