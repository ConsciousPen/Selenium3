/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.rest.policy.personallines;

import aaa.main.modules.policy.PolicyType;
import aaa.rest.policy.PolicyRest;
import aaa.rest.policy.PolicyRestImpl;

public class PersonalLinesPolicyRest extends PolicyRest {
    private PolicyType policyType;

    public PersonalLinesPolicyRest(PolicyType personalLinesType) {
        this.policyType = personalLinesType;
    }

    public PolicyRestImpl createInstance(String customerNumber, String quoteNumber) {
        return new PolicyRestImpl(quoteNumber, customerNumber, policyType);
    }


}
