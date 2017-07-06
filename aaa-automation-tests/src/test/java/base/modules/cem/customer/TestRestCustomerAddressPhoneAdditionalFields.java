/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.customer.model.ContactMethodPhone;
import aaa.rest.customer.model.Customer;
import aaa.rest.wrapper.CommonModelWrapper;
import aaa.rest.wrapper.RestError;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name IPBQA-12308 - [DEV] Extension, Preferred Day(s) to Contact and Preferred Time(s) to Contact fields in REST API (Phone)
 * @scenario :
 * Check that new fields phoneExtension,preferredDaysToContact and preferredTimesToContact can be added/updated/received via customercore-rs
 *
 * Note: Automated all steps except 13-17 (redudant)
 *
 */
public class TestRestCustomerAddressPhoneAdditionalFields extends RestBaseTest {

    @Test
    @TestInfo(component = "Customer.REST", testCaseId = "IPBQA-12308")
    public void testRestCustomerAddressPhoneAdditionalFields() {

        CustomAssert.enableSoftMode();
        mainApp().open();
        customer.createViaUI(tdCustomersRest.getTestData("CRCustomer1"));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", customerId);
        Customer customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);

        //step 1 check
        CustomAssert.assertTrue("Phone extension is not null or empty", StringUtils.isEmpty(customer.getPhones().get(0).getPhoneExtension()));
        CustomAssert.assertTrue("Preferred Day(s) to Contact list is not empty", customer.getPhones().get(0).getPreferredDaysToContact().isEmpty());
        CustomAssert.assertTrue("Preferred Time(s) to Contact list is not empty", customer.getPhones().get(0).getPreferredTimesToContact().isEmpty());
        ContactMethodPhone phone = customer.getPhones().get(0);
        String phoneId = phone.getId();
        TestData phoneData = customerData.adjust("contactId", phoneId);
        phone.setPhoneExtension("380");
        phone.addPreferredDaysToContact(DayOfWeek.MONDAY.name(), DayOfWeek.SATURDAY.name());
        phone.addPreferredTimesToContact(PartOfDay.MORNING.name(), PartOfDay.AFTERNOON.name());

        ContactMethodPhone updatedPhone =
                customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                        .getResponse().readEntity(ContactMethodPhone.class);
        log.info("Response:\n" + updatedPhone);
        //step 2 check
        CustomAssert.assertEquals(String.format("Phone was not updated for customer %1$s", customerId), phone, updatedPhone);
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 3 check
        CustomAssert.assertEquals(String.format("Phone was not updated for customer %1$s", customerId), phone, customer.getPhones().get(0));

        phone.setPhoneExtension(RandomStringUtils.random(5, specialSymbols));

        CommonModelWrapper customerErrorWrapper = customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 4 check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());

        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));

        customerErrorWrapper = customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 5 check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());

        phone.setPhoneExtension("");
        phone.getPreferredDaysToContact().clear();
        phone.getPreferredTimesToContact().clear();

        updatedPhone =
                customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                        .getResponse().readEntity(ContactMethodPhone.class);
        log.info("Response:\n" + updatedPhone);
        //step 6 check
        CustomAssert.assertEquals(String.format("Phone was not updated for customer %1$s", customerId), phone, updatedPhone);

        //step 7 repeat steps starts
        phone.setPhoneExtension("1");
        phone.addPreferredDaysToContact(DayOfWeek.THURSDAY.name(), DayOfWeek.TUESDAY.name());
        phone.addPreferredTimesToContact(PartOfDay.EVENING.name(), PartOfDay.NIGHT.name());
        customer.getPhones().clear();
        customer.getPhones().add(phone);

        customer = customerRestClient.putCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 7 (repeat step 2) check
        CustomAssert.assertEquals(String.format("Phone was not updated for customer = %1$s", customerId), phone, customer.getPhones().get(0));
        //step 7 (repeat step 3) ignored, same checked in step 1 and in put method response
        phone.setPhoneExtension(RandomStringUtils.random(5, specialSymbols));
        customer.getPhones().clear();
        customer.getPhones().add(phone);
        customerErrorWrapper = customerRestClient.putCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper.toString());
        //step 7 (repeat step 4) check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());
        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));
        customerErrorWrapper = customerRestClient.putCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 7 (repeat step 5) check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());
        phone.setPhoneExtension("");
        phone.getPreferredDaysToContact().clear();
        phone.getPreferredTimesToContact().clear();
        customer = customerRestClient.putCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 7 (repeat step 6) check
        CustomAssert.assertEquals(String.format("Phone was not updated for customer = %1$s", customerId), phone, customer.getPhones().get(0));

        phone.setId(null);
        phone.setPhoneNumber("911");

        ContactMethodPhone addedPhone = customerRestClient.postCustomersPhonesItem(Entity.json(phone), customerData)
                .getResponse().readEntity(ContactMethodPhone.class);
        log.info("Response:\n" + addedPhone);
        //step 8 check
        CustomAssert.assertEquals(String.format("Newly added contact method phone was not added for customer = %1$s", customerId), phone, addedPhone);

        phone.setPhoneExtension("7");
        addedPhone = customerRestClient.postCustomersPhonesItem(Entity.json(phone), customerData)
                .getResponse().readEntity(ContactMethodPhone.class);
        log.info("Response:\n" + addedPhone);
        //step 9 check
        CustomAssert.assertEquals(String.format("Newly added contact method phone was not added for customer = %1$s", customerId), phone, addedPhone);

        phone.setPhoneExtension(RandomStringUtils.random(5, specialSymbols));
        customerErrorWrapper = customerRestClient.postCustomersPhonesItem(Entity.json(phone), customerData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 10 check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());

        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));
        customerErrorWrapper = customerRestClient.postCustomersPhonesItem(Entity.json(phone), customerData)
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 11 check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());

        //step 12 repeat 8-11
        customer.setCustomerNumber(null);
        customer.getAddresses().get(0).setId(null);
        phone.setPhoneNumber("101");
        phone.setPhoneExtension("");
        phone.setId(null);
        customer.getPhones().clear();
        customer.getPhones().add(phone);
        Customer newCustomer = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer.toString());
        //step 12 (repeat step 8) check
        CustomAssert.assertEquals("Customer was not created", customer, newCustomer);
        phone.setPhoneExtension("3");
        phone.addPreferredDaysToContact(DayOfWeek.FRIDAY.name());
        phone.addPreferredTimesToContact(PartOfDay.EVENING.name());
        newCustomer = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer.toString());
        //step 12 (repeat step 9) check
        CustomAssert.assertEquals("Customer was not created", customer, newCustomer);
        phone.setPhoneExtension(RandomStringUtils.random(5, specialSymbols));
        customerErrorWrapper = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 12 (repeat step 10) check
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());
        phone.setPhoneExtension(RandomStringUtils.randomNumeric(8));
        customerErrorWrapper = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerErrorWrapper);
        //step 12 (repeat step 11 check)
        CustomAssert
                .assertEquals("Incorrect error object", new RestError("422", "7 digits should be entered", "phoneExtension"), customerErrorWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerErrorWrapper.getErrorCode());
        //steps 13-17 not covered as redudant

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();

    }

}
