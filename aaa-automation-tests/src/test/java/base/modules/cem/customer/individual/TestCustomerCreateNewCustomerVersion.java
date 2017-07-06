/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Create New Customer Version
 * @scenario
 * 1. Create Individual Customer
 * 2. [Take Action]=Inquiry
 * 3. Verify no [Timeline] links/buttons are NOT displayed in customer profile
 * 4. Update any customer data (except contacts)
 * 5. Save Updates
 * 6. [Take Action]=Inquiry
 * 7. Verify [Timeline] links/buttons are displayed for those sections/table entities that have been changed
 * @details
 */
public class TestCustomerCreateNewCustomerVersion extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerCreateNewCustomerVersion() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("STEP: Inquiry Customer # " + customerNumber);
        customer.inquiry().perform();

        CustomerSummaryPage.buttonTimeLine.verify.present(false);

        GeneralTab.buttonTopCancel.click();

        log.info("STEP: Update for Customer # " + customerNumber);
        customer.update().perform(tdCustomerIndividual.getTestData("TestCustomerUpdate", "TestData"));

        log.info("STEP: Inquiry Customer # " + customerNumber);
        customer.inquiry().perform();

        CustomerSummaryPage.buttonTimeLine.verify.present();
    }
}
