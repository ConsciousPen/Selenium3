/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Schedule Update Remove
 * @scenario
 * 1. Create Individual Customer
 * 2. Select action Schedule update from drop-down
 * 3. Select action Inquiry from drop-down
 * 4. Verify that Pending Update is present
 * 5. Click Cancel button
 * 6. Select action Delete Pending Update from drop-down
 * 7. Select action Inquiry from drop-down
 * 8. Verify that Pending Update is not present
 * @details
 */
public class TestCustomerScheduleUpdateRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerScheduleUpdateRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Scheduled Update for Customer # " + customerNumber);
        customer.scheduledUpdate().perform(tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData"));

        CustomerSummaryPage.linkPendingUpdatesPanel.verify.present();

        log.info("TEST: Delete Pending Updates for Customer # " + customerNumber);
        customer.deletePendingUpdates().perform();

        CustomerSummaryPage.linkPendingUpdatesPanel.verify.present(false);

    }
}
