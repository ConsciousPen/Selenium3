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
 * @name Test Rescind Cancellation Umbrella Policy in Future Date
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Cancellation Umbrella Policy with Rescind Cancellation Date + 3 days
 * 4. Verify Policy status is "Cancellation Pending"
 * 5. Rescind Cancellation policy
 * 6. Verify Policy status is "Policy Active"
 * @details
 */
public class TestPolicyCancellationFutureDatedRescind extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCancellationFutureDatedRescind() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));

        log.info("Cancellation Pending for Policy #" + policyNumber);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

        log.info("TEST: Rescind Cancellation for Policy #" + policyNumber);
        policy.rescindCancellation().perform();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
