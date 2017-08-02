/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ca;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test Renew Home Policy with Lapse
 * @scenario
 * 1. Create Customer
 * 2. Create Home Policy
 * 3. Manual Renew Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Term includes lapse period' is not displayed in the header.
 * 6. Click 'Renewals' button
 * 7. Verify Policy status is 'Policy Pending'
 * 8. Verify 'Term includes lapse period' is displayed in the header.
 * @details
 */
public class TestPolicyRenewWithLapse extends HomeCaHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRenewWithLapse() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.createRenewal(getPolicyTD("Renew", "TestData_Plus370Days")
                .adjust(getPolicyTD("Issue", "TestData_ExistentBillingAccount").resolveLinks()));

        PolicySummaryPage.linkPolicy.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present(false);

        PolicySummaryPage.buttonRenewals.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();
    }
}
