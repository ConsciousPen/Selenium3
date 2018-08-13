/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

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
		return PolicyType.AUTO_SS;
	}
	
	/**
	 * @author Lei Dai
	 * @name Test Create Auto Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Auto (Preconfigured) Quote
	 * 3. Verify quote status is 'Premium Calculated'
	 * @details
	 */

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteCreation(@Optional("") String state) {
		super.testQuoteCreation();
	}
	
	/**
	 * @author Lina Li
	 * @name Test Issue Auto Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create AutoSS Quote
	 * 3. Issue Quote
	 * 4. Verify policy status is 'Policy Active'
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteIssue(@Optional("") String state) {
		super.testQuoteIssue();
	}
	
	/**
	 * @author Lina Li
	 * @name Test Propose Auto Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create AutoSS Quote
	 * 3. Try to propose quote(click 'Cancel' button on propose screen)
	 * 4. Verify quote status id 'Premium Calculated'
	 * 5. Propose Quote
	 * 6. Verify quote status is 'Proposed'
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuotePropose(@Optional("") String state) {
		super.testQuotePropose();
	}
	
	/**
	 * @author Jelena Dembovska
	 * @name Test Customer Decline action
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Quote for corresponding product
	 * 3. Select action "Decline by customer"
	 * 4. Verify quote status is '"Customer Declined"'
	 * @details
	 */
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteDeclineByCustomer(@Optional("") String state) {
		super.testQuoteDeclineByCustomer();
	}
	
	/**
	 * @author Jelena Dembovska
	 * @name Test Company Decline action
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Quote for corresponding product
	 * 3. Select action "Decline by company"
	 * 4. Verify quote status is 'Company Declined'
	 * @details
	 */
	  
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteDeclineByCompany(@Optional("") String state) {
		super.testQuoteDeclineByCompany();
	}
	
	/**
	 * @author Jelena Dembovska
	 * @name Test quote copy action
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Quote for corresponding product
	 * 3. Select action "Copy from quote"
	 * 4. Verify new quote status is 'Data Gathering'
	 * @details
	 */	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteCopy(@Optional("") String state) {
		super.testQuoteCopy();
	}
}
