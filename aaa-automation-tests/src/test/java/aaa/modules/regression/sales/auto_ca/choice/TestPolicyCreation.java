/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.choice;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
import toolkit.utils.TestInfo;

/**
 * @author N. Belakova
 * @name Test Create CA Choice Auto Policy
 * @scenario
 * 1. Create Customer 
 * 2. Create CA Choice Auto Policy
 * 3. Verify Policy status is '	 Policy Active'
 * 4. Inquiry Policy and verify Policy Product is 'CA Choice'
 * @details
 */
public class TestPolicyCreation extends AutoCaChoiceBaseTest {

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
	public void testPolicyCreation(@Optional("CA") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
		log.info("CA Choice Policy Product Verification Started...");
		policy.policyInquiry().start();
		NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());

		assertThat(PremiumAndCoveragesTab.labelProductInquiry).valueContains("CA Choice");
	}
}
