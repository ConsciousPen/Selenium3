/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * @name Test Reinstatement Change Lapse option for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home Policy
 * 3. Cancel Policy
 * 4. Reinstate Policy without Lapse
 * 5. Verify 'Term includes lapse period' flag is not displayed in the policy consolidated view header
 * 6. Add Lapse Period for Policy
 * 7. Verify 'Term includes lapse period' flag is displayed in the policy consolidated view header
 * 8. Change Lapse Period for Policy
 * 9. Verify 'Term includes lapse period' flag is displayed
 * 10. Remove Lapse Period for Policy
 * 11. Verify 'Term includes lapse period' flag is not displayed
 * @details
 */
public class TestPolicyReinstatementChangeLapse extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyReinstatementChangeLapse(@Optional("CA") String state) {
		mainApp().open();

		getCopiedPolicy();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("Cancelling Policy #" + policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

		log.info("Reinstate Policy #" + policyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		assertThat(PolicySummaryPage.labelLapseExist).isPresent(false);

		log.info("TEST: Add Lapse Period for Policy #" + policyNumber);
		policy.changeReinstatementLapse().perform(getPolicyTD("ReinstatementChangeLapse", "TestData_Plus10Days"));
		assertThat(PolicySummaryPage.labelLapseExist).isPresent();

		log.info("TEST: Change Lapse Period for Policy #" + policyNumber);
		policy.changeReinstatementLapse().perform(getPolicyTD("ReinstatementChangeLapse", "TestData_Plus5Days"));
		assertThat(PolicySummaryPage.labelLapseExist).isPresent();

		log.info("TEST: Remove Lapse Period for Policy #" + policyNumber);
		policy.changeReinstatementLapse().perform(getPolicyTD("ReinstatementChangeLapse", "TestData"));
		assertThat(PolicySummaryPage.labelLapseExist).isPresent(false);
	}
}
