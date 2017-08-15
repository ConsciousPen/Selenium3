/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Tanya Dabolina
 * Name:
 * Test Create CA Policy Cancel and Reinstate
 * Scenario:
 * 1. Create Customer
 * 2. Create CA Select Auto Policy
 * 3. Verify Policy status is '	 Policy Active'
 * 4. Make Cancellation action (with reason 'Insured Non-Payment Of Premium').
 * 5. Verify policy status is Cancelled.
 * 6. Make Reinstatement action.
 * 7. Verify policy status is Active.
 *
 */
public class TestPolicyCancelReinstate extends AutoCaSelectBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyCancelReinstate() {
		mainApp().open();

		//createCustomerIndividual();

		createCustomerIndividual();
		createPolicy();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);


		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);


		log.info("TEST: Reinstate Policy With Lapse #" + policyNumber);

		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);


	}
}


