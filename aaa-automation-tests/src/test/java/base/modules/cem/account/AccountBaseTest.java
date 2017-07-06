/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.cem.account;

import aaa.main.modules.account.Account;
import aaa.main.modules.account.AccountType;
import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerType;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class AccountBaseTest extends BaseTest {

    protected TestData tdCustomerIndividual = testDataManager.customer.get(CustomerType.INDIVIDUAL);
    protected TestData tdCustomerNonIndividual = testDataManager.customer.get(CustomerType.NON_INDIVIDUAL);

    protected TestData tdAccountIndividual = testDataManager.account.get(AccountType.INDIVIDUAL);
    protected TestData tdAccountNonIndividual = testDataManager.account.get(AccountType.NON_INDIVIDUAL);

    protected Account account = new Account();
    protected Customer customer = new Customer();
}
