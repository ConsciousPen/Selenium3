/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.productfactory;

import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;

public class ProductFactoryRESTMethods {
    private RestServiceUtil pfRestfulClient = new RestServiceUtil("pfrestful");
    private RestServiceUtil productDtRsClient = new RestServiceUtil("product-dt-rs");
    private RestServiceUtil productRtRsClient = new RestServiceUtil("product-rt-rs");

    public ResponseWrapper productDeploy(TestData data) {
        return pfRestfulClient.processRequest("DEPLOY", RestServiceUtil.RestMethod.POST, data);
    }

    public ResponseWrapper productExists(TestData data) {
        return pfRestfulClient.processRequest("EXIST", RestServiceUtil.RestMethod.GET, data);
    }

    public ResponseWrapper productInfo(TestData data) {
        return productDtRsClient.processRequest("PRODUCTS", RestServiceUtil.RestMethod.GET, data);
    }

    public ResponseWrapper productMetadata(TestData data) {
        return productRtRsClient.processRequest("METADATA", RestServiceUtil.RestMethod.GET, data);
    }

    public ResponseWrapper postProductOperation(String productCd, String policyNumber) {
        return productRtRsClient.processRequest("PRODUCT-OPERATIONS", RestServiceUtil.RestMethod.POST, new SimpleDataProvider()
                .adjust("productCd", productCd).adjust("entityRefNo", policyNumber));
    }

    public ResponseWrapper postQuoteCommandLoad(TestData td) {
        return productRtRsClient.processRequest("PRODUCT-STATE.LOAD", RestServiceUtil.RestMethod.POST, td);
    }

    public ResponseWrapper putQuoteCopy(TestData td) {
        return productRtRsClient.processRequest("PRODUCT-DATA.QUOTE.COPY", RestServiceUtil.RestMethod.PUT, td);
    }

    public ResponseWrapper putQuotePropose(TestData td) {
        return productRtRsClient.processRequest("PRODUCT-DATA.QUOTE.PROPOSE", RestServiceUtil.RestMethod.PUT, td);
    }

    public ResponseWrapper postChangeTabQuote(TestData td) {
        return productRtRsClient.processRequest("PRODUCT-STATE.CHANGE_TAB", RestServiceUtil.RestMethod.POST, td);
    }

    public ResponseWrapper postSaveQuote(TestData td) {
        return productRtRsClient.processRequest("PRODUCT-STATE.SAVE", RestServiceUtil.RestMethod.POST, td);
    }

    public TestData transferDataForInit(String productType, String action, String customerNumber, String instanceName) {
        TestData td = new SimpleDataProvider()
                .adjust("productCd", productType)
                .adjust("actionCd", action)
                .adjust("customerNumber", customerNumber)
                .adjust("entityClass", "com.exigen.ipb.policy.domain.PolicyEntity")
                .adjust("instanceName", instanceName);
        return td;

    }

}
