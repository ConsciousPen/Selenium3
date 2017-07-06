/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.pages.ErrorPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.ViewHistoryActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.TextBox;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer View History Without Previous Versions
 * @scenario
 * 1. Create Individual Customer
 * 2. Select action View History from drop-down
 * 3. Enter [Version Date]=system date and click [OK] button
 * 4. Verify [Version Date] field displays previously entered date
 * 5. Expand any section
 * 6. Verify current section data is displayed as V.1
 * 7. Verify вЂ�No corresponding prior version exists for this componentвЂ™ message is displayed above version table *
 * @details
 */
public class TestCustomerViewHistoryWithoutPreviousVersions extends CustomerBaseTest {

    private static String errorMessage = "No corresponding prior version exists for this component for selected date";

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerViewHistoryWithoutPreviousVersions() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: View History for Customer # " + customerNumber);
        customer.viewHistory().perform(tdCustomerIndividual.getTestData("ViewHistoryAction", "TestData"));

        new ViewHistoryActionTab().getAssetList().getControl(CustomerMetaData.ViewHistoryActionTab.VERSION_DATE.getLabel(), TextBox.class).verify.value(tdCustomerIndividual.getValue(
                "ViewHistoryAction", "TestData", CustomerMetaData.ViewHistoryActionTab.class.getSimpleName(), CustomerMetaData.ViewHistoryActionTab.VERSION_DATE.getLabel()));

        ViewHistoryActionTab.linkHistoryPanel.click();
        ViewHistoryActionTab.tableGeneralInfo.getRow(1).getCell(2).verify.contains("Current Version");

        ErrorPage.provideLabelErrorMessage(errorMessage).verify.present();
    }
}
