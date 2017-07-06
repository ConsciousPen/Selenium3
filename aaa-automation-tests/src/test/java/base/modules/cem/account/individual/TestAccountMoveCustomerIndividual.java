/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.individual;

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
 * @name Test Account Move Individual Customer to Account with Individual Customer
 * @scenario
 * 1. Create first Account with Individual Customer
 * 2. Create second Account with Individual Customer
 * 3. Move second Customer to first Account
 * 4. Verify that first Account has 2 Customers
 * @details
 */
public class TestAccountMoveCustomerIndividual extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountMoveCustomerIndividual() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String firstCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));
        String secondCustomerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        log.info("TEST: Move Customer # " + secondCustomerNumber);
        account.moveCustomer().perform(tdAccountIndividual.getTestData("MoveCustomer", "TestData").adjust(
                TestData.makeKeyPath(MoveCustomerSearchActionTab.class.getSimpleName(),
                        AccountMetaData.MoveCustomerSearchActionTab.CUSTOMER.getLabel()),
                firstCustomerNumber));
        CustomerSummaryPage.tableCustomers.verify.rowsCount(2);
    }
}
