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
import toolkit.verification.CustomAssert;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer New Customer Version Timeline Verification
 * @scenario
 * 1. Create Individual Customer
 * 2. [Take Action]=Inquiry
 * 3. Verify no [Timeline] links/buttons are NOT displayed in customer profile
 * 4. Update any customer data (except contacts)
 * 5. Save Updates
 * 6. [Take Action]=Inquiry
 * 7. Verify [Timeline] links/buttons are displayed for those sections/table entities that have been changed
 * 8. Click [Timeline] button/link for any of previously changed sections
 * 9. Verify 2 versions of corresponding customer section are displayed ( current version V.2 and V.1)
 * 10. Verify that version2 and version1 values is different
 * @details
 */
public class TestCustomerNewCustomerVersionTimelineVerification extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerNewCustomerVersionTimelineVerification() {
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

        CustomerSummaryPage.buttonTimeLine.click();

        CustomerSummaryPage.tableGeneralInfo.getRow(1).getCell(2).verify.contains("V.2");

        CustomerSummaryPage.tableGeneralInfo.getRow(1).getCell(3).verify.contains("V.1");

        // Check that, Version 2 First name is not equals Version 1 First name
        CustomAssert.assertFalse(CustomerSummaryPage.tableGeneralInfo.getRow(4).getCell(2).getValue().contentEquals(CustomerSummaryPage.tableGeneralInfo.getRow(4).getCell(3).getValue()));
    }
}
