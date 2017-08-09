/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Test;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Delete Pending Endorsement for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Delete Pended Transaction for Home Policy
 * 5. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public class TestPolicyEndorsementDeletePending extends HomeSSHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testPolicyEndorsementDeletePending() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		log.info("TEST: Delete Pending Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
		PolicySummaryPage.buttonPendedEndorsement.click();

		policy.deletePendedTransaction().perform(new SimpleDataProvider());
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	}
}
