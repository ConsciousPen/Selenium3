/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyRewriteToNewNumber;

/**
 * @author Yonggang Sun
 * @name Test Rewrite to new number for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (AAA) Policy
 * 3. Cancel Policy
 * 4. Rewrite Policy
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Policy number different the cancelled policy number
 * @details
 */
public class TestPolicyRewriteToNewNumber extends PolicyRewriteToNewNumber {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyRewriteToNewNumber() {
    	super.setFileName("Rewrite");
    	super.setTdName("TestDataForBindRewrittenPolicy");
        super.testPolicyRewriteToNewNumber();
    }
}
