/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import javax.ws.rs.client.Entity;

import org.testng.annotations.Test;

import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.customer.model.Customer;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name IPBQA-15012 - [DEV] Archived Indicator in REST API
 * @scenario :
 * Check that new field archived can be added/updated/received via customercore-rs
 *
 * Note: Automated all steps except 11 (redudant)
 *
 */

public class TestRestCustomerArchivedIndicator extends RestBaseTest {

    @Test
    @TestInfo(component = "Customer.REST", testCaseId = "IPBQA-15012")
    public void testRestCustomerArchivedIndicator() {

        CustomAssert.enableSoftMode();
        mainApp().open();
        customer.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", testDataKey));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", customerId);
        Customer customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 1 check
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", customerId), "false", customer.getArchived());

        customer.setArchived("true");
        customer =
                customerRestClient.putCustomer(Entity.json(customer), customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 2 check
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", customerId), "true", customer.getArchived());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 3
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", customerId), "true", customer.getArchived());
        customer.setArchived("false");
        customer =
                customerRestClient.putCustomer(Entity.json(customer), customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 4 check
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", customerId), "false", customer.getArchived());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 5
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", customerId), "false", customer.getArchived());
        //step 6 ignored
        customer.setCustomerNumber(null);
        customer.getAddresses().get(0).setId(null);
        customer.setArchived("true");
        Customer newCustomer = customerRestClient.postCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //step 9
        CustomAssert.assertEquals("Archived indicator for new customer is incorrect", "true", newCustomer.getArchived());
        newCustomer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //step 8
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", newCustomer.getCustomerNumber()), "true", customer.getArchived());

        customer.setArchived("false");
        newCustomer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //step 7
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", newCustomer.getCustomerNumber()), "false", customer.getArchived());
        newCustomer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //step 10
        CustomAssert.assertEquals(String.format("Archived indicator for customer(id=%1$s) is incorrect", newCustomer.getCustomerNumber()), "false", customer.getArchived());
        //step 11 ignored as redudant
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
