/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.billing_and_payments.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Billing functionality
 * @scenario 1. Find customer or create new if customer does not exist;
 * 2. Create new Auto Policy;
 * 3. Move to Billing tab
 * 4. Make 4 different payments(Accept Payment with Payments types: Cash, Check, Credit Card, EFT);
 * 6. Verify payments are displayed in Payments & Other Transactions section.
 * 7. Make Refund with Payment type Check
 * 8. Verify Refund is displayed in Payments & Other Transactions section.
 * @details
 */

public class TestPolicyBilling extends PolicyBilling {


	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(statesExcept =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS)
	public void testBillingPayments(@Optional("") String state) {
		super.testBillingPayments();
	}
	
	@Parameters({"state"})
	@StateList(statesExcept =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS)
	public void testBillingRefund(@Optional("") String state) {
		super.testBillingRefund();
	}

}
