/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Jelena Dembovska
 * @name Test Auto policy creation with 2 NI/2Drivers/2Vehicles
 * @scenario
 * @details
 */
public class TestPolicyCreationBig extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyCreationBig() {
    	
        mainApp().open();
        
        createCustomerIndividual();
        
		log.info("Policy Creation Started...");
		
        TestData bigPolicy_td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
		getPolicyType().get().createPolicy(bigPolicy_td);

        CustomAssert.enableSoftMode();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        PolicySummaryPage.tablePolicyDrivers.verify.rowsCount(2);
        PolicySummaryPage.tablePolicyVehicles.verify.rowsCount(2);
        PolicySummaryPage.tableInsuredInformation.verify.rowsCount(2);
        
        
        CustomAssert.assertAll();
    }
    
    
}
