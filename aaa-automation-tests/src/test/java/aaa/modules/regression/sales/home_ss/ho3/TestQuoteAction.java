/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.QuoteActionAbstract;
import toolkit.utils.TestInfo;

public class TestQuoteAction extends QuoteActionAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	/**
	 * @author Ryan Yu
	 * @name Test Create Home Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Home Quote
	 * 3. Verify quote status is 'Premium Calculated'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteCreation(@Optional("") String state) {
		super.testQuoteCreation();
	}
	
	/**
	 * @author Ryan Yu
	 * @name Test Issue Home Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Home Quote
	 * 3. Issue Quote
	 * 4. Verify policy status is 'Policy Active'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteIssue(@Optional("") String state) {
		super.testQuoteIssue();
	}
	
	/**
	 * @author Ryan Yu
	 * @name Test Propose Home Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Home Quote
	 * 3. Try to propose quote(click 'Cancel' button on propose screen)
	 * 4. Verify quote status id 'Premium Calculated'
	 * 5. Propose Quote
	 * 6. Verify eFolder 'Applications and Proposals/New Business' was created
	 * 7. Verify quote status is 'Proposed'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuotePropose(@Optional("") String state) {
		super.testQuotePropose();
	}
}
