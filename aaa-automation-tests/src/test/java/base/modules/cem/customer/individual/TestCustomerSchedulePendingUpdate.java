/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.JobRunner;
import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.pages.MainPage;
import aaa.main.enums.BamConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTime;

/**
 * @author Veronika Shkatulo
 * @name Reschedule Pending Update (3768887)
 * @scenario
 * 1. Create Individual Customer
 * 2. Take action "Scheduled Update"
 * 3. Set Update Effective Date
 * 4. Change first name and last name, save and exit
 * 5. The System creates version in the future
 * 6. System execute pendingUpdateJob
 * 7. Verify customer's name is changed
 * 8. Verify update message is created
 * @details
 */
public class TestCustomerSchedulePendingUpdate extends CustomerBaseTest {

    private TestData tdCustomer = tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData");

    @Test
    public void testCustomerSchedulePendingUpdate() {
        mainApp().open();

        createCustomerIndividual();

        customer.scheduledUpdate().perform(tdCustomer);

        CustomerSummaryPage.linkPendingUpdatesPanel.verify.present();

        TimeSetterUtil.getInstance().nextPhase(new DateTime(tdCustomer.getValue(CustomerMetaData.ScheduledUpdateActionTab.class.getSimpleName(),
                CustomerMetaData.ScheduledUpdateActionTab.UPDATE_EFFECTIVE_DATE.getLabel()), DateTime.MM_DD_YYYY));

        adminApp().reopen();
        JobRunner.executeJob(GeneralSchedulerPage.Job.PENDING_UPDATE_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(customerNumber);

        CustomerSummaryPage.labelCustomerName.verify.value(tdCustomer.getValue(CustomerMetaData.GeneralTab.class.getSimpleName(),
                CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()) + " " + tdCustomer.getValue(CustomerMetaData.GeneralTab.class.getSimpleName(),
                CustomerMetaData.GeneralTab.LAST_NAME.getLabel()));

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(BamConstants.UPDATE_PENDING, customerNumber));
    }
}
