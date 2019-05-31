/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyReinstatementWithLapse;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test Policy Reinstate with lapse </b>
 * <p> Steps:
 * <p> 1. Find customer or create new if customer does not exist;
 * <p> 2. Create new Policy;
 * <p> 3. Cancel the Policy
 * <p> 4. Verify Policy status is 'Policy Cancelled'
 * <p> 5. Reinstate Policy with Lapse
 * <p> 6. Verify Policy status is 'Policy Active'
 * <p> 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 *
 */

public class TestPolicyReinstatementWithLapse extends PolicyReinstatementWithLapse {
   
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
    
    @Parameters({"state"})
    @StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testPolicyReinstatementWithLapse(@Optional("") String state) {

		testPolicyReinstatementWithLapse();
    }
}
