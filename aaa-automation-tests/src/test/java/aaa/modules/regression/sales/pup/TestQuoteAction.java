/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.pup;

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
		return PolicyType.PUP;
	}
	
	/**
	 * @author Lina Li
	 * <b> Test Create Umbrella Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Personal Umbrella Policy Quote
	 * <p> 3. Verify quote status is 'Premium Calculated'
	 *
	 */
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteCreation(@Optional("") String state) {
		testQuoteCreation();
    }
    
    /**
     * @author Xiaolan Ge
	 * <b> Test Issue Umbrella Quote </b>
	 * <p> Steps:
	 * <p> 1. Create Customer
	 * <p> 2. Create Umbrella Quote
	 * <p> 3. Issue Quote
	 * <p> 4. Verify policy status is 'Policy Active'
	 *
     */
	@Parameters({"state"})
	//@StateList("All")
	//@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP )
    public void testQuoteIssue(@Optional("") String state) {
		testQuoteIssue();
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
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testQuoteCopy(@Optional("") String state) {
		testQuoteCopy();
	}
}
