/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * <b> Test Create HSS Policy HO3-Full </b>
 * <p> Steps:
 * <p> 1. Create new or open existed customer.
 * <p> 2. Initiate HSS quote creation.
 * <p> 3. Fill all fields on all tabs, order reports.
 * <p> 4. Add HS 04 92 endorsement form
 * <p> (after form HS 04 92 added, should go back to Reports tab and order PPC report for added in form address).
 * <p> 5. Navigate to Premiums&Coverages Quote tab and calculate premium.
 * <p> 6. Fill the rest of tabs.
 * <p> 7. Purchase policy.
 * <p> 8. Verify policy status is Active on Consolidated policy view.
 *
 */
public class TestPolicyCreationFull extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testPolicyCreationFull(@Optional("") String state) {
		mainApp().open();

		TestData td = getTestSpecificTD("TestData");
		TestData td_orderPPC = getTestSpecificTD("TestData_OrderPPC");

		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, false);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		ReportsTab reportsTab = new ReportsTab();
		reportsTab.fillTab(td_orderPPC);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		policy.getDefaultView().fillFromTo(td, PremiumsAndCoveragesQuoteTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: HSS Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}
}
