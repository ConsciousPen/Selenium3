/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.customer;

import javax.ws.rs.client.Entity;

import aaa.rest.IRestClient;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;

public class CustomerCoreRESTMethods implements IRestClient {

    private ThreadLocal<RestServiceUtil> restClient = new ThreadLocal<RestServiceUtil>() {
        @Override protected RestServiceUtil initialValue() {
            return new RestServiceUtil("customercore-rs");
        }
    };

    public ResponseWrapper postCommunications(TestData data) {
        return getRestClient().processRequest("COMMUNICATIONS", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> customers
     */
    public ResponseWrapper getCustomers(TestData data) {
        return getRestClient().processRequest("CUSTOMERS", RestServiceUtil.RestMethod.GET, data);
    }

    public ResponseWrapper postCustomer(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("POSTCUSTOMERENTITY", RestServiceUtil.RestMethod.POST, entity, data);
    }

    public ResponseWrapper putCustomer(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("PUTCUSTOMERENTITY", RestServiceUtil.RestMethod.PUT, entity, data);
    }

    /**
     * <b>Target:</b> customers
     */
    public ResponseWrapper postCustomers(TestData data) {
        return getRestClient().processRequest("CUSTOMERS", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> customers
     */
    public ResponseWrapper postCustomersIndividual(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.TYPE.INDIVIDUAL", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> customers
     */
    public ResponseWrapper postCustomersNonIndividual(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.TYPE.NONINDIVIDUAL", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/anonymous
     */
    public ResponseWrapper getCustomersAnonymous(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ANONYMOUS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/details
     */
    public ResponseWrapper getCustomersDetails(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.DETAILS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/with-active-campaigns
     */
    public ResponseWrapper getCustomersWithActiveCampaigns(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.WITH-ACTIVE-CAMPAIGNS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}
     */
    public ResponseWrapper getCustomersItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ITEM", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}
     */
    public ResponseWrapper putCustomersItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/addresses
     */
    public ResponseWrapper getCustomersAddresses(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ADDRESSES", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/addresses
     */
    public ResponseWrapper postCustomersAddresses(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ADDRESSES", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/addresses/{contactId}
     */
    public ResponseWrapper deleteCustomersAddressesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ADDRESSES.ITEM", RestServiceUtil.RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/addresses/{contactId}
     */
    public ResponseWrapper putCustomersAddressesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.ADDRESSES.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/addresses/{contactId}
     */
    public ResponseWrapper putCustomersAddressesItem(Entity<?> entity, TestData data) {
        //return getRestClient().processRequest("PUTCUSTOMERENTITY", RestServiceUtil.RestMethod.PUT, entity, data);
        return getRestClient().processRequest("CUSTOMERS.PUTADDRESSESENTITY.ITEM", RestServiceUtil.RestMethod.PUT, entity, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/addresses
     */
    public ResponseWrapper postCustomersAddressesItem(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("CUSTOMERS.POSTADDRESSESENTITY.ITEM", RestServiceUtil.RestMethod.POST, entity, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/chats
     */
    public ResponseWrapper getCustomersChats(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.CHATS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/chats
     */
    public ResponseWrapper postCustomersChats(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.CHATS", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/chats/{contactId}
     */
    public ResponseWrapper deleteCustomersChatsItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.CHATS.ITEM", RestServiceUtil.RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/chats/{contactId}
     */
    public ResponseWrapper putCustomersChatsItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.CHATS.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/emails
     */
    public ResponseWrapper getCustomersEmails(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.EMAILS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/emails
     */
    public ResponseWrapper postCustomersEmails(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.EMAILS", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/emails/{contactId}
     */
    public ResponseWrapper deleteCustomersEmailsItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.EMAILS.ITEM", RestServiceUtil.RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/emails/{contactId}
     */
    public ResponseWrapper putCustomersEmailsItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.EMAILS.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/emails/{contactId}
     */
    public ResponseWrapper putCustomersEmailsItem(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("CUSTOMERS.EMAILSENTITY.ITEM", RestServiceUtil.RestMethod.PUT, entity, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/emails/{contactId}
     */
    public ResponseWrapper postCustomersEmailsItem(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("CUSTOMERS.POSTEMAILSENTITY.ITEM", RestServiceUtil.RestMethod.POST, entity, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/phones
     */
    public ResponseWrapper getCustomersPhones(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.PHONES", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/phones
     */
    public ResponseWrapper postCustomersPhones(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.PHONES", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/phones
     */
    public ResponseWrapper postCustomersPhonesItem(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("CUSTOMERS.PHONESENTITY", RestServiceUtil.RestMethod.POST, entity, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/phones/{contactId}
     */
    public ResponseWrapper deleteCustomersPhonesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.PHONES.ITEM", RestServiceUtil.RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/phones/{contactId}
     */
    public ResponseWrapper putCustomersPhonesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.PHONES.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/phones/{contactId}
     */
    public ResponseWrapper putCustomersPhonesItem(Entity<?> entity, TestData data) {
        return getRestClient().processRequest("CUSTOMERS.PHONESENTITY.ITEM", RestServiceUtil.RestMethod.PUT, entity, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/socialNets
     */
    public ResponseWrapper getCustomersSocialnets(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.SOCIALNETS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/socialNets
     */
    public ResponseWrapper postCustomersSocialnets(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.SOCIALNETS", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/socialNets/{contactId}
     */
    public ResponseWrapper deleteCustomersSocialnetsItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.SOCIALNETS.ITEM", RestServiceUtil.RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/socialNets/{contactId}
     */
    public ResponseWrapper putCustomersSocialnetsItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.SOCIALNETS.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/webAddresses
     */
    public ResponseWrapper getCustomersWebaddresses(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.WEBADDRESSES", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/webAddresses
     */
    public ResponseWrapper postCustomersWebaddresses(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.WEBADDRESSES", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/webAddresses/{contactId}
     */
    public ResponseWrapper deleteCustomersWebaddressesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.WEBADDRESSES.ITEM", RestServiceUtil.RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/webAddresses/{contactId}
     */
    public ResponseWrapper putCustomersWebaddressesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.WEBADDRESSES.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> campaigns
     */
    public ResponseWrapper getCampaigns(TestData data) {
        return getRestClient().processRequest("CAMPAIGNS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> campaigns
     */
    public ResponseWrapper postCampaigns(TestData data) {
        return getRestClient().processRequest("CAMPAIGNS", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /campaigns/{campaignId}
     */
    public ResponseWrapper getCampaignsItem(TestData data) {
        return getRestClient().processRequest("CAMPAIGNS.ITEM", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /campaigns/{campaignId}
     */
    public ResponseWrapper putCampaignsItem(TestData data) {
        return getRestClient().processRequest("CAMPAIGNS.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /campaigns/{campaignId}/associated-customers
     */
    public ResponseWrapper getCampaignsAssociatedCustomers(TestData data) {
        return getRestClient().processRequest("CAMPAIGNS.ASSICIATED-CUSTOMERS", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /campaigns/{campaignId}/performace-summary
     */
    public ResponseWrapper getCampaignsPerformanceSummary(TestData data) {
        return getRestClient().processRequest("CAMPAIGNS.PERFORMANCE-SUMMARY", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> customersOpportunities
     */
    public ResponseWrapper getCustomersOpportunities(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.OPPORTUNITIES", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> customersOpportunities
     */
    public ResponseWrapper postCustomersOpportunities(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.OPPORTUNITIES", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/Opportunities/{opportunityId}
     */
    public ResponseWrapper getCustomersOpportunitiesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.OPPORTUNITIES.ITEM", RestServiceUtil.RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/Opportunities/{opportunityId}
     */
    public ResponseWrapper putCustomersOpportunitiesItem(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.OPPORTUNITIES.ITEM", RestServiceUtil.RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> /customers/{customerNumber}/Opportunities/{opportunityId}/close
     */
    public ResponseWrapper postCustomersOpportunitiesClose(TestData data) {
        return getRestClient().processRequest("CUSTOMERS.OPPORTUNITIES.CLOSE", RestServiceUtil.RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> Opportunities
     */
    public ResponseWrapper getOpportunities(TestData data) {
        return getRestClient().processRequest("OPPORTUNITIES", RestServiceUtil.RestMethod.GET, data);
    }

    @Override public RestServiceUtil getRestClient() {
        return this.restClient.get();
    }
}
