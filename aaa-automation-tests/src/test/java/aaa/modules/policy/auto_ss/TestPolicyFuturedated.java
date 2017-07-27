/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;


import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Tanya Dabolina
 * @name Test Create Auto SS in Future dated
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy in Future dated
 * 3. Verify policy status is Policy Pending
 * @details
 */
public class TestPolicyFuturedated extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void TestPolicyFutureDated() {
    	
    	 mainApp().open();
         
         createCustomerIndividual();
              
         TestData td = getStateTestData(tdPolicy, "DataGather", "TestData")
        		  .adjust(TestData.makeKeyPath("GeneralTab",
        					AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
        					AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
        					"/today+1m:MM/dd/yyyy");
		
         getPolicyType().get().createPolicy(td);

         PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
        
    }
}







