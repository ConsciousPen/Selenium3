/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import aaa.modules.regression.sales.auto_ss.TestPolicyBackdated;
import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Midterm Cancellation 
 * @scenario
 * 1. Create customer
 * 2. Create backdated policy
 * 3. Cancel policy with current date
 * @details
 */
public class TestPolicyCancellationMidTerm extends AutoSSBaseTest {


	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testPolicyCancellationMidTerm() {
	
		
		new TestPolicyBackdated().testPolicyBackdated();
		
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		
		
	}
}
