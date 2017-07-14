/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Delete Pending Endorsement for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Delete Pended Transaction for Home Policy
 * 5. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public class TestPolicyEndorsementDeletePending extends HomeSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyEndorsementDeletePending() {
        mainApp().open();

        createPolicy();

        log.info("TEST: Delete Pending Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.endorse().performAndExit(tdPolicy.getTestData("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.deletePendedTransaction().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
    }
}
