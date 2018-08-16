/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * @name Test add and remove 'Manual Renew' flag for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Set 'Manual Renew' flag for Policy
 * 4. Verify 'Manual Renew' flag is displayed in the policy overview header
 * 5. Remove Manual Renew flag from Policy
 * 6. Verify 'Manual Renew' flag is not displayed in the policy overview header
 * @details
 */
public class TestPolicyManualRenewFlagAddRemove extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyManualRenewFlagAddRemove(@Optional("") String state) {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("TEST: Add Manual Renew for Policy #" + policyNumber);
		policy.manualRenew().perform(getPolicyTD("ManualRenew", "TestData"));
		assertThat(PolicySummaryPage.labelManualRenew).isPresent();

		log.info("TEST: Remove Manual Renew for Policy #" + policyNumber);
		policy.removeManualRenew().perform(new SimpleDataProvider());
		assertThat(PolicySummaryPage.labelManualRenew).isPresent(false);
	}
}
