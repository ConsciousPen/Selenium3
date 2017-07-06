/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_choice;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author 
 * @name Test Create CA Choice Auto Policy with 2 NIs/Drivers/Vehicles
 * @scenario 
 * 1. Create Customer 
 * 2. Create CA Choice Auto Policy
 * 3. Verify Policy status is 'Policy Active'
 * 4. Inqury Policy and verify Policy Product is 'CA Choice'
 * @details
 */
public class TestPolicyCreationBig extends AutoCaChoiceBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testQuoteCreation() {
		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");
		
        TestData tdPolicyBig = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
		getPolicyType().get().createPolicy(tdPolicyBig);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		log.info("CA Select Policy Product Verification Started...");
		policy.policyInquiry().start();
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.labelProductInquiry.verify.contains("CA Choice");
		
	}
}
