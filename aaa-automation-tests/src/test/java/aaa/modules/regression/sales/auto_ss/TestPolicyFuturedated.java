/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import toolkit.utils.TestInfo;
import aaa.modules.regression.sales.template.PolicyFuturedated;

/**
 * @author Tanya Dabolina
 * @name Test Create Auto SS in Future dated
 */

public class TestPolicyFuturedated extends PolicyFuturedated {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

    @Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyFuturedated(@Optional("") String state) {
    	
    	super.testPolicyFuturedated();
        
    }
}


