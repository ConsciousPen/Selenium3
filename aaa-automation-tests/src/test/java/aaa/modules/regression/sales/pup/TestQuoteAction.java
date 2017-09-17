/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.pup;

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
		return PolicyType.PUP;
	}
	
	/**
	 * @author Lina Li
	 * @name Test Create Umbrella Quote
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Personal Umbrella Policy Quote
	 * 3. Verify quote status is 'Premium Calculated'
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteCreation(String state) {
        super.testQuoteCreation();
    }
    
    /**
     * @author Xiaolan Ge
     * @name Test Issue Umbrella Quote
     * @scenario
     * 1. Create Customer
     * 2. Create Umbrella Quote
     * 3. Issue Quote
     * 4. Verify policy status is 'Policy Active'
     * @details
     */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteIssue(String state) {
    	super.testQuoteIssue();
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuoteCopy(String state) {
		super.testQuoteCopy();
	}
}
