/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.nonindividual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.main.metadata.AccountMetaData;
import aaa.main.modules.account.actiontabs.MoveCustomerSearchActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Remigijus Giedraitis
 * @name Test Account Move Non-Individual Customer to Account with Non-Individual Customer
 * @scenario
 * 1. Create first Account with Non-Individual Customer
 * 2. Create second Account with Non-Individual Customer
 * 3. Move second Customer to first Account
 * 4. Verify that first Account has 2 Customers
 * @details
 */
public class TestAccountMoveCustomerNonIndividual extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountMoveCustomerNonIndividual() {
        mainApp().open();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String firstCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String secondCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Move Customer # " + secondCustomerNumber + " to Customer # " + firstCustomerNumber);
        account.moveCustomer().perform(tdAccountNonIndividual.getTestData("MoveCustomer", "TestData").adjust(
                TestData.makeKeyPath(MoveCustomerSearchActionTab.class.getSimpleName(),
                        AccountMetaData.MoveCustomerSearchActionTab.CUSTOMER.getLabel()),
                firstCustomerNumber));
        CustomerSummaryPage.tableCustomers.verify.rowsCount(2);
    }
}
