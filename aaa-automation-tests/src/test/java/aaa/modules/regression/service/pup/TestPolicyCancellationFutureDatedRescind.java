/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test Rescind Cancellation Umbrella Policy in Future Date
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Cancellation Umbrella Policy with Rescind Cancellation Date + 3 days
 * 4. Verify Policy status is "Cancellation Pending"
 * @details
 */
public class TestPolicyCancellationFutureDatedRescind extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyCancellationFutureDatedRescind() {
        mainApp().open();

        getCopiedPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

//        policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));

        log.info("Cancellation Pending for Policy #" + policyNumber);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

//        AAA PUP Product can't Rescind Cancellation policy for  Cancellation pending stauts.
//        log.info("TEST: Rescind Cancellation for Policy #" + policyNumber);
//        policy.rescindCancellation().perform();
//
//        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE); 
    }
}
