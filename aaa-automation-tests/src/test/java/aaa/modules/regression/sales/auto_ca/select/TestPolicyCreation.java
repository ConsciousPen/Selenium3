/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author 
 * @name Test Create CA Select Auto Policy
 * @scenario 
 * 1. Create Customer 
 * 2. Create CA Select Auto Policy
 * 3. Verify Policy status is '	 Policy Active'
 * 4. Inqury Policy and verify Policy Product is 'CA Select'
 * @details
 */
public class TestPolicyCreation extends AutoCaSelectBaseTest {

	@Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyCreation() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();
		//policy.createPolicy(getPolicyTD("DataGather", "TestData"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		log.info("CA Select Policy Product Verification Started...");
		policy.policyInquiry().start();
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.labelProductInquiry.verify.contains("CA Select");
		
	}
}
