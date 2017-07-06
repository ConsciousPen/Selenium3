/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.pages.ErrorPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.ViewHistoryActionTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer View History Without Previous Versions
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Select action View History from drop-down
 * 3. Enter [Version Date]=system date and click [OK] button
 * 4. Verify [Version Date] field displays previously entered date
 * 5. Expand any section
 * 6. Verify current section data is displayed as V.1
 * 7. Verify вЂ�No corresponding prior version exists for this componentвЂ™ message is displayed above version table *
 * 8. Back to customer consolidated view
 * 9. [Take Action]=Update
 * 10. Make updates for the section previously reviewed on History page
 * 11. Save updates
 * 12. [Take Action]=View History
 * 13. Enter [Version Date]=system date and click [OK] button
 * 14. Expand the same section
 * 15. Verify two versions are displayed: updated section data is displayed as V.2 (current version) and V.1 (previous data)
 * 16. Verify No corresponding prior version exists for this component message is NOT displayed any more for this section
 * @details
 */
public class TestCustomerViewHistoryWithPreviousVersions extends CustomerBaseTest {

    private static String errorMessage = "No corresponding prior version exists for this component for selected date";

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerViewHistoryWithPreviousVersions() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: View History for Customer # " + customerNumber);
        customer.viewHistory().perform(tdCustomerNonIndividual.getTestData("ViewHistoryAction", "TestData"));

        new ViewHistoryActionTab().getAssetList().getControl(CustomerMetaData.ViewHistoryActionTab.VERSION_DATE.getLabel(), TextBox.class).verify.value(tdCustomerNonIndividual.getValue(
                "ViewHistoryAction", "TestData", CustomerMetaData.ViewHistoryActionTab.class.getSimpleName(), CustomerMetaData.ViewHistoryActionTab.VERSION_DATE.getLabel()));

        ViewHistoryActionTab.linkHistoryPanel.click();

        ViewHistoryActionTab.tableGeneralInfo.getRow(1).getCell(2).verify.contains("V.1");

        StaticElement labelMessage = ErrorPage.provideLabelErrorMessage(errorMessage);
        labelMessage.verify.present();

        GeneralTab.buttonTopCancel.click();

        log.info("STEP: Update for Customer # " + customerNumber);
        customer.update().perform(tdSpecific.getTestData("TestData"));

        log.info("STEP: View History for Customer # " + customerNumber);
        customer.viewHistory().perform(tdCustomerNonIndividual.getTestData("ViewHistoryAction", "TestData"));

        ViewHistoryActionTab.linkHistoryPanel.click();

        ViewHistoryActionTab.tableGeneralInfo.getRow(1).getCell(2).verify.contains("V.2");
        ViewHistoryActionTab.tableGeneralInfo.getRow(4).getCell(2).verify.value(tdSpecific.getValue("TestData", CustomerMetaData.GeneralTab.class.getSimpleName(),
                CustomerMetaData.GeneralTab.NAME_LEGAL.getLabel()));

        labelMessage.verify.present(false);
    }
}
