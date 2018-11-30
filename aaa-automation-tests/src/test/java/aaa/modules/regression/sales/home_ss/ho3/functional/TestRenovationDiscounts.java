/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3.functional;

import static aaa.common.enums.NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES;
import static aaa.common.enums.NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestRenovationDiscounts extends HomeSSHO3BaseTest {

	private final PremiumsAndCoveragesQuoteTab premium = new PremiumsAndCoveragesQuoteTab();

	/**
	 * @author Dominykas Razgunas
	 * @name Safe Home (Renovation) Discount during renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create KS Home HO3 policy with plumbing, electricity, roof, heating/cooling renovations
	 * 3. Calculate Premium and check that the discount is applied
	 * 3. Renew Policy
	 * 4. Calculate Premium and Check that the discount is applied
	 * @details
	 */
	@StateList(states = Constants.States.KS)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-4317")
	public void pas4317_RenovationDiscounts(@Optional("KS") String state) {
		String expectedValue = "Safe Home";
		String infoTabName = new PropertyInfoTab().getMetaKey();
		String docTabName = new DocumentsTab().getMetaKey();

		mainApp().open();
		createCustomerIndividual();

		// Prepare testdata for policy to be eligible for the discount
		TestData testData = getPolicyTD();

		TestData propertyInfo = testData.getTestData(infoTabName);
		propertyInfo.adjust(getTestSpecificTD("TestData").getTestData(infoTabName));
		testData.adjust(infoTabName, propertyInfo);

		TestData documents = testData.getTestData(docTabName);
		documents.adjust(getTestSpecificTD("TestData").getTestData(docTabName));
		testData.adjust(docTabName, documents);

		//Initiate policy and check if the discount is applied
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, PremiumsAndCoveragesQuoteTab.class, true);

		assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getColumn(1).getValue()).contains(expectedValue);

		// Issue policy
		premium.submitTab();
		policy.getDefaultView().fillFromTo(testData, MortgageesTab.class, PurchaseTab.class, true);
		new PurchaseTab().submitTab();

		// Initiate renewal
		policy.renew().start().submit();

		NavigationPage.toViewTab(PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(PREMIUMS_AND_COVERAGES_QUOTE.get());

		// Calculate premium and check that the discount is still applied
		premium.calculatePremium();
		assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getColumn(1).getValue()).contains(expectedValue);

	}
}


