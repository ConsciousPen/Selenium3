/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test backdated policy
 * @scenario
 * 1. Create new quote
 * 2. Set Quote effective date < current date
 * 3. Check that appropriate rule about backdated Policy is fired
 * 4. Override rule and bind policy
 * @details
 */
public class TestPolicyBackdated extends AutoSSBaseTest {

	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testPolicyBackdated() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with
		//1. effective date = today minus 10 days
		//2. error tab: "Requested Effective Date not Available" error should be overridden 
		TestData td = getPolicyTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab",
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
						"/today-10d:MM/dd/yyyy")
				.adjust(getPolicyTD(this.getClass().getSimpleName(), "TestData").resolveLinks());

		getPolicyType().get().createPolicy(td);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		PolicySummaryPage.labelPolicyEffectiveDate.verify
				.contains(td.getTestData("GeneralTab", AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel()).getValue(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));

	}

}
