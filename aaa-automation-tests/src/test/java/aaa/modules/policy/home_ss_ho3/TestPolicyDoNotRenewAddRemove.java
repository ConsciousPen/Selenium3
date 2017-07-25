/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Add and Remove 'Do Not Renew' flag for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Set Do Not Renew for Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Remove Do Not Renew for Policy
 * 7. Verify Policy status is 'Policy Active'
 * 8. Verify 'Do Not Renew' flag isn't displayed in the policy overview header
 * @details
 */
public class TestPolicyDoNotRenewAddRemove extends HomeSSHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyDoNotRenewAddRemove() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("TEST: Do Not Renew for Policy #" + policyNumber);
		policy.doNotRenew().perform(tdPolicy.getTestData("DoNotRenew", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelDoNotRenew.verify.present();

		log.info("TEST: Remove Do Not Renew for Policy #" + policyNumber);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelDoNotRenew.verify.present(false);
	}
}
