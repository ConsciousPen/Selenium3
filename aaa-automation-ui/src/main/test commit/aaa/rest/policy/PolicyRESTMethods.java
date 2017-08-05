/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.policy;

import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;

public class PolicyRESTMethods {
    private RestServiceUtil policyRest = new RestServiceUtil("policy-rt-rs");

    public ResponseWrapper postExecute(TestData td) {
        return policyRest.processRequest("PRODUCT-DATA.EXECUTE", RestServiceUtil.RestMethod.POST, td);
    }
}
