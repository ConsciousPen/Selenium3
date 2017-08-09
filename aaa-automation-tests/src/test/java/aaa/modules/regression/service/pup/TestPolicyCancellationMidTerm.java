/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import aaa.modules.regression.sales.pup.TestPolicyBackdated;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

/**
 * @author Yongagng Sun
 * @name Test Midterm Cancellation Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Midterm Cancellation Policy
 * 4. Verify Policy status is "Policy Cancelled"
 * @details
 */
public class TestPolicyCancellationMidTerm extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyCancellationMidTerm() {
    	new TestPolicyBackdated().testPolicyBackdated();

        log.info("TEST: MidTerm Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
