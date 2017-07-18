/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;

import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


/**
 * @author Jelena Dembovska
 * @name Test Endorsement for Auto Policy with removal
 * @scenario
 * @details
 */
public class TestPolicyRulesOverride extends AutoSSBaseTest {

	private TestData class_td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData"); 
	
    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyEndorsementAdd() {
    	
        mainApp().open();
        
        
        createCustomerIndividual();
        
		log.info("Policy Creation Started...");
		

		
        policy.initiate();
        policy.getDefaultView().fillUpTo(class_td, ErrorTab.class);
        
        ErrorTab tab = new ErrorTab();
        tab.fillTab(class_td);
        
        tab.submitTab();
        
        
        
        //policy.updateRulesOverride().perform(td);
        


    }
    
    
}
