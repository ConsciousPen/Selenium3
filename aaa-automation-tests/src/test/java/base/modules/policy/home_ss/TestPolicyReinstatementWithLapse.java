/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Reinstatement Home Policy with Lapse
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Cancel Policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * 5. Reinstate Policy with Lapse
 * 6. Verify Policy status is 'Policy Active'
 * 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 * @details
 */
public class TestPolicyReinstatementWithLapse extends HomeSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyReinstatementWithLapse() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Cancelling Policy #" + policyNumber);
        policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        log.info("TEST: Reinstate Policy With Lapse #" + policyNumber);
        policy.reinstate().perform(tdPolicy.getTestData("Reinstatement", "TestData_Plus14Days"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();
    }
}
