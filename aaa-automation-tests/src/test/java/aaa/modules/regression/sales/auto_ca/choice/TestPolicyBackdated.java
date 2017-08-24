/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.choice;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
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
public class TestPolicyBackdated extends AutoCaChoiceBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
	public void testPolicyBackdated() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with
		//1. effective date = today minus 10 days
		//2. error tab: "Requested Effective Date not Available" error should be overridden 
		TestData td = getPolicyTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath("GeneralTab",
						AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
						"/today-10d:MM/dd/yyyy")
				.adjust(getTestSpecificTD("TestData").resolveLinks());

		getPolicyType().get().createPolicy(td);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		PolicySummaryPage.labelPolicyEffectiveDate.verify
				.contains(td.getTestData("GeneralTab", AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel()).getValue(AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()));


	}
}