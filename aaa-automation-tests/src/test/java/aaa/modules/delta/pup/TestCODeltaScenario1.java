package aaa.modules.delta.pup;

import java.util.ArrayList;
import java.util.Map;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksOtherVehiclesTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksPropertyTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class TestCODeltaScenario1 extends PersonalUmbrellaBaseTest {
	
	private String quoteNumber;
	private String policyNumber;

	@Test
	public void pupDeltaSC1_TC01() {
		mainApp().open();
        createCustomerIndividual();

        Map<String, String> primaryPolicies = getPrimaryPoliciesForPup();
        TestData tdPolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
        PrefillTab prefillTab = policy.getDefaultView().getTab(PrefillTab.class);
        tdPolicy = prefillTab.adjustWithRealPolicies(tdPolicy, primaryPolicies);
        quoteNumber = createQuote(tdPolicy);
		
       //TODO add verification of On-Demand Documents Tab	
//		policy.quoteDocGen().start();
	}

	@Test
	public void pupDeltaSC1_TC02() {
		mainApp().open();
		CustomAssert.enableSoftMode();
		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();

		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		UnderlyingRisksPropertyTab.tableAdditionalResidences.getRow(2).getCell(9).controls.links.get("View/Edit").click();
		UnderlyingRisksPropertyTab propertyTab = policy.getDefaultView().getTab(UnderlyingRisksPropertyTab.class);
		propertyTab.getAdditionalResidenciesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(residence_CurrentCarrierLOVs);

		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		UnderlyingRisksAutoTab.tableAutomobiles.getRow(1).getCell(9).controls.links.get("View/Edit").click();
		UnderlyingRisksAutoTab autoTab = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);
		autoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(auto_CurrentCarrierLOVs);

		UnderlyingRisksAutoTab.tableAutomobiles.getRow(2).getCell(9).controls.links.get("View/Edit").click();
		autoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(antq_CurrentCarrierLOVs);

		autoTab.getMotorcyclesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(motorcycle_CurrentCarrierLOVs);
		autoTab.getMotorHomesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(motorhome_CurrentCarrierLOVs);

		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
		UnderlyingRisksOtherVehiclesTab otherVehiclesTab = policy.getDefaultView().getTab(UnderlyingRisksOtherVehiclesTab.class);
		otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(recrVehicle_CurrentCarrierLOVs);

		otherVehiclesTab.getWatercraftAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(watercraft_CurrentCarrierLOVs);
		Tab.buttonSaveAndExit.click();
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test
	public void pupDeltaSC1_TC03() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();

		NavigationPage.toViewTab(PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
		PremiumAndCoveragesQuoteTab premiumQuoteTab = policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class);
		premiumQuoteTab.calculatePremium();
		
		NavigationPage.toViewTab(PersonalUmbrellaTab.BIND.get());
		policy.getDefaultView().getTab(BindTab.class).submitTab();
		policy.getDefaultView().getTab(PurchaseTab.class).fillTab(getPolicyTD()).submitTab();
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("============================================");
		log.info(getState() + " Policy PUP SC1 is created: " + policyNumber);
		log.info("============================================");
	}

	@Test
	public void pupDeltaSC1_TC04() throws Exception {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		//TODO add verification of On-Demand Documents Tab	
//		policy.quoteDocGen().start();
	}
	
	private static ArrayList<String> residence_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		residence_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		residence_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		residence_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		residence_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		residence_CurrentCarrierLOVs.add("Allied");
		residence_CurrentCarrierLOVs.add("Allstate");
		residence_CurrentCarrierLOVs.add("Amco Ins Co");
		residence_CurrentCarrierLOVs.add("American Family");
		residence_CurrentCarrierLOVs.add("American National");
		residence_CurrentCarrierLOVs.add("Auto Owners");
		residence_CurrentCarrierLOVs.add("Bear River Mutual");
		residence_CurrentCarrierLOVs.add("Chartis");
		residence_CurrentCarrierLOVs.add("Cincinnati");
		residence_CurrentCarrierLOVs.add("Country");
		residence_CurrentCarrierLOVs.add("CSAA IG");
		residence_CurrentCarrierLOVs.add("CSE Safeguard");
		residence_CurrentCarrierLOVs.add("Farm Bureau");
		residence_CurrentCarrierLOVs.add("Farmers");
		residence_CurrentCarrierLOVs.add("Fire Insurance");
		residence_CurrentCarrierLOVs.add("First Time Homebuyer");
		residence_CurrentCarrierLOVs.add("Foremost");
		residence_CurrentCarrierLOVs.add("Great Northern");
		residence_CurrentCarrierLOVs.add("Hartford");
		residence_CurrentCarrierLOVs.add("Homesite");
		residence_CurrentCarrierLOVs.add("Liberty Mutual");
		residence_CurrentCarrierLOVs.add("Metropolitan");
		residence_CurrentCarrierLOVs.add("Nationwide");
		residence_CurrentCarrierLOVs.add("No Prior");
		residence_CurrentCarrierLOVs.add("Other Carrier");
		residence_CurrentCarrierLOVs.add("Owners Insurance");
		residence_CurrentCarrierLOVs.add("Pacific Indemnity");
		residence_CurrentCarrierLOVs.add("Safeco");
		residence_CurrentCarrierLOVs.add("Standard Fire");
		residence_CurrentCarrierLOVs.add("State Farm");
		residence_CurrentCarrierLOVs.add("Travelers");
		residence_CurrentCarrierLOVs.add("Unigard");
		residence_CurrentCarrierLOVs.add("USAA");
	}

	private static ArrayList<String> auto_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		auto_CurrentCarrierLOVs.add("");
		auto_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		auto_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		auto_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		auto_CurrentCarrierLOVs.add("21st Century");
		auto_CurrentCarrierLOVs.add("AIG");
		auto_CurrentCarrierLOVs.add("Allied");
		auto_CurrentCarrierLOVs.add("Allstate");
		auto_CurrentCarrierLOVs.add("American Family");
		auto_CurrentCarrierLOVs.add("American Modern");
		auto_CurrentCarrierLOVs.add("American National");
		auto_CurrentCarrierLOVs.add("AMEX");
		auto_CurrentCarrierLOVs.add("AMICA Mutual");
		auto_CurrentCarrierLOVs.add("CA Casualty");
		auto_CurrentCarrierLOVs.add("Charter Oak Fire");
		auto_CurrentCarrierLOVs.add("Chubb");
		auto_CurrentCarrierLOVs.add("Commerce West");
		auto_CurrentCarrierLOVs.add("Country");
		auto_CurrentCarrierLOVs.add("CSAA IG");
		auto_CurrentCarrierLOVs.add("Dairyland");
		auto_CurrentCarrierLOVs.add("Deerbrook");
		auto_CurrentCarrierLOVs.add("Encompass");
		auto_CurrentCarrierLOVs.add("Farm Bureau");
		auto_CurrentCarrierLOVs.add("Farmers");
		auto_CurrentCarrierLOVs.add("Foremost");
		auto_CurrentCarrierLOVs.add("GEICO");
		auto_CurrentCarrierLOVs.add("GMAC");
		auto_CurrentCarrierLOVs.add("Grange");
		auto_CurrentCarrierLOVs.add("Great American");
		auto_CurrentCarrierLOVs.add("Guaranty National");
		auto_CurrentCarrierLOVs.add("GuideOne");
		auto_CurrentCarrierLOVs.add("Hartford");
		auto_CurrentCarrierLOVs.add("Horace Mann");
		auto_CurrentCarrierLOVs.add("Infinity");
		auto_CurrentCarrierLOVs.add("Kemper");
		auto_CurrentCarrierLOVs.add("Leader");
		auto_CurrentCarrierLOVs.add("Liberty Mutual");
		auto_CurrentCarrierLOVs.add("Mercury");
		auto_CurrentCarrierLOVs.add("Met Life");
		auto_CurrentCarrierLOVs.add("Mid-Century");
		auto_CurrentCarrierLOVs.add("Nationwide");
		auto_CurrentCarrierLOVs.add("None");
		auto_CurrentCarrierLOVs.add("North Pacific");
		auto_CurrentCarrierLOVs.add("Omaha");
		auto_CurrentCarrierLOVs.add("Omni");
		auto_CurrentCarrierLOVs.add("Other Carrier");
		auto_CurrentCarrierLOVs.add("Progressive");
		auto_CurrentCarrierLOVs.add("Prudential");
		auto_CurrentCarrierLOVs.add("Regal");
		auto_CurrentCarrierLOVs.add("Safeco");
		auto_CurrentCarrierLOVs.add("Safeway Insurance Group");
		auto_CurrentCarrierLOVs.add("State Farm");
		auto_CurrentCarrierLOVs.add("Travelers");
		auto_CurrentCarrierLOVs.add("Unigard");
		auto_CurrentCarrierLOVs.add("Unitrin");
		auto_CurrentCarrierLOVs.add("USAA");
		auto_CurrentCarrierLOVs.add("Viking");
		auto_CurrentCarrierLOVs.add("Wawanesa");
		auto_CurrentCarrierLOVs.add("Windsor");
		auto_CurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> antq_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		antq_CurrentCarrierLOVs.add("");
		antq_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		antq_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		antq_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		antq_CurrentCarrierLOVs.add("21st Century");
		antq_CurrentCarrierLOVs.add("AIG");
		antq_CurrentCarrierLOVs.add("Allied");
		antq_CurrentCarrierLOVs.add("Allstate");
		antq_CurrentCarrierLOVs.add("American Family");
		antq_CurrentCarrierLOVs.add("American Modern");
		antq_CurrentCarrierLOVs.add("American National");
		antq_CurrentCarrierLOVs.add("AMEX");
		antq_CurrentCarrierLOVs.add("AMICA Mutual");
		antq_CurrentCarrierLOVs.add("CA Casualty");
		antq_CurrentCarrierLOVs.add("Charter Oak Fire");
		antq_CurrentCarrierLOVs.add("Chubb");
		antq_CurrentCarrierLOVs.add("Commerce West");
		antq_CurrentCarrierLOVs.add("Country");
		antq_CurrentCarrierLOVs.add("CSAA IG");
		antq_CurrentCarrierLOVs.add("Dairyland");
		antq_CurrentCarrierLOVs.add("Deerbrook");
		antq_CurrentCarrierLOVs.add("Encompass");
		antq_CurrentCarrierLOVs.add("Farm Bureau");
		antq_CurrentCarrierLOVs.add("Farmers");
		antq_CurrentCarrierLOVs.add("Foremost");
		antq_CurrentCarrierLOVs.add("GEICO");
		antq_CurrentCarrierLOVs.add("GMAC");
		antq_CurrentCarrierLOVs.add("Grange");
		antq_CurrentCarrierLOVs.add("Great American");
		antq_CurrentCarrierLOVs.add("Guaranty National");
		antq_CurrentCarrierLOVs.add("GuideOne");
		antq_CurrentCarrierLOVs.add("Hartford");
		antq_CurrentCarrierLOVs.add("Horace Mann");
		antq_CurrentCarrierLOVs.add("Infinity");
		antq_CurrentCarrierLOVs.add("Kemper");
		antq_CurrentCarrierLOVs.add("Leader");
		antq_CurrentCarrierLOVs.add("Liberty Mutual");
		antq_CurrentCarrierLOVs.add("Mercury");
		antq_CurrentCarrierLOVs.add("Met Life");
		antq_CurrentCarrierLOVs.add("Mid-Century");
		antq_CurrentCarrierLOVs.add("Nationwide");
		antq_CurrentCarrierLOVs.add("None");
		antq_CurrentCarrierLOVs.add("North Pacific");
		antq_CurrentCarrierLOVs.add("Omaha");
		antq_CurrentCarrierLOVs.add("Omni");
		antq_CurrentCarrierLOVs.add("Other Carrier");
		antq_CurrentCarrierLOVs.add("Progressive");
		antq_CurrentCarrierLOVs.add("Prudential");
		antq_CurrentCarrierLOVs.add("Regal");
		antq_CurrentCarrierLOVs.add("Safeco");
		antq_CurrentCarrierLOVs.add("Safeway Insurance Group");
		antq_CurrentCarrierLOVs.add("State Farm");
		antq_CurrentCarrierLOVs.add("Travelers");
		antq_CurrentCarrierLOVs.add("Unigard");
		antq_CurrentCarrierLOVs.add("Unitrin");
		antq_CurrentCarrierLOVs.add("USAA");
		antq_CurrentCarrierLOVs.add("Viking");
		antq_CurrentCarrierLOVs.add("Wawanesa");
		antq_CurrentCarrierLOVs.add("Windsor");
		antq_CurrentCarrierLOVs.add("Workmens Auto");

	}

	private static ArrayList<String> motorcycle_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		motorcycle_CurrentCarrierLOVs.add("");
		motorcycle_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		motorcycle_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		motorcycle_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		motorcycle_CurrentCarrierLOVs.add("21st Century");
		motorcycle_CurrentCarrierLOVs.add("AIG");
		motorcycle_CurrentCarrierLOVs.add("Allied");
		motorcycle_CurrentCarrierLOVs.add("Allstate");
		motorcycle_CurrentCarrierLOVs.add("American Family");
		motorcycle_CurrentCarrierLOVs.add("American Modern");
		motorcycle_CurrentCarrierLOVs.add("American National");
		motorcycle_CurrentCarrierLOVs.add("AMEX");
		motorcycle_CurrentCarrierLOVs.add("AMICA Mutual");
		motorcycle_CurrentCarrierLOVs.add("CA Casualty");
		motorcycle_CurrentCarrierLOVs.add("Charter Oak Fire");
		motorcycle_CurrentCarrierLOVs.add("Chubb");
		motorcycle_CurrentCarrierLOVs.add("Commerce West");
		motorcycle_CurrentCarrierLOVs.add("Country");
		motorcycle_CurrentCarrierLOVs.add("CSAA IG");
		motorcycle_CurrentCarrierLOVs.add("Dairyland");
		motorcycle_CurrentCarrierLOVs.add("Deerbrook");
		motorcycle_CurrentCarrierLOVs.add("Encompass");
		motorcycle_CurrentCarrierLOVs.add("Farm Bureau");
		motorcycle_CurrentCarrierLOVs.add("Farmers");
		motorcycle_CurrentCarrierLOVs.add("Foremost");
		motorcycle_CurrentCarrierLOVs.add("GEICO");
		motorcycle_CurrentCarrierLOVs.add("GMAC");
		motorcycle_CurrentCarrierLOVs.add("Grange");
		motorcycle_CurrentCarrierLOVs.add("Great American");
		motorcycle_CurrentCarrierLOVs.add("Guaranty National");
		motorcycle_CurrentCarrierLOVs.add("GuideOne");
		motorcycle_CurrentCarrierLOVs.add("Hartford");
		motorcycle_CurrentCarrierLOVs.add("Horace Mann");
		motorcycle_CurrentCarrierLOVs.add("Infinity");
		motorcycle_CurrentCarrierLOVs.add("Kemper");
		motorcycle_CurrentCarrierLOVs.add("Leader");
		motorcycle_CurrentCarrierLOVs.add("Liberty Mutual");
		motorcycle_CurrentCarrierLOVs.add("Mercury");
		motorcycle_CurrentCarrierLOVs.add("Met Life");
		motorcycle_CurrentCarrierLOVs.add("Mid-Century");
		motorcycle_CurrentCarrierLOVs.add("Nationwide");
		motorcycle_CurrentCarrierLOVs.add("None");
		motorcycle_CurrentCarrierLOVs.add("North Pacific");
		motorcycle_CurrentCarrierLOVs.add("Omaha");
		motorcycle_CurrentCarrierLOVs.add("Omni");
		motorcycle_CurrentCarrierLOVs.add("Other Carrier");
		motorcycle_CurrentCarrierLOVs.add("Progressive");
		motorcycle_CurrentCarrierLOVs.add("Prudential");
		motorcycle_CurrentCarrierLOVs.add("Regal");
		motorcycle_CurrentCarrierLOVs.add("Safeco");
		motorcycle_CurrentCarrierLOVs.add("Safeway Insurance Group");
		motorcycle_CurrentCarrierLOVs.add("State Farm");
		motorcycle_CurrentCarrierLOVs.add("Travelers");
		motorcycle_CurrentCarrierLOVs.add("Unigard");
		motorcycle_CurrentCarrierLOVs.add("Unitrin");
		motorcycle_CurrentCarrierLOVs.add("USAA");
		motorcycle_CurrentCarrierLOVs.add("Viking");
		motorcycle_CurrentCarrierLOVs.add("Wawanesa");
		motorcycle_CurrentCarrierLOVs.add("Windsor");
		motorcycle_CurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> motorhome_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		motorhome_CurrentCarrierLOVs.add("");
		motorhome_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		motorhome_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		motorhome_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		motorhome_CurrentCarrierLOVs.add("21st Century");
		motorhome_CurrentCarrierLOVs.add("AIG");
		motorhome_CurrentCarrierLOVs.add("Allied");
		motorhome_CurrentCarrierLOVs.add("Allstate");
		motorhome_CurrentCarrierLOVs.add("American Family");
		motorhome_CurrentCarrierLOVs.add("American Modern");
		motorhome_CurrentCarrierLOVs.add("American National");
		motorhome_CurrentCarrierLOVs.add("AMEX");
		motorhome_CurrentCarrierLOVs.add("AMICA Mutual");
		motorhome_CurrentCarrierLOVs.add("CA Casualty");
		motorhome_CurrentCarrierLOVs.add("Charter Oak Fire");
		motorhome_CurrentCarrierLOVs.add("Chubb");
		motorhome_CurrentCarrierLOVs.add("Commerce West");
		motorhome_CurrentCarrierLOVs.add("Country");
		motorhome_CurrentCarrierLOVs.add("CSAA IG");
		motorhome_CurrentCarrierLOVs.add("Dairyland");
		motorhome_CurrentCarrierLOVs.add("Deerbrook");
		motorhome_CurrentCarrierLOVs.add("Encompass");
		motorhome_CurrentCarrierLOVs.add("Farm Bureau");
		motorhome_CurrentCarrierLOVs.add("Farmers");
		motorhome_CurrentCarrierLOVs.add("Foremost");
		motorhome_CurrentCarrierLOVs.add("GEICO");
		motorhome_CurrentCarrierLOVs.add("GMAC");
		motorhome_CurrentCarrierLOVs.add("Grange");
		motorhome_CurrentCarrierLOVs.add("Great American");
		motorhome_CurrentCarrierLOVs.add("Guaranty National");
		motorhome_CurrentCarrierLOVs.add("GuideOne");
		motorhome_CurrentCarrierLOVs.add("Hartford");
		motorhome_CurrentCarrierLOVs.add("Horace Mann");
		motorhome_CurrentCarrierLOVs.add("Infinity");
		motorhome_CurrentCarrierLOVs.add("Kemper");
		motorhome_CurrentCarrierLOVs.add("Leader");
		motorhome_CurrentCarrierLOVs.add("Liberty Mutual");
		motorhome_CurrentCarrierLOVs.add("Mercury");
		motorhome_CurrentCarrierLOVs.add("Met Life");
		motorhome_CurrentCarrierLOVs.add("Mid-Century");
		motorhome_CurrentCarrierLOVs.add("Nationwide");
		motorhome_CurrentCarrierLOVs.add("None");
		motorhome_CurrentCarrierLOVs.add("North Pacific");
		motorhome_CurrentCarrierLOVs.add("Omaha");
		motorhome_CurrentCarrierLOVs.add("Omni");
		motorhome_CurrentCarrierLOVs.add("Other Carrier");
		motorhome_CurrentCarrierLOVs.add("Progressive");
		motorhome_CurrentCarrierLOVs.add("Prudential");
		motorhome_CurrentCarrierLOVs.add("Regal");
		motorhome_CurrentCarrierLOVs.add("Safeco");
		motorhome_CurrentCarrierLOVs.add("Safeway Insurance Group");
		motorhome_CurrentCarrierLOVs.add("State Farm");
		motorhome_CurrentCarrierLOVs.add("Travelers");
		motorhome_CurrentCarrierLOVs.add("Unigard");
		motorhome_CurrentCarrierLOVs.add("Unitrin");
		motorhome_CurrentCarrierLOVs.add("USAA");
		motorhome_CurrentCarrierLOVs.add("Viking");
		motorhome_CurrentCarrierLOVs.add("Wawanesa");
		motorhome_CurrentCarrierLOVs.add("Windsor");
		motorhome_CurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> watercraft_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		watercraft_CurrentCarrierLOVs.add("");
		watercraft_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		watercraft_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		watercraft_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		watercraft_CurrentCarrierLOVs.add("21st Century");
		watercraft_CurrentCarrierLOVs.add("AIG");
		watercraft_CurrentCarrierLOVs.add("Allied");
		watercraft_CurrentCarrierLOVs.add("Allstate");
		watercraft_CurrentCarrierLOVs.add("American Family");
		watercraft_CurrentCarrierLOVs.add("American Modern");
		watercraft_CurrentCarrierLOVs.add("American National");
		watercraft_CurrentCarrierLOVs.add("AMEX");
		watercraft_CurrentCarrierLOVs.add("AMICA Mutual");
		watercraft_CurrentCarrierLOVs.add("CA Casualty");
		watercraft_CurrentCarrierLOVs.add("Charter Oak Fire");
		watercraft_CurrentCarrierLOVs.add("Chubb");
		watercraft_CurrentCarrierLOVs.add("Commerce West");
		watercraft_CurrentCarrierLOVs.add("Country");
		watercraft_CurrentCarrierLOVs.add("CSAA IG");
		watercraft_CurrentCarrierLOVs.add("Dairyland");
		watercraft_CurrentCarrierLOVs.add("Deerbrook");
		watercraft_CurrentCarrierLOVs.add("Encompass");
		watercraft_CurrentCarrierLOVs.add("Farm Bureau");
		watercraft_CurrentCarrierLOVs.add("Farmers");
		watercraft_CurrentCarrierLOVs.add("Foremost");
		watercraft_CurrentCarrierLOVs.add("GEICO");
		watercraft_CurrentCarrierLOVs.add("GMAC");
		watercraft_CurrentCarrierLOVs.add("Grange");
		watercraft_CurrentCarrierLOVs.add("Great American");
		watercraft_CurrentCarrierLOVs.add("Guaranty National");
		watercraft_CurrentCarrierLOVs.add("GuideOne");
		watercraft_CurrentCarrierLOVs.add("Hartford");
		watercraft_CurrentCarrierLOVs.add("Horace Mann");
		watercraft_CurrentCarrierLOVs.add("Infinity");
		watercraft_CurrentCarrierLOVs.add("Kemper");
		watercraft_CurrentCarrierLOVs.add("Leader");
		watercraft_CurrentCarrierLOVs.add("Liberty Mutual");
		watercraft_CurrentCarrierLOVs.add("Mercury");
		watercraft_CurrentCarrierLOVs.add("Met Life");
		watercraft_CurrentCarrierLOVs.add("Mid-Century");
		watercraft_CurrentCarrierLOVs.add("Nationwide");
		watercraft_CurrentCarrierLOVs.add("None");
		watercraft_CurrentCarrierLOVs.add("North Pacific");
		watercraft_CurrentCarrierLOVs.add("Omaha");
		watercraft_CurrentCarrierLOVs.add("Omni");
		watercraft_CurrentCarrierLOVs.add("Other Carrier");
		watercraft_CurrentCarrierLOVs.add("Progressive");
		watercraft_CurrentCarrierLOVs.add("Prudential");
		watercraft_CurrentCarrierLOVs.add("Regal");
		watercraft_CurrentCarrierLOVs.add("Safeco");
		watercraft_CurrentCarrierLOVs.add("Safeway Insurance Group");
		watercraft_CurrentCarrierLOVs.add("State Farm");
		watercraft_CurrentCarrierLOVs.add("Travelers");
		watercraft_CurrentCarrierLOVs.add("Unigard");
		watercraft_CurrentCarrierLOVs.add("Unitrin");
		watercraft_CurrentCarrierLOVs.add("USAA");
		watercraft_CurrentCarrierLOVs.add("Viking");
		watercraft_CurrentCarrierLOVs.add("Wawanesa");
		watercraft_CurrentCarrierLOVs.add("Windsor");
		watercraft_CurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> recrVehicle_CurrentCarrierLOVs = new ArrayList<String>();
	static {
		recrVehicle_CurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		recrVehicle_CurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		recrVehicle_CurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		recrVehicle_CurrentCarrierLOVs.add("21st Century");
		recrVehicle_CurrentCarrierLOVs.add("AIG");
		recrVehicle_CurrentCarrierLOVs.add("Allied");
		recrVehicle_CurrentCarrierLOVs.add("Allstate");
		recrVehicle_CurrentCarrierLOVs.add("American Family");
		recrVehicle_CurrentCarrierLOVs.add("American Modern");
		recrVehicle_CurrentCarrierLOVs.add("American National");
		recrVehicle_CurrentCarrierLOVs.add("AMEX");
		recrVehicle_CurrentCarrierLOVs.add("AMICA Mutual");
		recrVehicle_CurrentCarrierLOVs.add("CA Casualty");
		recrVehicle_CurrentCarrierLOVs.add("Charter Oak Fire");
		recrVehicle_CurrentCarrierLOVs.add("Chubb");
		recrVehicle_CurrentCarrierLOVs.add("Commerce West");
		recrVehicle_CurrentCarrierLOVs.add("Country");
		recrVehicle_CurrentCarrierLOVs.add("CSAA IG");
		recrVehicle_CurrentCarrierLOVs.add("Dairyland");
		recrVehicle_CurrentCarrierLOVs.add("Deerbrook");
		recrVehicle_CurrentCarrierLOVs.add("Encompass");
		recrVehicle_CurrentCarrierLOVs.add("Farm Bureau");
		recrVehicle_CurrentCarrierLOVs.add("Farmers");
		recrVehicle_CurrentCarrierLOVs.add("Foremost");
		recrVehicle_CurrentCarrierLOVs.add("GEICO");
		recrVehicle_CurrentCarrierLOVs.add("GMAC");
		recrVehicle_CurrentCarrierLOVs.add("Grange");
		recrVehicle_CurrentCarrierLOVs.add("Great American");
		recrVehicle_CurrentCarrierLOVs.add("Guaranty National");
		recrVehicle_CurrentCarrierLOVs.add("GuideOne");
		recrVehicle_CurrentCarrierLOVs.add("Hartford");
		recrVehicle_CurrentCarrierLOVs.add("Horace Mann");
		recrVehicle_CurrentCarrierLOVs.add("Infinity");
		recrVehicle_CurrentCarrierLOVs.add("Kemper");
		recrVehicle_CurrentCarrierLOVs.add("Leader");
		recrVehicle_CurrentCarrierLOVs.add("Liberty Mutual");
		recrVehicle_CurrentCarrierLOVs.add("Mercury");
		recrVehicle_CurrentCarrierLOVs.add("Met Life");
		recrVehicle_CurrentCarrierLOVs.add("Mid-Century");
		recrVehicle_CurrentCarrierLOVs.add("Nationwide");
		recrVehicle_CurrentCarrierLOVs.add("None");
		recrVehicle_CurrentCarrierLOVs.add("North Pacific");
		recrVehicle_CurrentCarrierLOVs.add("Omaha");
		recrVehicle_CurrentCarrierLOVs.add("Omni");
		recrVehicle_CurrentCarrierLOVs.add("Other Carrier");
		recrVehicle_CurrentCarrierLOVs.add("Progressive");
		recrVehicle_CurrentCarrierLOVs.add("Prudential");
		recrVehicle_CurrentCarrierLOVs.add("Regal");
		recrVehicle_CurrentCarrierLOVs.add("Safeco");
		recrVehicle_CurrentCarrierLOVs.add("Safeway Insurance Group");
		recrVehicle_CurrentCarrierLOVs.add("State Farm");
		recrVehicle_CurrentCarrierLOVs.add("Travelers");
		recrVehicle_CurrentCarrierLOVs.add("Unigard");
		recrVehicle_CurrentCarrierLOVs.add("Unitrin");
		recrVehicle_CurrentCarrierLOVs.add("USAA");
		recrVehicle_CurrentCarrierLOVs.add("Viking");
		recrVehicle_CurrentCarrierLOVs.add("Wawanesa");
		recrVehicle_CurrentCarrierLOVs.add("Windsor");
		recrVehicle_CurrentCarrierLOVs.add("Workmens Auto");
	}

}
