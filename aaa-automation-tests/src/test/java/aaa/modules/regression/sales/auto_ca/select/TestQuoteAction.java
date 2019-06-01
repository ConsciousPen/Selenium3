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
	 * <b> Test Create Auto Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Auto Quote
	 * <p> 3. Verify quote status is 'Premium Calculated'
	 *
	 */
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testQuoteCreation(@Optional("CA") String state) {
		testQuoteCreation();
	}
	
	/**
	 * @author Xiaolan Ge
	 * <b> Test Issue Auto Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Auto Quote
	 * <p> 3. Issue Quote
	 * <p> 4. Verify policy status is 'Policy Active'
	 *
	 */
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testQuoteIssue(@Optional("CA") String state) {
		testQuoteIssue();
	}
	
	/**
	 * @author Jelena Dembovska
	 * <b> Test Customer Decline action </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Quote for corresponding product
	 * <p> 3. Select action "Decline by customer"
	 * <p> 4. Verify quote status is '"Customer Declined"'
	 *
	 */
	
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteDeclineByCustomer(@Optional("CA") String state) {
		testQuoteDeclineByCustomer();
	}
	
	/**
	 * @author Jelena Dembovska
	 * <b> Test Company Decline action </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Quote for corresponding product
	 * <p> 3. Select action "Decline by company"
	 * <p> 4. Verify quote status is 'Company Declined'
	 *
	 */
	
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteDeclineByCompany(@Optional("CA") String state) {
		testQuoteDeclineByCompany();
	}
	
	/**
	 * @author Jelena Dembovska
	 * <b> Test quote copy action </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Quote for corresponding product
	 * <p> 3. Select action "Copy from quote"
	 * <p> 4. Verify new quote status is 'Data Gathering'
	 *
	 */	
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testQuoteCopy(@Optional("CA") String state) {
		testQuoteCopy();
	}
}
