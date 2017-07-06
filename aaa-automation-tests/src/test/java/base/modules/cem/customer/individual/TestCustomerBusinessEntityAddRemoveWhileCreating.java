/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.enums.CustomerConstants;
import aaa.main.metadata.CustomerMetaData.BusinessEntityTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Kestutis Leskevicius
 * @name Test Customer Business Entity Add Remove While Creating
 * @scenario
 * 1. Create Individual Customer and add Business Entity
 * 2. Verify that Business Entity is present in Business Entity table
 * 3. Navigate to Business Entity tab and remove them
 * 4. Verify that Business Entity is not present in Business Entity table
 * @details
 */
public class TestCustomerBusinessEntityAddRemoveWhileCreating extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerBusinessEntityAddRemoveWhileCreating() {
        TestData dateUpdate = tdCustomerIndividual.getTestData("DataGather", "Adjustment_BusinessEntity").resolveLinks();
        DateTime newDate = new DateTime(dateUpdate.getValue(BusinessEntityTab.class.getSimpleName(), BusinessEntityTab.DATE_BUSINESS_STARTED.getLabel()), DateTime.MM_DD_YYYY);
        dateUpdate.adjust(TestData.makeKeyPath(BusinessEntityTab.class.getSimpleName(), BusinessEntityTab.DATE_BUSINESS_STARTED.getLabel()), newDate.toString());

        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData").adjust(dateUpdate).resolveLinks());

        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Update Customer # " + customerNumber);
        CustomerSummaryPage.tableBusinessEntities.getRow(1).getCell(CustomerConstants.CustomerBusinessEntitiesTable.DATE_BUSINESS_STARTED).verify.value(newDate.toString());

        customer.removeBusinessEntity().perform();
        CustomerSummaryPage.tableBusinessEntities.getRow(1).verify.present(false);
    }
}
