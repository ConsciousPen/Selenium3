/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.customer.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.cem.customer.CustomerBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Customer Add/Remove Agency
 * @scenario
 * 1. Create Individual Customer
 * 2. Add Agency
 * 3. Verify that added Agency is present in Agencies table
 * 4. Remove previously added Agency
 * 5. Verify that previously removed Agency is not present in Agencies table
 * @details
 */
public class TestCustomerAgencyAddRemove extends CustomerBaseTest {

    @Test
    @TestInfo(component = "CRM.Customer")
    public void testCustomerAgencyAddRemove() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        CustomerSummaryPage.buttonAgencies.click();
        int agenciesCount = CustomerSummaryPage.tableAgencies.getRowsCount();

        log.info("TEST: Add Agency for Customer # " + customerNumber);
        customer.addAgency().perform(tdCustomerIndividual.getTestData("AddAgency", "TestData"));
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        CustomerSummaryPage.buttonAgencies.click();
        CustomerSummaryPage.tableAgencies.verify.rowsCount(agenciesCount + 1);

        log.info("TEST: Remove Agency for Customer # " + customerNumber);
        customer.removeAgency().perform(1, tdCustomerIndividual.getTestData("RemoveAgency", "TestData"));
        NavigationPage.toViewSubTab(CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        CustomerSummaryPage.buttonAgencies.click();
        CustomerSummaryPage.tableAgencies.verify.rowsCount(agenciesCount);
    }
}
