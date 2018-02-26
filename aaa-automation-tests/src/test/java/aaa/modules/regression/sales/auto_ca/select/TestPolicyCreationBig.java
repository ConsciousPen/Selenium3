/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author 
 * @name Test Create CA Select Auto Policy with 2 NIs/Drivers/Vehicles
 * @scenario 
 * 1. Create Customer 
 * 2. Create CA Select Auto Policy
 * 3. Verify Policy status is 'Policy Active'
 * 4. Inqury Policy and verify Policy Product is 'CA Select'
 * @details
 */
public class TestPolicyCreationBig extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyCreationBig(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");
		
        TestData tdPolicyBig = getTestSpecificTD("TestData");
		getPolicyType().get().createPolicy(tdPolicyBig);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}
}
