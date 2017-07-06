/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.platform.policy;

import org.w3c.dom.Document;

import aaa.main.modules.policy.PolicyType;
import toolkit.exceptions.IstfException;

public class PolicyRestPlatform {

    public static Document createQuoteViaPlatformRest(PolicyType policyType, String customerNumber) {
        try {
            PolicyRESTMethods restPolicy = new PolicyRESTMethods();
            Document policyDocument = restPolicy.insertPolicy(policyType.getShortName(), PolicyRESTMethods.TxType.Quote, customerNumber);
            return policyDocument;
        } catch (Exception e) {
            throw new IstfException(e);
        }
    }

    public static Document createPolicyViaPlatformRest(PolicyType policyType, String customerNumber) {
        try {
            PolicyRESTMethods restPolicy = new PolicyRESTMethods();
            Document policyDocument = restPolicy.insertPolicy(policyType.getShortName(), PolicyRESTMethods.TxType.Policy, customerNumber);
            return policyDocument;
        } catch (Exception e) {
            throw new IstfException(e);
        }
    }
}
