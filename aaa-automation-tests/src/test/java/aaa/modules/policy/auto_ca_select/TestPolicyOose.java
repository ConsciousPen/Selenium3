/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_select;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.policy.templates.PolicyOose;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

/**
 * @author N. Belakova
 * @name Test Policy OOSE functionality
 * @scenario
 * 1. Create new Auto CA Policy;
 * 2. Create Endorsement Policy with Endorsement effective date is today + 10 days: Add second Vehicle
 * 3. Create OOSE Policy with Endorsement effective date + 5 days: Add second NI/Driver
 * 4. Execute action 'Roll on Changes'
 * 5. Select values manually to have all changes
 * 6. Verify data quantity on Differences screen
 * 7. Complete Roll On
 * 8. Verify 2nd NI, Driver & Vehicle are added
 * @details
 */

public class TestPolicyOose extends PolicyOose {
    
	 @Override
	    protected PolicyType getPolicyType() {
	        return PolicyType.AUTO_CA_SELECT;
	    }
	 
	@Test
	@TestInfo(component = "Policy.AutoCA")
   public void testPolicyOose() {

		        super.testPolicyOose();
		        
		    }
}
