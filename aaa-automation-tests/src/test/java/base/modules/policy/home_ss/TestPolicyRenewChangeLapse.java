/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Renew Change Lapse option for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Manual Renew Policy
 * 4. Add Lapse Period for Policy
 * 5. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 * 6. Change Lapse Period for Policy
 * 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 * 8. Remove Lapse Period for Policy
 * 11. Verify 'Term includes lapse period' flag is not displayed
 * @details
 */
public class TestPolicyRenewChangeLapse extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRenewChangeLapse() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Manual Renew for Policy #" + policyNumber);
        policy.createRenewal(getPolicyTD("Issue", "TestData_ExistentBillingAccount"));

        log.info("TEST: Add Lapse Period for Policy Renew #" + policyNumber);
        policy.policyChangeRenewalLapse().perform(getPolicyTD("RenewChangeLapse", "TestData_Plus370Days"));
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();

        log.info("TEST: Change Lapse Period for Policy Renew #" + policyNumber);
        policy.policyChangeRenewalLapse().perform(getPolicyTD("RenewChangeLapse", "TestData_Plus368Days"));
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();

        log.info("TEST: Remove Lapse Period for Policy Renew #" + policyNumber);
        policy.policyChangeRenewalLapse().perform(getPolicyTD("RenewChangeLapse", "TestData"));
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present(false);
    }
}
