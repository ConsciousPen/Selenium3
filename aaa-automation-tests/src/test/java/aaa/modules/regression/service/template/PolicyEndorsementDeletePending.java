/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.impl.SimpleDataProvider;

/**
 * @author Xiaolan Ge
 * @name Test Delete Pending Endorsement for Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Policy
 * 3. Create endorsement
 * 4. Delete Pended Transaction for Policy
 * 5. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public abstract class PolicyEndorsementDeletePending extends PolicyBaseTest {

    public void testPolicyEndorsementDeletePending() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Delete Pending Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

        policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.deletePendedTransaction().perform(new SimpleDataProvider());
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
    }
}
