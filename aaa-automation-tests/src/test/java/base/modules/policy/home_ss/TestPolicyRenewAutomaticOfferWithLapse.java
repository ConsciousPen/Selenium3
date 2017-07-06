/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.policy.home_ss;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.JobRunner;
import aaa.admin.pages.general.GeneralSchedulerPage.Job;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.BillingAccountType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Viachaslau Markouski
 * @name Test Renew Automatic Offer with Lapse for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Go to com.exigen.ipb.etcsa.admin's panel and run 'policyAutomatedRenewalAsyncTaskGenerationJob' job
 * 4. Verify Policy status is 'Data Gathering'
 * 5. Shift Time to next day
 * 6. Go com.exigen.ipb.etcsa.admin's panel and run 'renewalRatingJob' job
 * 7. Verify Policy status is 'Premium Calculated'
 * 8. Shift Time to next day
 * 9. Go com.exigen.ipb.etcsa.admin's panel and run 'renewalProposingJob' job
 * 10. Verify Policy status is 'Policy Active'
 * 11. Click 'Renewals' button
 * 12. Verify Policy status is 'Proposed'
 * 13. Change server date to policy expiration date + 25 days
 * 14. Verify Policy status is 'Policy Active'
 * 15. Click 'Renewals' button
 * 16. Verify Policy status is 'Proposed'
 * 17. Apply manual payment to renewal offer
 * 18. Verify Policy status is 'Policy Active'
 * 19. Verify 'Term includes lapse period' is displayed in the header
 * @details
 */
public class TestPolicyRenewAutomaticOfferWithLapse extends HomeSSBaseTest {
    private TestData tdBillingAccount = testDataManager.billingAccount.get(BillingAccountType.BILLING_ACCOUNT);

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRenewAutomaticOfferWithLapse() {
        mainApp().open();

        createCustomerIndividual();

        policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData")
                .adjust(tdPolicy.getTestData("DataGather", "Adjustment_NJ").resolveLinks())
                .adjust(tdPolicy.getTestData("Issue", "TestData").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        DateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

        log.info("TEST: Automatic Issue Renew for Policy #" + policyNumber);

        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.subtractDays(BillingHelper.DAYS_RENEW_STRATEGY));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.POLICY_AUTOMATED_RENEWAL_ASYNC_TASK_GENERATION_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.DATA_GATHERING).verify(1);

        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.subtractDays(BillingHelper.DAYS_RENEW_STRATEGY - 1));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.RENEWAL_RATING_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.subtractDays(BillingHelper.DAYS_RENEW_STRATEGY - 2));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.RENEWAL_PROPOSING_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.addDays(BillingHelper.DAYS_RENEW_WITH_LAPSE));
        mainApp().reopen();

        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonRenewals.click();
        new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
        String minimumDue = BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(
                "Minimum Due").getValue();

        new BillingAccount().acceptPayment().perform(tdBillingAccount.getTestData("AcceptPayment", "TestData_Cash"), minimumDue);

        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();
    }
}
