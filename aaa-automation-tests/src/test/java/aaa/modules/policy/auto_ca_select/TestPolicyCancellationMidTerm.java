/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_select;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Lei Dai
 * @name Test Midterm Cancellation Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto Policy
 * 3. Midterm Cancellation Policy
 * 4. Verify Policy status is "Policy Cancelled"
 * @details
 */
public class TestPolicyCancellationMidTerm extends AutoCaSelectBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoCA")
    public void testPolicyCancellationMidTerm() {
       
    	new TestPolicyBackdated().testPolicyBackdated();
		
		log.info("TEST: MidTerm Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
    	
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
