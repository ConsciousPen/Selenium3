/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Update
 * @scenario
 * 1. Create Individual Customer
 * 2. Perform Update Action
 * 3. Change First and Last names
 * 4. Verify that First and Last names were changed
 * @details
 */
public class TestCustomerUpdate extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerUpdate() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Update Customer # " + customerNumber);
        customer.update().perform(tdSpecific.getTestData("TestData"));
        CustomerSummaryPage.labelCustomerName.verify.value(tdSpecific.getTestData("TestData")
                .getTestData(CustomerMetaData.GeneralTab.class.getSimpleName())
                .getValue(CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()) + " " + tdSpecific.getTestData("TestData")
                        .getTestData(CustomerMetaData.GeneralTab.class.getSimpleName())
                        .getValue(CustomerMetaData.GeneralTab.LAST_NAME.getLabel()));
    }
}
