/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Yonggang Sun
 * @name Test Renew Policy with Lapse
 * @scenario
 * 1. Create Customer
 * 2. Create (AAA) Policy
 * 3. Manual Renew Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Term includes lapse period' is not displayed in the header.
 * @details
 */
public class PolicyRenewWithLapse extends PolicyBaseTest {

    public void testPolicyRenewWithLapse() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.renew().performAndExit(getPolicyTD("Renew", "TestData_Plus370Days"));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelLapseExist.verify.present(false);

        //TODO: Need change time before renew action.
        PolicySummaryPage.buttonRenewals.click();
//        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
//        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();
    }
}
