/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.QuoteActionAbstract;
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testQuoteCreation() {
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
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testQuoteIssue() {
		super.testQuoteIssue();
	}
}
