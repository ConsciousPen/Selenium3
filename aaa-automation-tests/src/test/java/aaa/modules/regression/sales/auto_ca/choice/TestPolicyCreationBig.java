/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.choice;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author
 * <b> Test Create CA Choice Auto Policy with 2 NIs/Drivers/Vehicles </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create CA Choice Auto Policy
 * <p> 3. Verify Policy status is 'Policy Active'
 * <p> 4. Inqury Policy and verify Policy Product is 'CA Choice'
 *
 */
public class TestPolicyCreationBig extends AutoCaChoiceBaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
	public void testPolicyCreationBig(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");
		
        TestData tdPolicyBig = getTestSpecificTD("TestData");
		getPolicyType().get().createPolicy(tdPolicyBig);

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		log.info("CA Select Policy Product Verification Started...");
		policy.policyInquiry().start();
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());
		assertThat(PremiumAndCoveragesTab.labelProductInquiry).valueContains("CA Choice");
		
		Tab.buttonCancel.click();
	}
}
