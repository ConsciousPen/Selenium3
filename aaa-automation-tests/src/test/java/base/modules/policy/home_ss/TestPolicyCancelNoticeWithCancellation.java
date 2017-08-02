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
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Cancellation Notice with Cancellation for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Cancel Notice for Policy and set Days Of Notice in the future
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Shift Time to Days Of Notice + 18 days (number of days configured for Cancellation in Cancellation Rule)
 * 6. Run 'cancellationConfirmationGenerationJob' in com.exigen.ipb.etcsa.admin panel
 * 7. Verify Policy status is 'Policy Cancelled'
 * 8. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */
public class TestPolicyCancelNoticeWithCancellation extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCancelNoticeWithCancellation() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Cancel Notice for Policy #" + policyNumber);
        policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
        PolicySummaryPage.labelCancelNotice.verify.present();

        int daysOfNotice = Integer.parseInt(getPolicyTD("CancelNotice", "TestData").getValue(
                CancelNoticeActionTab.class.getSimpleName(),
                HomeSSMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE.getLabel()));

        log.info("TEST: Cancellation Policy #" + policyNumber);

        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(
                daysOfNotice).plusDays(BillingHelper.DAYS_CANCELLATION));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.CANCELLATION_CONFIRMATION_GENERATION_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);

        PolicySummaryPage.labelCancelNotice.verify.present(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
