/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.customer.model.ContactMethod;
import aaa.rest.customer.model.ContactMethodEmail;
import aaa.rest.customer.model.ContactMethodPhone;
import aaa.rest.customer.model.Customer;
import aaa.rest.wrapper.CommonModelWrapper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.rest.RestServiceUtil;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name IPBQA-13606 - [DEV] Consent Status and Consent Date fields in REST API (Phone and Email)
 * @scenario :
 * Check that new fields consentStatus and consentDate can be added/updated/received via customercore-rs
 *
 * Note: Automated only critical common sense steps. some of them combined to cover both email and phones consent fields
 *
 */

public class TestRestCustomerConsentFields extends RestBaseTest {

    @Test
    @TestInfo(component = "Customer.REST", testCaseId = "IPBQA-13606")
    public void testRestCustomerConsentFields() {
        CustomAssert.enableSoftMode();
        mainApp().open();
        customer.createViaUI(tdCustomersRest.getTestData("CRCustomer2"));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", customerId);
        Customer customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 1 check
        CustomAssert.assertEquals("Consent status is not correct", ConsentStatus.NOT_REQUESTED.name(), customer.getEmails().get(0).getConsentStatus());
        CustomAssert.assertTrue("Consent Date is not null or empty", StringUtils.isEmpty(customer.getEmails().get(0).getConsentDate()));

        ContactMethodEmail email = new ContactMethodEmail(customer.getEmails().get(0));
        ContactMethodPhone phone = new ContactMethodPhone(customer.getPhones().get(0));
        TestData emailData = DataProviderFactory.emptyData().adjust("customerNumber", customerId).adjust("contactId", email.getId()).resolveLinks();
        TestData phoneData = DataProviderFactory.emptyData().adjust("customerNumber", customerId).adjust("contactId", phone.getId()).resolveLinks();

        email.setConsentStatus(ConsentStatus.GRANTED.name());
        ContactMethod updatedEmail = customerRestClient.putCustomersEmailsItem(Entity.json(email), emailData)
                .getResponse().readEntity(ContactMethodEmail.class);
        //step 2 check
        CustomAssert.assertEquals("Email address was not updated", email, updatedEmail);
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 3 check
        CustomAssert.assertEquals("Email contact details object is not correct", email, customer.getEmails().get(0));
        phone.setConsentStatus(ConsentStatus.DENIED.name());
        phone.setConsentDate(TimeSetterUtil.getInstance().getCurrentTime().subtractDays(10).toString(CUSTOMER_RS_DATE_FORMAT));

        ContactMethod updatedPhone = customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                .getResponse().readEntity(ContactMethodPhone.class);
        //step 4 check
        CustomAssert.assertEquals("Phone was not updated", phone, updatedPhone);
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 5 check
        CustomAssert.assertEquals("Phone contact details object is not correct", phone, customer.getPhones().get(0));
        email.setConsentDate(TimeSetterUtil.getInstance().getCurrentTime().addDays(13).toString(CUSTOMER_RS_DATE_FORMAT));
        updatedEmail = customerRestClient.putCustomersEmailsItem(Entity.json(email), emailData)
                .getResponse().readEntity(ContactMethodEmail.class);
        //step 6 check
        CustomAssert.assertEquals("Email address was not updated", email, updatedEmail);
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //step 7 check
        CustomAssert.assertEquals("Email contact details object is not correct", email, customer.getEmails().get(0));
        //step 8 and 9 check
        assertPhoneIncorrectDateFormat(phone, phoneData, RestServiceUtil.RestMethod.PUT);
        email.setId(null);
        email.setEmailId("added@mail.com");
        email.setConsentStatus(ConsentStatus.REQUESTED.name());
        email.setConsentDate(TimeSetterUtil.getInstance().getCurrentTime().subtractDays(2).toString(CUSTOMER_RS_DATE_FORMAT));
        ContactMethod addedEmail = customerRestClient.postCustomersEmailsItem(Entity.json(email), customerData)
                .getResponse().readEntity(ContactMethodEmail.class);
        //step 10 check
        CustomAssert.assertEquals("Email was not added", email, addedEmail);
        phone.setId(null);
        phone.setConsentDate(TimeSetterUtil.getInstance().getCurrentTime().addDays(5).toString(CUSTOMER_RS_DATE_FORMAT));
        phone.setConsentStatus(ConsentStatus.GRANTED.name());
        phone.setPhoneNumber("911");
        phone.setPhoneExtension("321");
        ContactMethod addedPhone = customerRestClient.postCustomersPhonesItem(Entity.json(phone), customerData)
                .getResponse().readEntity(ContactMethodPhone.class);
        //step 11 check
        CustomAssert.assertEquals("Phone was not added", phone, addedPhone);
        //step 12 skipped as redudant (same check mixed in 11-12 steps)
        email.setEmailId("secondAdded@mail.com");
        customer.resetAllCustomersIds();

        Customer newCustomer = customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                .getResponse().readEntity(Customer.class);

        //step 14 it is enough to check at least one time as similar checks already done in separate tests
        CustomAssert.assertEquals("Customer was not created", customer, newCustomer);

        //step 13 and 14(repeat 13)
        assertPhoneIncorrectDateFormat(phone, phoneData, RestServiceUtil.RestMethod.POST);
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    private void assertPhoneIncorrectDateFormat(ContactMethodPhone phone, TestData phoneData, RestServiceUtil.RestMethod restMethod) {
        List<String> incorrectFormats = Arrays.asList("MM/dd/yyyy", "yyyy.MM.dd", "yyyy,MM,dd", "yyyy.dd.MM", "'abc'");
        CommonModelWrapper customerRestResponse;
        CommonModelWrapper phonesRestResponse;
        CommonModelWrapper expectedResponse = new CommonModelWrapper(tdCustomersRest.getTestData("ExpectedError"));
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", phoneData.getValue("customerNumber")).resolveLinks();
        Customer customer = customerRestClient.getCustomersItem(customerData)
                .getResponse().readEntity(Customer.class);
        for (String dateFormat : incorrectFormats) {
            phone.setId(null);
            phone.setConsentDate(TimeSetterUtil.getInstance().getCurrentTime().toString(dateFormat));
            customer.getPhones().clear();
            customer.getPhones().add(phone);
            switch (restMethod) {
                case PUT: {
                    customerRestResponse = customerRestClient.putCustomer(Entity.json(customer), customerData)
                            .getResponse().readEntity(CommonModelWrapper.class);
                    phonesRestResponse = customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                            .getResponse().readEntity(CommonModelWrapper.class);
                    break;
                }
                case POST: {
                    customer.resetAllCustomersIds();
                    phonesRestResponse = customerRestClient.putCustomersPhonesItem(Entity.json(phone), phoneData)
                            .getResponse().readEntity(CommonModelWrapper.class);
                    customerRestResponse = customerRestClient.postCustomer(Entity.json(customer), customerData)
                            .getResponse().readEntity(CommonModelWrapper.class);
                    break;
                }
                default: {
                    throw new IstfException("Unsupported choice");
                }
            }

            log.info("Phones Response:\n" + phonesRestResponse);
            log.info("Customer Response:\n" + customerRestResponse);
            CustomAssert.assertEquals(String.format("Incorrect response while trying to update phone with incorrect format [%1$s]", dateFormat),
                    expectedResponse, phonesRestResponse);
            CustomAssert.assertEquals(String.format("Incorrect response while trying to update phone with incorrect format [%1$s]", dateFormat),
                    expectedResponse, customerRestResponse);
        }

    }

}
