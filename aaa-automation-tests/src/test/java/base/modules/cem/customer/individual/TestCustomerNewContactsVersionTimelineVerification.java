/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.CustomerConstants;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.table.Row;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Create New Contacts Version Timeline Verification
 * @scenario
 * 1. Create Individual Customer with address
 * 2. [Take Action]=Inquiry
 * 3. Verify no [Timeline] links/buttons are NOT displayed in customer profile
 * 4. Update any customer data
 * 5. Save Updates
 * 6. [Take Action]=Inquiry
 * 7. Verify [Timeline] links/buttons are displayed for those sections/table entities that have been changed
 * 8. Click [Timeline] link for any of previously changed contact
 * 9. Verify 2 versions of corresponding contact are displayed ( current version V.2 and V.1)
 * 10. Verify that version2 and version1 values is different
 * @details
 */
public class TestCustomerNewContactsVersionTimelineVerification extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerNewContactsVersionTimelineVerification() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("STEP: Inquiry Customer # " + customerNumber);
        customer.inquiry().perform();

        CustomerSummaryPage.buttonTimeLine.verify.present(false);

        GeneralTab.buttonTopCancel.click();

        log.info("TEST: Update Contacts Details for Customer and created new version and Timeline Verification # " + customerNumber);
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        customer.updateContactsDetails().perform(tdCustomerIndividual.getTestData("ContactsDetails", "TestData")
                .adjust(tdCustomerIndividual.getTestData("ContactsDetails", "Adjustment_UpdateAddressType")), 1);

        log.info("STEP: Inquiry Customer # " + customerNumber);
        customer.inquiry().perform();

        CustomerSummaryPage.buttonTimeLine.verify.present();

        CustomerSummaryPage.buttonTimeLine.click();

        // Check that, Version 2 is not equals Version 1
        verifyVersionCompare();
    }

    private static void verifyVersionCompare() {
        CustomerSummaryPage.tableAddresslInfo.getRow(1).getCell(2).verify.contains("V.2");
        CustomerSummaryPage.tableAddresslInfo.getRow(1).getCell(3).verify.contains("V.1");

        CustomAssert.enableSoftMode();
        Row rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.ADDRRESS_TYPE);
        CustomAssert.assertFalse(rowChanged.getCell(2).getValue().contentEquals(rowChanged.getCell(3).getValue()));

        rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.ZIP_CODE);
        CustomAssert.assertFalse(rowChanged.getCell(2).getValue().contentEquals(rowChanged.getCell(3).getValue()));

        rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.CITY);
        CustomAssert.assertFalse(rowChanged.getCell(2).getValue().contentEquals(rowChanged.getCell(3).getValue()));

        rowChanged = CustomerSummaryPage.tableAddresslInfo.getRowContains(1, CustomerConstants.ADDRESS_LINE_1);
        CustomAssert.assertFalse(rowChanged.getCell(2).getValue().contentEquals(rowChanged.getCell(3).getValue()));
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
