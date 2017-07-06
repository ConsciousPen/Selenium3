/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test renew delete pended transaction for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Renew Policy
 * 4. Delete Pended Transaction
 * 5. Verify 'Renewals' button is disabled
 * @details
 */
public class TestPolicyRenewDeletePending extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRenewDeletePending() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Delete Pending Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.renew().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonRenewals.click();

        policy.deletePendedTransaction().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonRenewals.verify.enabled(false);
    }
}
