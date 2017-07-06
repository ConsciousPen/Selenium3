/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Copy Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Copy Policy
 * 4. Verify new Policy
 * @details
 */
public class TestPolicyCopy extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCopy() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Copy Policy #" + policyNumber);
        policy.policyCopy().perform(tdPolicy.getTestData("CopyFromPolicy", "TestData"));
        policy.calculatePremiumAndPurchase(tdPolicy.getTestData("DataGather", "TestData"));

        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Copied Policy #" + policyNumberCopied);

        CustomAssert.assertFalse(String.format("Copied policy %s number is the same as initial policy number %s",
                policyNumberCopied, policyNumber),
                policyNumber.equals(policyNumberCopied));
    }
}
