/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

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
 * 1. Create Non Individual Customer
 * 2. a) Select action Schedule update from drop-down
 * 2. b) Enter Update Effective Date > system date
 * 2. c) Enter data for future version of customer data and save
 * 3. Select action Inquiry from drop-down
 * 4. Verify that 'Pending Update!' text is present for previously updated section of customer profile
 * 5. Click Cancel button
 * 6. Select action Delete Pending Updates from drop-down and confirm
 * 7. Select action Inquiry from drop-down
 * 8. Verify that 'Pending Update!' text is not present
 * @details
 */
public class TestCustomerScheduleUpdateRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerScheduleUpdateRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Scheduled Update for Customer # " + customerNumber);
        customer.scheduledUpdate().perform(tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData"));

        CustomerSummaryPage.linkPendingUpdatesPanel.verify.present();

        log.info("TEST: Delete Pending Updates for Customer # " + customerNumber);
        customer.deletePendingUpdates().perform();

        CustomerSummaryPage.linkPendingUpdatesPanel.verify.present(false);
    }
}
