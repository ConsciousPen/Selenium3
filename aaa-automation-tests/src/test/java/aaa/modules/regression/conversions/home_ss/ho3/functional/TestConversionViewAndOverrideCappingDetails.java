package aaa.modules.regression.conversions.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ProductOfferingTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.Button;


public class TestConversionViewAndOverrideCappingDetails extends HomeSSHO3BaseTest {

	PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	ProductOfferingTab productOfferingTab = new ProductOfferingTab();

	/**
	 * @author R. Kazlauskiene
	 * @name Test View And Override Capping Details
	 * @scenario
	 * Preconditions: policy qualifies for capping and user have the capping privilege
	 * 1. Create Individual Customer / Account
	 * 2. Create converted SS home policy
	 * 3. On the Quote tab of the "Premium & Coverages" page click Calculate Premium button
	 * 4. Select the "View capping details" link
	 * 5. Check Capping details
	 * 6. Override Capping Factor
	 * 7. Click Calculate and Save and Return to Premium & Coverages buttons
	 * 8. Click Calculate Premium and then "View capping details" link
	 * 9. Check Capping Factor and Capped Term Premium values
	 *
	 **/
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.DE, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.UT})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = "PAS-3002")
	public void testPolicyViewCappingDetails(@Optional("VA") String state) {

		TestData td = initTestData();
		TestData initiateRenewalEntry = initInitiateRenewalEntry();

		String renewalTermPremiumOld = getTestSpecificTD("TestData").getTestData
				("InitiateRenewalEntryActionTab").getValue("Renewal Policy Premium");

		mainApp().open();
		createCustomerIndividual();

		//Initiate Renewal manual entry
		customer.initiateRenewalEntry().perform(initiateRenewalEntry);

		//Fill Quote
		policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);

		//Check that coverages are rounded to dollar value
		List<String> currentValues = new ArrayList<>();
		currentValues.addAll(Arrays.asList(PremiumsAndCoveragesQuoteTab.tableCoverages.getRow(2).getCell("Percentage of Coverage A").getValue()));
		for(String value : currentValues.toString().split("\n")) {
			assertThat(value).as("Coverages should be rounded to dollar value").contains(".00");
		}
		String ceilingCap = PolicyHelper.getCeilingByPolicyNumber(premiumsAndCoveragesQuoteTab.getPolicyNumberForConversion());
		String floorCap = PolicyHelper.getFloorByPolicyNumber(premiumsAndCoveragesQuoteTab.getPolicyNumberForConversion());
		//View Capping Details
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		String calculatedTermPremium = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CALCULATED_TERM_PREMIUM);
		String cappedTermPremium = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CAPPED_TERM_PREMIUM);
		//Check Capping Details
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.RENEWAL_TERM_PREMIUM_OLD_RATER))
				.isEqualTo(new Dollar(renewalTermPremiumOld).toString());
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CHANGE_IN_POLICY_PREMIUM))
				.isEqualTo(PolicyHelper.calculateChangeInPolicyPremium(calculatedTermPremium, renewalTermPremiumOld));
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CEILING_CAP)).isEqualTo(String.format("%s.0%%", ceilingCap));
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.FLOOR_CAP)).isEqualTo(String.format("%s.0%%", floorCap));
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.SYSTEM_CALCULATED_CAPPING_FACTOR))
				.isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, floorCap));
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.APPLIED_CAPPING_FACTOR))
				.isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, floorCap));
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.PROGRAM_CODE)).isEqualTo("LegacyConv");
		//Override Capping Factor
		overrideCappingFactor(premiumsAndCoveragesQuoteTab);

		premiumsAndCoveragesQuoteTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.APPLIED_CAPPING_FACTOR))
				.isEqualTo(String.format("%s.00%%", getTestSpecificTD("TestData_OverideCappingDetails").getTestData
						("PremiumsAndCoveragesQuoteTab", "View Capping Details").getValue(PolicyConstants.ViewCappingDetailsTable.MANUAL_CAPPING_FACTOR)));
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CAPPED_TERM_PREMIUM)).isNotEqualTo(cappedTermPremium);
	}

/**
 * @author R. Kazlauskiene
 * @name Test check Capping Factor
 * @scenario
 * Preconditions: policy qualifies for capping and user have the capping privilege
 * 1. Create Individual Customer / Account
 * 2. Create converted SS home policy
 * 3. On the Quote tab of the "Premium & Coverages" page click Calculate Premium button
 * 4. Select the "View capping details" link
 * 5. Check Capping Factor
 * 6. Navigate to Product Offering tab and change variations
 * 7. Calculate Quote again from Quote tab
 * 7. Check Capping factor
 * **/
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.DE, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.UT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = "PAS-9350")
	public void testPolicyCheckCappingFactor(@Optional("VA") String state) {

		TestData td = initTestData();
		TestData initiateRenewalEntry = initInitiateRenewalEntry();

		mainApp().open();
		createCustomerIndividual();

		//Initiate Renewal manual entry
		customer.initiateRenewalEntry().perform(initiateRenewalEntry);

		//Fill Quote
		policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
		//View Capping Factor
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		String cappingFactor = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor");

		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.VIEW_CAPPING_DETAILS_DIALOG).getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.ViewCappingDetailsDialog.BUTTON_TO_PREMIUM_AND_COVERAGES.getLabel(), Button.class).click();

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());

		productOfferingTab.buttonSelectLegacy.click();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor"))
				.isEqualTo(cappingFactor);
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("System Calculated Capping Factor"))
				.isEqualTo(cappingFactor);

		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.VIEW_CAPPING_DETAILS_DIALOG).getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.ViewCappingDetailsDialog.BUTTON_TO_PREMIUM_AND_COVERAGES.getLabel(),Button.class).click();

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());

		productOfferingTab.buttonSelectPrestige.click();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor"))
				.isEqualTo(cappingFactor);
		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("System Calculated Capping Factor"))
				.isEqualTo(cappingFactor);

		//Override Capping Factor
		overrideCappingFactor(premiumsAndCoveragesQuoteTab);

		//View Capping Factor
		premiumsAndCoveragesQuoteTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		String cappingFactorAfterOverride = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor");

		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.VIEW_CAPPING_DETAILS_DIALOG).getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.ViewCappingDetailsDialog.BUTTON_TO_PREMIUM_AND_COVERAGES.getLabel(), Button.class).click();

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());

		productOfferingTab.buttonSelectLegacy.click();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor"))
				.isEqualTo(cappingFactorAfterOverride);

		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.VIEW_CAPPING_DETAILS_DIALOG).getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.ViewCappingDetailsDialog.BUTTON_TO_PREMIUM_AND_COVERAGES.getLabel(),Button.class).click();

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());

		productOfferingTab.buttonSelectPrestige.click();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
		PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

		assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey("Applied Capping Factor"))
				.isEqualTo(cappingFactorAfterOverride);
	}

	/**
	 * @author Parth Varmora
	 * @name Test Check Capping Lock Indicator
	 * @scenario
	 * Preconditions: policy qualifies for capping and user have the capping privilege
	 * 1. Create Individual Customer / Account
	 * 2. Create converted SS home policy
	 * 3. navigate to the “Premiums & Coverages” – “Product offering” tab
	 * 4. Click the “Calculate Premiums’ button.
	 * 5. Assert that Capping Lock is disabled.
	 * 6. Assert that Capping Lock does not get selected.
	 * 7. navigate to the “Premiums & Coverages” – “Quote” tab
	 * 8. Click the “Calculate Premiums’ button.
	 * 9. navigate to the “Premiums & Coverages” – “Product offering” tab
	 * 10. Assert that Capping Lock is disabled.
	 * 11. Assert that Capping Lock get selected.
	 *
	 **/
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.DE, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.UT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = "PAS-8847")

	public void testCappingLockNotSelected(@Optional("VA") String state) {

		TestData td = initTestData();
		TestData initiateRenewalEntry = initInitiateRenewalEntry();

		mainApp().open();

		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(initiateRenewalEntry);

		policy.getDefaultView().fillUpTo(td, ProductOfferingTab.class, true);
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());

		productOfferingTab.btnCalculatePremium.click();
		assertThat(productOfferingTab.getAssetList().getAsset(HomeSSMetaData.ProductOfferingTab.CAPPING_LOCK))
				.isEnabled(false);
		assertThat(productOfferingTab.getAssetList().getAsset(HomeSSMetaData.ProductOfferingTab.CAPPING_LOCK)
				.getWebElement().isSelected()).isFalse();

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_PRODUCT_OFFERING.get());

		assertThat(productOfferingTab.getAssetList().getAsset(HomeSSMetaData.ProductOfferingTab.CAPPING_LOCK))
				.isEnabled(false);
		assertThat(productOfferingTab.getAssetList().getAsset(HomeSSMetaData.ProductOfferingTab.CAPPING_LOCK)
				.getWebElement().isSelected()).isTrue();
	}

	private TestData initInitiateRenewalEntry() {
		return getManualConversionInitiationTd()
					.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
							CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_POLICY_PREMIUM.getLabel()),
							getTestSpecificTD("TestData").getTestData("InitiateRenewalEntryActionTab")
									.getValue("Renewal Policy Premium"));
	}

	private TestData initTestData() {
		return getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(),
				HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()), getTestSpecificTD("TestData")
				.getTestData("GeneralTab").getValue("Immediate prior carrier"));
	}

	private void overrideCappingFactor(PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab) {
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab
				.VIEW_CAPPING_DETAILS_DIALOG).fill(getTestSpecificTD("TestData_OverideCappingDetails")
				.getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName()), false);
	}
}
