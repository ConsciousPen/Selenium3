/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author
 * <b> Test Create CA Select Auto Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create CA Select Auto Policy
 * <p> 3. Verify Policy status is '	 Policy Active'
 * <p> 4. Inqury Policy and verify Policy Product is 'CA Select'
 *
 */
public class TestPolicyCreation extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testPolicyCreation(@Optional("CA") String state) {
		
		if (getUserGroup().equals(UserGroups.B31.get())) {
			mainApp().open(getLoginTD(UserGroups.QA));
			String customerNumber = createCustomerIndividual();
			mainApp().close();
			
			//re-login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			assertThat(SearchPage.buttonCreateCustomer).isDisabled();
			SearchPage.openCustomer(customerNumber);
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
			new QuoteSummaryPage().verifyProductDoesNotContainOption(PolicyType.AUTO_CA_SELECT);
		}
		else {
			mainApp().open();
			createCustomerIndividual();
			createPolicy();

			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
			log.info("CA Select Policy Product Verification Started...");
			policy.policyInquiry().start();
			NavigationPage.toViewTab(AutoCaTab.PREMIUM_AND_COVERAGES.get());

			if(getUserGroup().equals(UserGroups.F35.get())||getUserGroup().equals(UserGroups.G36.get())) {
				assertThat(PremiumAndCoveragesTab.labelProductMessageInquiry).valueContains("CA Select");
			}
			else {
				assertThat(PremiumAndCoveragesTab.labelProductInquiry).valueContains("CA Select");
			}
		}
	}
}
