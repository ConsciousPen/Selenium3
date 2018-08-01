/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Dominykas Razgunas
 * @name Test Home SS Hurricane Deductible for specified zip codes
 * @scenario 1. Create new customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. Check That Hurricane Mandatory minimum is 1%.
 * 4. Change Hurricane Deductible to 5%. Save NB Value.
 * 5. Issue Policy.
 * 6. Endorse Policy.
 * 7. Check That Hurricane Deductible is 5%.
 * 8. Change Hurricane Deductible to 2%.
 * 9. Issue Endorsement.
 * 10. Renew Policy.
 * 11. Check That Hurricane Deductible is 2%.
 * 12. Change Hurricane Deductible to 1%.
 * 13. Calculate Premium.
 * 14. Check that Premium is calculated. P&C page is opened.
 * @details
 */
@StateList(states = Constants.States.MD)
public class TestMDHurricaneDeductible extends HomeSSDP3BaseTest {

	private String hurricaneDeductible = HomeSSMetaData.PremiumsAndCoveragesQuoteTab.HomeSSCoverages.HURRICANE_DEDUCTIBLE.get();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	@Parameters({"state"})
	@Test(groups = {Groups.SMOKE, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
	public void pas6907_testMDHurricaneDeductiblePrivilegedUser(@Optional("MD") String state) {

		// TestData for Home . Required Zip Code and Distance to coast for mandatory 1% Hurricane deductible
		TestData tdHome = getPolicyTD().adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), "21056")
				.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(),
						HomeSSMetaData.PropertyInfoTab.RISKMETER.getLabel(),
						HomeSSMetaData.PropertyInfoTab.Riskmeter.DISTANCE_TO_COAST_MILES.getLabel()), "1");

		// Open App initiate policy
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class, true);

		// Assert That Hurricane Deductible is 1% for this zip code and Distance to Coast  AC1 PAS-6907
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible).getValue().toString()).contains("1%");

		// Change and Save nb Hurricane Deductible Value
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible, ComboBox.class).setValueContains("5%");
		String nbValue = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(hurricaneDeductible).getValue().toString();

		// Purchase policy
		premiumsAndCoveragesQuoteTab.calculatePremium();
		premiumsAndCoveragesQuoteTab.submitTab();
		policy.getDefaultView().fillFromTo(tdHome, MortgageesTab.class, BindTab.class);
		bindTab.submitTab();
		purchaseTab.fillTab(tdHome);
		purchaseTab.submitTab();

		// Endorse Policy
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		// Make Sure NB Deductible Value is carried over
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible).getValue().toString()).contains(nbValue);
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible, ComboBox.class).setValueContains("2%");
		String endValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible).getValue().toString();

		// Bind Endorsement
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();

		// Renew Policy
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		// Make Sure NB Deductible Value is carried over
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible).getValue().toString()).contains(endValue);
		// Set the Hurricane Deductible Value to 1% and Calculate Premium. P&C page is opened.
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible, ComboBox.class).setValueContains("1%");
		premiumsAndCoveragesQuoteTab.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible)).isPresent();
	}
}
