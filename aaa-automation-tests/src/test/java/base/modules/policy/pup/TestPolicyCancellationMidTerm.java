/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test Midterm Cancellation Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Midterm Cancellation Policy
 * 4. Verify Policy status is "Policy Cancelled"
 * @details
 */
public class TestPolicyCancellationMidTerm extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCancellationMidTerm() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy(tdPolicy.getTestData("DataGather", "TestData")
                .adjust(tdPolicy.getTestData("Issue", "TestData").resolveLinks())
                .adjust(tdPolicy.getTestData("CopyFromQuote", "TestData_BackDated").resolveLinks()));

        log.info("TEST: MidTerm Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
