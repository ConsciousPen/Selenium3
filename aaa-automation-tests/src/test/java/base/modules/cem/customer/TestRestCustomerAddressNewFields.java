/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import javax.ws.rs.client.Entity;

import org.testng.annotations.Test;

import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.customer.model.ContactMethodAddress;
import aaa.rest.customer.model.Customer;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name IPBQA-11842 - [DEV] Address Validated?, Latitude, Longitude, Reference ID fields in REST API
 * @scenario :
 * Check that new fields Address Validated?,Latitude and Longitude can be added/updated/received via customercore-rs
 *
 * Note: Automated all steps except 12-15 (redudant)
 *
 */
public class TestRestCustomerAddressNewFields extends RestBaseTest {

    @Test
    @TestInfo(component = "Customer.REST", testCaseId = "IPBQA-11842")
    public void testRestCustomerAddressNewFields() {

        CustomAssert.enableSoftMode();
        mainApp().open();
        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", testDataKey));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", customerId);
        Customer customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        ContactMethodAddress address = customer.getAddresses().get(0);
        //step 1 check
        CustomAssert.assertEquals("Validation Indicator is not false", "false", address.getValidationIndicator());
        CustomAssert.assertEquals("Validation Latitude is not null", "null", address.getLatitude() == null ? "null" : "not null");
        CustomAssert.assertEquals("Validation Longitude is not null", "null", address.getLongitude() == null ? "null" : "not null");
        CustomAssert.assertEquals("Validation ReferenceId is not null", "null", address.getReferenceId() == null ? "null" : "not null");

        ContactMethodAddress updatedAddress = customer.getAddresses().get(0);
        String contactId = updatedAddress.getId();
        TestData contactData = DataProviderFactory.emptyData().adjust("customerNumber", customerId).adjust("contactId", contactId);
        updatedAddress.setId(null);
        updatedAddress.setCountryCd("US");
        updatedAddress.setPostalCode("32801");
        updatedAddress.setCity("Orlando");
        updatedAddress.setStateProvCd("FL");
        updatedAddress.setAddressLine1("45 Magnolia Avenue");

        ContactMethodAddress customerAddress = customerRestClient.putCustomersAddressesItem(Entity.json(updatedAddress), contactData)
                .getResponse().readEntity(ContactMethodAddress.class);
        log.info("Response:\n" + customerAddress);
        //step 2 check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customerAddress);
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Step 3 check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        updatedAddress.setValidationIndicator("true");
        customerAddress = customerRestClient.putCustomersAddressesItem(Entity.json(updatedAddress),
                contactData).getResponse().readEntity(ContactMethodAddress.class);
        log.info("Response:\n" + customerAddress);
        //step 4 check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customerAddress);
        updatedAddress.setLatitude("999");
        updatedAddress.setLongitude("-999");
        updatedAddress.setReferenceId("123");
        customerAddress = customerRestClient.putCustomersAddressesItem(Entity.json(updatedAddress),
                contactData).getResponse().readEntity(ContactMethodAddress.class);
        log.info("Response:\n" + customerAddress);
        //step 5 check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customerAddress);
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 6 check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        //step 7 skipped - redudant check
        updatedAddress.setId(contactId);
        updatedAddress.setCountryCd("US");
        updatedAddress.setPostalCode("75000");
        updatedAddress.setCity("Orlando");
        updatedAddress.setStateProvCd("FL");
        updatedAddress.setAddressLine1("55 Magnolia Avenue");
        updatedAddress.setLatitude(null);
        updatedAddress.setLongitude(null);
        updatedAddress.setReferenceId(null);
        updatedAddress.setValidationIndicator("false");
        customer.getAddresses().clear();
        customer.getAddresses().add(updatedAddress);
        customer =
                customerRestClient.putCustomer(Entity.json(customer),
                        customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 8 (repeat step 2) check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 8 (repeat step 3) check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        updatedAddress.setValidationIndicator("true");
        customer.getAddresses().clear();
        customer.getAddresses().add(updatedAddress);
        customer =
                customerRestClient.putCustomer(Entity.json(customer),
                        customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 8 (repeat step 4) check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        updatedAddress.setLatitude("999");
        updatedAddress.setLongitude("-999");
        updatedAddress.setReferenceId("123");
        customer.getAddresses().clear();
        customer.getAddresses().add(updatedAddress);
        customer =
                customerRestClient.putCustomer(Entity.json(customer),
                        customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 8 (repeat step 5) check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 8 (repeat step 6) check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customer.getAddresses().get(0));
        //step 8 (repeat step 7) skipped

        //step 9
        updatedAddress = customer.getAddresses().get(0);
        contactId = updatedAddress.getId();
        updatedAddress.setId(null);
        updatedAddress.setCountryCd("US");
        updatedAddress.setPostalCode("99666");
        updatedAddress.setCity("Orlando");
        updatedAddress.setStateProvCd("FL");
        updatedAddress.setAddressLine1("99 Magnolia Avenue");
        updatedAddress.setLatitude(null);
        updatedAddress.setLongitude(null);
        updatedAddress.setReferenceId(null);
        updatedAddress.setValidationIndicator("false");

        customerAddress = customerRestClient.postCustomersAddressesItem(Entity.json(updatedAddress),
                contactData).getResponse().readEntity(ContactMethodAddress.class);
        log.info("Response:\n" + customerAddress);
        //step 9 1st check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customerAddress);
        //step 9 2nd check
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        CustomAssert.assertTrue("Address was not added to existing client", customer.getAddresses().contains(updatedAddress));
        //step 10
        updatedAddress.setCountryCd("US");
        updatedAddress.setPostalCode("55555");
        updatedAddress.setCity("Orlando");
        updatedAddress.setStateProvCd("FL");
        updatedAddress.setAddressLine1("101 Magnolia Avenue");
        updatedAddress.setLatitude("777");
        updatedAddress.setLongitude("-888");
        updatedAddress.setReferenceId("321");
        updatedAddress.setValidationIndicator("true");
        customerAddress = customerRestClient.postCustomersAddressesItem(Entity.json(updatedAddress),
                contactData).getResponse().readEntity(ContactMethodAddress.class);
        log.info("Response:\n" + customerAddress);
        //step 10 1st check
        CustomAssert.assertEquals("Address was not updated", updatedAddress, customerAddress);
        //step 10 2nd check
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        CustomAssert.assertTrue("Address was not added to existing client", customer.getAddresses().contains(updatedAddress));
        //step 11
        customer.getAddresses().clear();
        customer.getAddresses().add(updatedAddress);
        customer.setCustomerNumber(null);
        customer.getAddresses().clear();
        updatedAddress.setCountryCd("US");
        updatedAddress.setPostalCode("66999");
        updatedAddress.setCity("Orlando");
        updatedAddress.setStateProvCd("FL");
        updatedAddress.setAddressLine1("112 Magnolia Avenue");
        updatedAddress.setLatitude(null);
        updatedAddress.setLongitude(null);
        updatedAddress.setReferenceId(null);
        updatedAddress.setValidationIndicator("false");
        customer.getAddresses().add(updatedAddress);

        customer =
                customerRestClient.postCustomer(Entity.json(customer),
                        DataProviderFactory.emptyData())
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        CustomAssert.assertEquals("New address for new customer was not created", updatedAddress, customer.getAddresses().get(0));
        updatedAddress.setLatitude("777");
        updatedAddress.setLongitude("-777");
        updatedAddress.setReferenceId("333");
        customer.getAddresses().clear();
        customer.getAddresses().add(updatedAddress);
        customer =
                customerRestClient.postCustomer(Entity.json(customer),
                        DataProviderFactory.emptyData())
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);

        CustomAssert.assertEquals("New address for new customer was not created", updatedAddress, customer.getAddresses().get(0));
        //steps 12-15 skipped (No need to check the same)
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

}
