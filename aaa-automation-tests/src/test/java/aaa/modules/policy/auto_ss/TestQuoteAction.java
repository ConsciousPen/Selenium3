/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.QuoteActionAbstract;
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
	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testQuoteCreation() {
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
	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testQuoteIssue() {
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
	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testQuotePropose() {
		super.testQuotePropose();
	}
}
