/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

/**
 * @author YongGang Sun
 * @name Test backdated policy
 * @scenario
 * 1. Create new quote
 * 2. Set Quote effective date < current date
 * 3. Check that appropriate rule about backdated Policy is fired
 * 4. Override rule and bind policy
 * @details
 */
public class TestPolicyBackdated extends PersonalUmbrellaBaseTest {

	@Test
	@TestInfo(component = "Policy.PUP")
	public void testPolicyBackdated() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with
		//1. effective date = today minus 2 days
		//2. error tab: "Requested Effective Date not Available" error should be overridden 
        TestData tdPolicyCreation = getBackDatedPolicyTD();
        tdPolicyCreation = adjustWithRealPolicies(tdPolicyCreation, getPrimaryPoliciesForPup(getTestSpecificTD("TestData_Home").resolveLinks(), getTestSpecificTD("TestData_Auto")));
        createPolicy(tdPolicyCreation);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

}
