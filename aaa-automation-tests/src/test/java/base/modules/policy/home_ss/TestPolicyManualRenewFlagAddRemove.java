/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test add and remove 'Manual Renew' flag for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Set 'Manual Renew' flag for Policy
 * 4. Verify 'Manual Renew' flag is displayed in the policy overview header
 * 5. Remove Manual Renew flag from Policy
 * 6. Verify 'Manual Renew' flag is not displayed in the policy overview header
 * @details
 */
public class TestPolicyManualRenewFlagAddRemove extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyManualRenewFlagAddRemove() {
        mainApp().open();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Add Manual Renew for Policy #" + policyNumber);
        policy.manualRenew().perform(getPolicyTD("ManualRenew", "TestData"));
        PolicySummaryPage.labelManualRenew.verify.present();

        log.info("TEST: Remove Manual Renew for Policy #" + policyNumber);
        policy.removeManualRenew().perform(new SimpleDataProvider());
        PolicySummaryPage.labelManualRenew.verify.present(false);
    }
}
