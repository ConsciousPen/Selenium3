/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_select;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Xiaolan Ge
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

		log.info("Policy Creation Started...");

		//adjust default policy data with
		//1. effective date = today minus 10 days
		//2. error tab: "Policy cannot be backdated" error should be overridden 
		TestData td = getPolicyTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab",
						AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
						"/today-10d:MM/dd/yyyy")
				.adjust(getPolicyTD(this.getClass().getSimpleName(), "TestData").resolveLinks());

		getPolicyType().get().createPolicy(td);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		PolicySummaryPage.labelPolicyEffectiveDate.verify
				.contains(td.getTestData("GeneralTab", AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel()).getValue(AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));

	}
}
