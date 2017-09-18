/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;


import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyReinstatementWithLapse;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Policy Reinstate with lapse
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Cancel the Policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * 5. Reinstate Policy with Lapse
 * 6. Verify Policy status is 'Policy Active'
 * 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 * @details
 */

public class TestPolicyReinstatementWithLapse extends PolicyReinstatementWithLapse {
   
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }
    
    //currently removed from suite - not sure if applicable? field "Reinstate Date:" is disabled
    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, enabled = false)
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT )
    public void testPolicyReinstatementWithLapse(String state) {

        super.testPolicyReinstatementWithLapse();
    }
}
