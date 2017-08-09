/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerType;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class CustomerBaseTest extends BaseTest {

    protected TestData tdCustomerIndividual = testDataManager.customer.get(CustomerType.INDIVIDUAL);
    protected TestData tdCustomerNonIndividual = testDataManager.customer.get(CustomerType.NON_INDIVIDUAL);

    protected Customer customer = new Customer();
}
