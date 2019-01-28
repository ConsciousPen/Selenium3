/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyDoNotRenewWithRenew;
import toolkit.utils.TestInfo;

/**
 * @author Yongagng Sun
 * @name Test Renew 'Do Not Renew' flag for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Set 'Do Not Renew' for Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Move Server Date = Policy Expiration Date - 60 days (number of days configured in Renewal Automated Processing Strategy)
 * 7. Go to com.exigen.ipb.eisa.admin's panel and run 'policyAutomatedRenewalAsyncTaskGenerationJob' job
 * 8. Verify 'Renewals' button is not displayed in the policy overview header
 * @details
 */
public class TestPolicyDoNotRenewWithRenew extends PolicyDoNotRenewWithRenew {

	@Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }
    
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP)
    public void TC01_CreatePolicyAddDoNotRenew(@Optional("") String state) {

		TC01_CreatePolicyAddDoNotRenewTemplate();
    }
    
	@Parameters({"state"})
	//@StateList("All")
	@Test(dependsOnMethods = "TC01_CreatePolicyAddDoNotRenew",
			groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void TC02_RenewPolicy(@Optional("") String state) {
		TC02_RenewPolicyTemplate();
	}
}
