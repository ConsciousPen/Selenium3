/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.cem.customer;

import javax.ws.rs.client.Entity;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import aaa.main.modules.customer.defaulttabs.BusinessEntityTab;
import aaa.main.modules.customer.defaulttabs.GeneralTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.customer.model.Customer;
import aaa.rest.wrapper.CommonModelWrapper;
import aaa.rest.wrapper.RestError;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name IPBQA-9322 - [DEV] Nickname and Associate Business Entities fields in REST API (Individual entity)
 * @scenario :
 * Preconditions:
 * 1. Create Individual Lead A
 * Note: All checks done only for one Lead, no need to check the same for Customer(Lead with quote)
 */
public class TestRestCustomerAssociateBusinessEntities extends RestBaseTest {


    @Test
    @TestInfo(component = "Customer.REST", testCaseId = "IPBQA-9322")
    public void testRestCustomerAssociateBusinessEntities() {

        CustomAssert.enableSoftMode();
        mainApp().open();
        customer.createViaUI(tdCustomerIndividual.getTestData("DataGather", testDataKey)
                .mask(BusinessEntityTab.class.getSimpleName())
                .adjust(GeneralTab.class.getSimpleName(), tdCustomersRest.getTestData(GeneralTab.class.getSimpleName())));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        TestData customerData = DataProviderFactory.emptyData().adjust("customerNumber", customerId);
        Customer customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Check for Nickname step 1
        CustomAssert.assertTrue("Customer nick Name is not null or Empty", StringUtils.isEmpty(customer.getIndividualDetails().getNickname()));
        //Check Associate Business Entities step 1
        CustomAssert.assertEquals("Associate Business Entity is not false", "false", customer.getIndividualDetails().getAssociateBusinessEntity());
        customer.getIndividualDetails().setNickname(RandomStringUtils.randomAlphabetic(51));
        CommonModelWrapper customerWrapper =
                customerRestClient.putCustomer(Entity.json(customer), customerData)
                        .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerWrapper);
        //Checks for Nickname step 2
        CustomAssert.assertEquals("Incorrect error object", new RestError("422", "Should not exceed 50 symbols", "nickname"), customerWrapper.getErrors().get(0));
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerWrapper.getErrorCode());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Check for Nickname step 2
        CustomAssert.assertTrue("Customer nick Name is not null or Empty", StringUtils.isEmpty(customer.getIndividualDetails().getNickname()));
        String correctNickName = RandomStringUtils.randomAlphabetic(50);
        customer.getIndividualDetails().setNickname(correctNickName);
        customer.getIndividualDetails().setAssociateBusinessEntity("true");
        customer = customerRestClient.putCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Check for Nickname step 3
        CustomAssert.assertEquals("Customer nick Name is incorrect", correctNickName, customer.getIndividualDetails().getNickname());
        //Check Associate Business Entities step 2
        CustomAssert.assertEquals("Associate Business Entity is not true", "true", customer.getIndividualDetails().getAssociateBusinessEntity());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Check for Nickname step 4
        CustomAssert.assertEquals("Customer nick Name is incorrect", correctNickName, customer.getIndividualDetails().getNickname());
        //Check Associate Business Entities step 3
        CustomAssert.assertEquals("Associate Business Entity is not true", "true", customer.getIndividualDetails().getAssociateBusinessEntity());
        customer.getIndividualDetails().setNickname(null);
        customer.getIndividualDetails().setAssociateBusinessEntity("false");
        customer = customerRestClient.putCustomer(Entity.json(customer), customerData)
                .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Check for Nickname step 5
        CustomAssert.assertTrue("Customer nick Name is not null", null == customer.getIndividualDetails().getNickname());
        //Check Associate Business Entities step 4
        CustomAssert.assertEquals("Associate Business Entity is not false", "false", customer.getIndividualDetails().getAssociateBusinessEntity());
        customer =
                customerRestClient.getCustomersItem(customerData)
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + customer);
        //Check for Nickname step 6
        CustomAssert.assertTrue("Customer nick Name is not null", null == customer.getIndividualDetails().getNickname());
        //Check Associate Business Entities step 5
        CustomAssert.assertEquals("Associate Business Entity is not false", "false", customer.getIndividualDetails().getAssociateBusinessEntity());

        customer.setCustomerNumber(null);
        customer.getAddresses().get(0).setId(null);
        customer.getIndividualDetails().setNickname(RandomStringUtils.randomAlphabetic(51));
        customerWrapper =
                customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                        .getResponse().readEntity(CommonModelWrapper.class);
        log.info("Response:\n" + customerWrapper);
        //Check for Nickname step 7
        CustomAssert.assertEquals("Incorrect error object", new RestError("422", "Should not exceed 50 symbols", "nickname"), customerWrapper.getErrors().get(0));
        //Check for Nickname step 7
        CustomAssert.assertEquals("Incorrect errorCode", "422", customerWrapper.getErrorCode());

        customer.getIndividualDetails().setNickname(null);
        customer.getIndividualDetails().setAssociateBusinessEntity("false");
        Customer newCustomer =
                customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer);
        //Check for Nickname step 8
        CustomAssert.assertTrue("New Customer id is null", newCustomer.getCustomerNumber() != null);
        //Check Associate Business Entities step 6
        CustomAssert.assertEquals("Associate Business Entity is not false", "false", newCustomer.getIndividualDetails().getAssociateBusinessEntity());
        customer.getIndividualDetails().setNickname(correctNickName);
        customer.getIndividualDetails().setAssociateBusinessEntity("true");
        Customer newCustomer2 =
                customerRestClient.postCustomer(Entity.json(customer), DataProviderFactory.emptyData())
                        .getResponse().readEntity(Customer.class);
        log.info("Response:\n" + newCustomer2);
        //Check for Nickname step 9
        CustomAssert.assertTrue("New Customer id is null", newCustomer2.getCustomerNumber() != null);
        //Check Associate Business Entities step 7
        CustomAssert.assertEquals("Associate Business Entity is not true", "true", newCustomer2.getIndividualDetails().getAssociateBusinessEntity());

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }
}
