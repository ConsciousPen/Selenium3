/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData.DivisionsTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Divisions On Update Add Remove
 * @scenario
 * 1. Create Non Individual Customer
 * 2. Select Update action from drop-down
 * 3. Fill Divisions tab
 * 4. Verify that Divisions is present in Divisions table
 * 5. Navigate to Divisions tab and remove them
 * 6. Verify that Divisions is not present in Divisions table
 * @details
 */
public class TestCustomerDivisionsOnUpdateAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerDivisionsOnUpdateAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Divisions for Customer # " + customerNumber);
        customer.update().perform(tdCustomerNonIndividual.getTestData("Update", "TestData")
                .adjust(tdCustomerNonIndividual.getTestData("DataGather", "Adjustment_Divisions")));

        CustomerSummaryPage.tableDivisions.getRow(1).getCell(CustomerConstants.CustomerDivisionsTable.BILLING_METHOD).verify.value(
                tdCustomerNonIndividual.getTestData("DataGather").getTestData(
                        "Adjustment_Divisions").getTestData(DivisionsTab.class.getSimpleName()).getValue(DivisionsTab.BILLING_METHOD.getLabel()));

        log.info("TEST: Remove Divisions for Customer # " + customerNumber);
        customer.removeDivisions().perform();

        CustomerSummaryPage.tableDivisions.verify.present(false);
    }
}
