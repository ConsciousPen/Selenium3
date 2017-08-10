/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public abstract class PolicyBaseTest extends BaseTest {

    protected IPolicy policy;
    private TestData tdPolicy;
    
    public PolicyBaseTest() {
        PolicyType type = (PolicyType) getPolicyType();
        if(type!=null) {
        	policy = type.get();
        }
        tdPolicy = testDataManager.policy.get(type);
    }

    protected TestData getPolicyTD() {
        return getPolicyTD("DataGather", "TestData");
    }
    
    protected TestData getPolicyTD(String fileName, String tdName) {
    	return getStateTestData(tdPolicy, fileName, tdName);
    }
    
    protected TestData getBackDatedPolicyTD() {
		return null;
	}

	protected TestData getBackDatedPolicyTD(String date) {
		return null;
	}
}
