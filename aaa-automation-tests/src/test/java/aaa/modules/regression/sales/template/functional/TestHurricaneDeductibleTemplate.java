package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestHurricaneDeductibleTemplate extends PolicyBaseTest {

	private String hurricaneDeductible = HomeSSMetaData.PremiumsAndCoveragesQuoteTab.HomeSSCoverages.HURRICANE_DEDUCTIBLE.get();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();
	private PurchaseTab purchaseTab = new PurchaseTab();
	private ErrorTab errorTab = new ErrorTab();

	protected void pas6907_testMDHurricaneDeductible() {

		// TestData for Home . Required Zip Code and Distance to coast for mandatory 1% Hurricane deductible
		TestData tdHome = getPolicyTD()
				.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(),
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), "21056")
				.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(),
						HomeSSMetaData.PropertyInfoTab.RISKMETER.getLabel(),
						HomeSSMetaData.PropertyInfoTab.Riskmeter.DISTANCE_TO_COAST_MILES.getLabel()), "1")
				.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(),
						HomeSSMetaData.PropertyInfoTab.RISKMETER.getLabel(),
						HomeSSMetaData.PropertyInfoTab.Riskmeter.ELEVATION_FEET.getLabel()), "50");

		// Open App initiate policy
		createQuoteAndFillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class);

		// Assert That Hurricane Deductible is 1% for this zip code and Distance to Coast  AC1 PAS-6907
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible).getValue().toString()).contains("1%");

		// Change and Save nb Hurricane Deductible Value
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible, ComboBox.class).setValueContains("5%");
		String nbValue = premiumsAndCoveragesQuoteTab.getAssetList().getAsset(hurricaneDeductible).getValue().toString();

		// Purchase policy
		premiumsAndCoveragesQuoteTab.calculatePremium();
		premiumsAndCoveragesQuoteTab.submitTab();
		policy.getDefaultView().fillFromTo(tdHome, MortgageesTab.class, BindTab.class);
		bindTab.submitTab();
		if(errorTab.isVisible()){
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_HO_SS6260765);
			errorTab.override();
			bindTab.submitTab();
		}
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
