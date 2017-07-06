/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.JobRunner;
import aaa.admin.pages.general.GeneralSchedulerPage.Job;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.BillingHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Renew 'Do Not Renew' flag for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Set 'Do Not Renew' for Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Move Server Date = Policy Expiration Date - 60 days (number of days configured in Renewal Automated Processing Strategy)
 * 7. Go to com.exigen.ipb.etcsa.admin's panel and run 'policyAutomatedRenewalAsyncTaskGenerationJob' job
 * 8. Verify 'Renewals' button is not displayed in the policy overview header
 * @details
 */
public class TestPolicyDoNotRenewWithRenew extends HomeSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyDoNotRenewWithRenew() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("TEST: Do Not Renew for Policy #" + policyNumber);
        policy.doNotRenew().perform(tdPolicy.getTestData("DoNotRenew", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelDoNotRenew.verify.present();

        log.info("TEST: Policy cannot bew renewed #" + policyNumber);

        TimeSetterUtil.getInstance().nextPhase(PolicySummaryPage.getExpirationDate().subtractDays(
                BillingHelper.DAYS_RENEW_STRATEGY));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.POLICY_AUTOMATED_RENEWAL_ASYNC_TASK_GENERATION_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.buttonRenewals.verify.enabled(false);
    }
}
