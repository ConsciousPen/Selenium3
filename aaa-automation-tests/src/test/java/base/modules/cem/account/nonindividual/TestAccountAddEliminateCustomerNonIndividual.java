/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Account with Non-Individual Customer Add/Eliminate New Non-Individual Customer
 * @scenario
 * 1. Create Account with Non-Individual Customer
 * 2. Navigate to Account tab
 * 3. Add another Non-Individual Customer
 * 4. Verify that newly created Customer is present in Customer table
 * 5. Eliminate previously created Customer
 * 6. Verify that previously created eliminated Customer is not present in Customer table
 * @details
 */
public class TestAccountAddEliminateCustomerNonIndividual extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountAddEliminateCustomerNonIndividual() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Add Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        int customerCount = CustomerSummaryPage.tableCustomers.getRowsCount();
        account.addCustomer().perform(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        CustomerSummaryPage.tableCustomers.verify.rowsCount(customerCount + 1);

        log.info("TEST: Remove Customer # " + customerNumber);
        account.eliminateCustomer().perform(2);
        CustomerSummaryPage.tableCustomers.verify.rowsCount(customerCount);
    }
}
