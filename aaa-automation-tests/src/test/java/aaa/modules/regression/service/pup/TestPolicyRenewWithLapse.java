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
 * @name Test Renew Umbrella Policy with Lapse
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Manual Renew Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Term includes lapse period' is not displayed in the header.
 * @details
 */
public class TestPolicyRenewWithLapse extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyRenewWithLapse() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.renew().performAndExit(getPolicyTD("Renew", "TestData_Plus370Days"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present(false);

//        PolicySummaryPage.buttonRenewals.click();
//        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
//        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();
    }
}
