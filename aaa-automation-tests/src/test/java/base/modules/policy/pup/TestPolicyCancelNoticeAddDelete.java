/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Deivydas Piliukaitis
 * @name Test Cancel and Delete Notice for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Cancel Notice for Umbrella Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Delete Cancel Notice for Policy
 * 6. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */
public class TestPolicyCancelNoticeAddDelete extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCancelNoticeAddDelete() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Cancel Notice for Policy #" + policyNumber);
        policy.cancelNotice().perform(tdPolicy.getTestData("CancelNotice", "TestData"));
        PolicySummaryPage.labelCancelNotice.verify.present();

        log.info("TEST: Delete Cancel Notice for Policy #" + policyNumber);
        policy.deleteCancelNotice().perform(new SimpleDataProvider());
        PolicySummaryPage.labelCancelNotice.verify.present(false);
    }
}
