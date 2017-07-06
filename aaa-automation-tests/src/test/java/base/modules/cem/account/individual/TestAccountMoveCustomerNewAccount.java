/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account.individual;

import org.testng.annotations.Test;

import aaa.EntityLogger;
import aaa.EntityLogger.EntityType;
import aaa.common.enums.NavigationEnum.CustomerSummaryTab;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.CustomerSummaryPage;
import base.modules.cem.account.AccountBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Remigijus Giedraitis
 * @name Test Account Move Individual Customer to new Account
 * @scenario
 * 1. Create Account with Individual Customer
 * 2. Move previously created Customer to new Account on Move Customer action after search with no results
 * 3. Verify that Account with moved customer has different Account number
 * @details
 */
public class TestAccountMoveCustomerNewAccount extends AccountBaseTest {

    @Test
    @TestInfo(component = "CRM.Account")
    public void testAccountMoveCustomerNewAccount() {
        mainApp().open();

        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        log.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        NavigationPage.toMainTab(CustomerSummaryTab.ACCOUNT.get());
        String accountNumber = CustomerSummaryPage.labelAccountNumber.getValue();
        NavigationPage.toMainTab(CustomerSummaryTab.CUSTOMER.get());

        log.info("TEST: Move Customer to new Account # " + accountNumber);
        account.moveCustomerToNewAccount().perform(tdAccountIndividual.getTestData("MoveCustomer", "TestData_NewAccount"));
        CustomAssert.assertFalse("Customer was not moved to new Account: " + accountNumber, accountNumber.equals(CustomerSummaryPage.labelAccountNumber.getValue()));
    }
}
