/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test renew delete pended transaction for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create AutoSS Policy
 * 3. Renew Policy
 * 4. Delete Pended Transaction
 * 5. Verify 'Renewals' button is disabled
 * @details
 */
public class TestPolicyRenewDeletePending extends AutoSSBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyRenewDeletePending() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Delete Pending Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.renew().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonRenewals.click();

        policy.deletePendingRenwals().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonRenewals.verify.enabled(false);
    }
}
