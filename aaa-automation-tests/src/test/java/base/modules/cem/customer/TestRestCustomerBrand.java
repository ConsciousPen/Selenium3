/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.RESTServiceUser;
import aaa.rest.customer.model.ContactMethod;
import aaa.rest.customer.model.Customer;
import aaa.rest.wrapper.CommonModelWrapper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name IPBQA-8164 - [DEV] Brand Name field in REST API
 * @scenario :
 * Preconditions:
 * 1. Create Agency A with Brand 1 assigned (any brand from existing in the application)
 * 2. Create User A with Brand 1
 * 3. Log under User A
 * Notes:
 * Agency A - CCRA1 (Create Customer REST Agency 1)
 * User A - CCRUser1 (Create Customer REST User 1)
 * Note: Test do not covers steps 13-16 because of they are redudant
 */
public class TestRestCustomerBrand extends RestBaseTest {

    @Test
    @TestInfo(component = "Customer.REST", testCaseId = "IPBQA-8164")
    public void testRestCustomerBrand() {

        CustomAssert.enableSoftMode();
        mainApp().open(RESTServiceUser.CUSTOMER_RS_USER.getLogin(), RESTServiceUser.CUSTOMER_RS_USER.getPassword());
        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", testDataKey));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        customerRestClient.getRestClient().adjustUser(RESTServiceUser.CUSTOMER_RS_USER.getLogin(), RESTServiceUser.CUSTOMER_RS_USER.getPassword());
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", customerId);
        Customer customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Brand Name step 1 check
        CustomAssert.assertTrue("Customer brand is not null", null == customer.getBrandCd());
        customer.setBrandCd("001");
        customer =
                customerRestClient.putCustomer(Entity.json(customer), customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Brand Name step 2 check
        CustomAssert.assertEquals("Customer brand is not 001", "001", customer.getBrandCd());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Brand Name step 4 check (step 3 skipped as not related to REST check)
        CustomAssert.assertEquals("Customer brand is not 001", "001", customer.getBrandCd());
        customer.setBrandCd(null);
        customer =
                customerRestClient.putCustomer(Entity.json(customer), customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Brand Name step 5 check
        CustomAssert.assertTrue("Customer brand is not null", null == customer.getBrandCd());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Brand Name step 6 check
        CustomAssert.assertTrue("Customer brand is not null", null == customer.getBrandCd());
        customer.setBrandCd("002");
        customer =
                customerRestClient.putCustomer(Entity.json(customer), customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Brand Name step 7 check
        CustomAssert.assertEquals("Customer brand is not 002", "002", customer.getBrandCd());
        CommonModelWrapper customerWrapper = customerRestClient.getCustomersItem(customerData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerWrapper);
        //Brand Name step 8 check
        CustomAssert.assertEquals("Error code does not match", "CUSTOMER_NOT_FOUND", customerWrapper.getErrorCode());
        //Brand Name step 8 check
        CustomAssert.assertEquals("Message does not match", String.format("Customer with number %1$s not found.", customerId), customerWrapper.getMessage());

        customer.setCustomerNumber(null);
        for (ContactMethod address : customer.getAddresses()) {
            address.setId(null);
        }
        customer.getIndividualDetails().setFirstName("CustomerRESTTest" + RandomStringUtils.randomNumeric(5));
        customer.getIndividualDetails().setLastName("LastName" + RandomStringUtils.randomNumeric(5));
        customer.setDisplayValue(customer.getIndividualDetails().getFirstName() + " " + customer.getIndividualDetails().getLastName());
        customer.setBrandCd("001");

        Customer newCustomer = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData()).getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //Brand Name step 9 check
        CustomAssert.assertFalse("Customer number of existing client is the same as for newly created via REST", customerId.equals(newCustomer.getCustomerNumber()));
        customer.setBrandCd(null);
        newCustomer = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData()).getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //Brand Name step 10 check
        CustomAssert.assertFalse("Customer number of existing client is the same as for newly created via REST", customerId.equals(newCustomer.getCustomerNumber()));
        customer.setBrandCd("002");
        newCustomer = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData()).getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //Brand Name step 11 check
        CustomAssert.assertTrue("New Customer id is null", newCustomer.getCustomerNumber() != null);
        customerWrapper = customerRestClient.getCustomersItem(customerData.adjust("customerNumber", newCustomer.getCustomerNumber()))
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerWrapper);
        //Brand Name step 12 check
        CustomAssert.assertEquals("Error code does not match", "CUSTOMER_NOT_FOUND", customerWrapper.getErrorCode());
        //Brand Name step 12 check
        CustomAssert.assertEquals("Message does not match", String.format("Customer with number %1$s not found.", newCustomer.getCustomerNumber()), customerWrapper.getMessage());
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
