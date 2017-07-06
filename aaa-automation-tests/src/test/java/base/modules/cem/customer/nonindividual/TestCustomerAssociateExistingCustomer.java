/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.CustomerMetaData.AssociateExistingCustomerSearchActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Associate Existing Customer with another Customer
 * @scenario
 * 1. Create first Individual Customer
 * 2. Create second Non-Individual Customer
 * 3. Perform Associate Existing Customer action for the second Customer (Non-Individual)
 * 3. a) Select Relationship Type=Employment
 * 4. Search for first Customer and select it to associate with
 * 5. Verify that first Customer is present in Employment Census table
 * @details
 */
public class TestCustomerAssociateExistingCustomer extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAssociateExistingCustomer() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String secondCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Associate with Existing Customer # " + secondCustomerNumber);
        customer.associateExistingCustomer().perform(
                tdCustomerNonIndividual.getTestData("AssociateExistingCustomer", "TestData").adjust(
                        TestData.makeKeyPath(AssociateExistingCustomerSearchActionTab.class.getSimpleName(),
                                CustomerMetaData.AssociateExistingCustomerSearchActionTab.FIRST_NAME.getLabel()),
                        customerName.split("\\s+")[0]));
        CustomerSummaryPage.tableEmploymentCensus.verify.present();
        CustomerSummaryPage.tableEmploymentCensus.getColumn(3).getCell(1).verify.value(customerName + "\n(Lead)");
    }
}
