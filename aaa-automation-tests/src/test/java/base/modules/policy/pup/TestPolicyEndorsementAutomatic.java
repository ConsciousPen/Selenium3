/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.JobRunner;
import aaa.admin.pages.general.GeneralSchedulerPage.Job;
import aaa.common.enums.NavigationEnum.AdminAppLeftMenu;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.product.ProductEndorsementsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Andrey Shashenka
 * @name Test Endorsement Automatic for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Move Server Date to Policy Effective Date + 305 days (number of days configured in Endorsement Automated Processing Strategy)
 * 4. Go to com.exigen.ipb.etcsa.admin's panel and run 'automatedProcessingInitiationJob' job
 * 5. Click 'Pended Endorsement' button on the consolidated view header
 * 6. Verify Policy status is 'Data Gathering'
 * 7. Move Server Date to next day
 * 8. Go to com.exigen.ipb.etcsa.admin's panel and run 'automatedProcessingRatingJob' job
 * 9. Click 'Pended Endorsement' button on the consolidated view header
 * 10. Verify Policy status is 'Premium Calculated'
 * 11. Move Server Date to next day
 * 12. Go to com.exigen.ipb.etcsa.admin's panel and run 'automatedProcessingIssuingOrProposingJob' job
 * 13. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public class TestPolicyEndorsementAutomatic extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyEndorsementAutomatic() {
        mainApp().open();

        createCustomerIndividual();

        policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData")
                .adjust(tdPolicy.getTestData("DataGather", "Adjustment_NY").resolveLinks())
                .adjust(tdPolicy.getTestData("Issue", "TestData").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        DateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        log.info("TEST: Automatic Endorsement for Policy #" + policyNumber);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.addDays(BillingHelper.DAYS_ENDORSEMENT_STRATEGY));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.AUTOMATED_PROCESSING_INITIATION_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.buttonPendedEndorsement.click();
        new ProductEndorsementsVerifier().setStatus(ProductConstants.PolicyStatus.DATA_GATHERING).verify(1);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.addDays(BillingHelper.DAYS_ENDORSEMENT_STRATEGY + 1));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.AUTOMATED_PROCESSING_RATING_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.buttonPendedEndorsement.click();
        new ProductEndorsementsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.addDays(BillingHelper.DAYS_ENDORSEMENT_STRATEGY + 2));

        adminApp().reopen();
        NavigationPage.toViewLeftMenu(AdminAppLeftMenu.GENERAL_SCHEDULER.get());
        JobRunner.executeJob(Job.AUTOMATED_PROCESSING_ISSUING_OR_PROPOSING_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
    }
}
