/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.QuoteActionAbstract;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestQuoteAction extends QuoteActionAbstract {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	/**
	 * @author Ryan Yu
	 * <b> Test Create Home Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Home Quote
	 * <p> 3. Verify quote status is 'Premium Calculated'
	 *
	 */
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteCreation(@Optional("") String state) {
		testQuoteCreation();
	}
	
	/**
	 * @author Ryan Yu
	 * <b> Test Issue Home Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Home Quote
	 * <p> 3. Issue Quote
	 * <p> 4. Verify policy status is 'Policy Active'
	 *
	 */
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuoteIssue(@Optional("") String state) {
		testQuoteIssue();
	}
	
	/**
	 * @author Ryan Yu
	 * <b> Test Propose Home Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Home Quote
	 * <p> 3. Try to propose quote(click 'Cancel' button on propose screen)
	 * <p> 4. Verify quote status id 'Premium Calculated'
	 * <p> 5. Propose Quote
	 * <p> 6. Verify eFolder 'Applications and Proposals/New Business' was created
	 * <p> 7. Verify quote status is 'Proposed'
	 *
	 */
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testQuotePropose(@Optional("") String state) {
		testQuotePropose();
	}
}
