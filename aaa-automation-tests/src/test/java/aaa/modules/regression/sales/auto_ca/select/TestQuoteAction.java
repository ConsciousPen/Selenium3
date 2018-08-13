/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

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
		return PolicyType.AUTO_CA_SELECT;
	}
	
	/**
	 * @author Xiaolan Ge
	 * @name Test Create Auto Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Auto Quote
	 * 3. Verify quote status is 'Premium Calculated'
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testQuoteCreation(@Optional("CA") String state) {
		super.testQuoteCreation();
	}
	
	/**
	 * @author Xiaolan Ge
	 * @name Test Issue Auto Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Auto Quote
	 * 3. Issue Quote
	 * 4. Verify policy status is 'Policy Active'
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testQuoteIssue(@Optional("CA") String state) {
		super.testQuoteIssue();
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
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteDeclineByCustomer(@Optional("CA") String state) {
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
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteDeclineByCompany(@Optional("CA") String state) {
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
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteCopy(@Optional("CA") String state) {
		super.testQuoteCopy();
	}
}
