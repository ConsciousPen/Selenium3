/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_select;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author amitjukovs
 * @name Test Backdated Auto CA Select policy creation
 * @scenario
 * 1. Create Customer
 * 2. Create Backdated Auto CA Select policy
 * 3. Verify Policy status is 'Policy Active'
 * 4. Verify Policy is backdated
 * @details
 */
public class TestPolicyBackdated extends AutoCaSelectBaseTest {

	@Test
	@TestInfo(component = "Policy.AutoCA")
	public void testPolicyBackdated() {
		mainApp().open();

        createCustomerIndividual();


        TestData backdated_td = getStateTestData(tdPolicy, "DataGather", "TestData")
                .adjust(TestData.makeKeyPath("GeneralTab","PolicyInformation","Effective Date"), "/today-10d:MM/dd/yyyy");

		createPolicy(backdated_td);
		//policy.createPolicy(tdPolicy.getTestData("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

//        PolicySummaryPage.labelPolicyEffectiveDate.verify.value();
		
	}
}
