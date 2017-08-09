/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Test;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test Delete Pending Endorsement for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (AAA) Policy
 * 3. Create endorsement
 * 4. Delete Pended Transaction for Policy
 * 5. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public class TestPolicyEndorsementDeletePending extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyEndorsementDeletePending() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Delete Pending Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.deletePendedTransaction().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
    }
}
