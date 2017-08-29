package aaa.modules.delta.pup;

import java.util.ArrayList;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.actiontabs.GenerateOnDemandDocumentActionTab;
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
import static aaa.main.enums.DocGenEnum.Documents.*;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.*;
/**
 * 
 * @author Xiaolan Ge
 *
 */

@Test(groups = {Groups.DELTA, Groups.HIGH})
public class TestDCDeltaScenario1 extends PersonalUmbrellaBaseTest {
	private String quoteNumber;
	private String policyNumber;

	@Test
	public void pupDeltaDC1_TC01() {
		mainApp().open();
        createCustomerIndividual();

        Map<String, String> primaryPolicies = getPrimaryPoliciesForPup();
        TestData tdPolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
        PrefillTab prefillTab = policy.getDefaultView().getTab(PrefillTab.class);
        tdPolicy = prefillTab.adjustWithRealPolicies(tdPolicy, primaryPolicies);
        quoteNumber = createQuote(tdPolicy); 
          log.info("DELTA DC SC1: PUP Quote created with #" + quoteNumber);
	}

	@Test
	public void pupDeltaDC1_TC02() {
		mainApp().open();
		CustomAssert.enableSoftMode();
		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();

		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		UnderlyingRisksPropertyTab.tableAdditionalResidences.getRow(2).getCell(9).controls.links.get("View/Edit").click();
		UnderlyingRisksPropertyTab propertyTab = policy.getDefaultView().getTab(UnderlyingRisksPropertyTab.class);
		propertyTab.getAdditionalResidenciesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(residenceCurrentCarrierLOVs);

		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		UnderlyingRisksAutoTab.tableAutomobiles.getRow(1).getCell(9).controls.links.get("View/Edit").click();
		UnderlyingRisksAutoTab autoTab = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);
		autoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(autoPPACurrentCarrierLOVs);

		UnderlyingRisksAutoTab.tableAutomobiles.getRow(2).getCell(9).controls.links.get("View/Edit").click();
		autoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(autoAntiqueCurrentCarrierLOVs);

		autoTab.getMotorcyclesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(motorcycleCurrentCarrierLOVs);
		autoTab.getMotorHomesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(motorHomeCurrentCarrierLOVs);

		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
		UnderlyingRisksOtherVehiclesTab otherVehiclesTab = policy.getDefaultView().getTab(UnderlyingRisksOtherVehiclesTab.class);
		otherVehiclesTab.getRecreationalVehicleAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(recrVehicleCurrentCarrierLOVs);

		otherVehiclesTab.getWatercraftAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.CURRENT_CARRIER.getLabel(), ComboBox.class).verify.options(watercraftCurrentCarrierLOVs);
		Tab.buttonSaveAndExit.click();
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	@Test
	public void pupDeltaDC1_TC03() {
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
		log.info(getState() + " Policy PUP DC is created: " + policyNumber);
		log.info("============================================");
	}
	

	@Test
	public void pupDeltaDC1_TC04() throws Exception {
		GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//add verification of On-Demand Documents Tab	
		policy.policyDocGen().start();
		goddTab.verify.documentsPresent(HSU01XX, HSU02XX, HSU03XX, HSU04XX, HSU05XX, HSU06XX, HSU07XX, HSU08XX, HSU09XX, F605005, AHAPXX, HSRFIXX, AHRCTXX,AHFMXX, PS11, AHAUXX);
		goddTab.getDocumentsControl().getTable().getRow(DOCUMENT_NUM, PS11.getId()).getCell(SELECT).controls.checkBoxes.getFirst().verify.enabled(true);
		goddTab.generateDocuments(PS11);
		NavigationPage.Verify.mainTabSelected(NavigationEnum.AppMainTabs.POLICY.get());
	}

	private static ArrayList<String> residenceCurrentCarrierLOVs = new ArrayList<String>();
	static {
		residenceCurrentCarrierLOVs.add("AAA Other");
		residenceCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		residenceCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		residenceCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		residenceCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		residenceCurrentCarrierLOVs.add("AIG");
		residenceCurrentCarrierLOVs.add("Alfa Insurance");
		residenceCurrentCarrierLOVs.add("Allstate");
		residenceCurrentCarrierLOVs.add("Assurant");
		residenceCurrentCarrierLOVs.add("Chubb");
		residenceCurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		residenceCurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		residenceCurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		residenceCurrentCarrierLOVs.add("Donegal");
		residenceCurrentCarrierLOVs.add("Erie");
		residenceCurrentCarrierLOVs.add("Farmers");
		residenceCurrentCarrierLOVs.add("GMAC");
		residenceCurrentCarrierLOVs.add("Hanover");
		residenceCurrentCarrierLOVs.add("Harleysville");
		residenceCurrentCarrierLOVs.add("Hartford");
		residenceCurrentCarrierLOVs.add("High Point");
		residenceCurrentCarrierLOVs.add("Homesite");
		residenceCurrentCarrierLOVs.add("Kemper");
		residenceCurrentCarrierLOVs.add("Liberty Mutual");
		residenceCurrentCarrierLOVs.add("Loudoun Mutual");
		residenceCurrentCarrierLOVs.add("Mercury");
		residenceCurrentCarrierLOVs.add("MetLife");
		residenceCurrentCarrierLOVs.add("Nationwide");
		residenceCurrentCarrierLOVs.add("NJ Cure");
		residenceCurrentCarrierLOVs.add("NJ Manufacturers");
		residenceCurrentCarrierLOVs.add("NJ Skylands");
		residenceCurrentCarrierLOVs.add("None");
		residenceCurrentCarrierLOVs.add("Northern Neck");
		residenceCurrentCarrierLOVs.add("Ohio Casualty");
		residenceCurrentCarrierLOVs.add("Other Carrier");
		residenceCurrentCarrierLOVs.add("Palisades");
		residenceCurrentCarrierLOVs.add("Proformance");
		residenceCurrentCarrierLOVs.add("Rockingham Mutual");
		residenceCurrentCarrierLOVs.add("Safeco");
		residenceCurrentCarrierLOVs.add("Selective");
		residenceCurrentCarrierLOVs.add("State Farm");
		residenceCurrentCarrierLOVs.add("Travelers");
		residenceCurrentCarrierLOVs.add("USAA");
		residenceCurrentCarrierLOVs.add("VA Farm Bureau");
		residenceCurrentCarrierLOVs.add("Western United");
		residenceCurrentCarrierLOVs.add("White Mountains");
	}

	private static ArrayList<String> autoPPACurrentCarrierLOVs = new ArrayList<String>();
	static {
		autoPPACurrentCarrierLOVs.add("");
		autoPPACurrentCarrierLOVs.add("AAA Other");
		autoPPACurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		autoPPACurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		autoPPACurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		autoPPACurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		autoPPACurrentCarrierLOVs.add("AIG");
		autoPPACurrentCarrierLOVs.add("Alfa Insurance");
		autoPPACurrentCarrierLOVs.add("Allstate");
		autoPPACurrentCarrierLOVs.add("Assurant");
		autoPPACurrentCarrierLOVs.add("Chubb");
		autoPPACurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		autoPPACurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		autoPPACurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		autoPPACurrentCarrierLOVs.add("Donegal");
		autoPPACurrentCarrierLOVs.add("Erie");
		autoPPACurrentCarrierLOVs.add("Farmers");
		autoPPACurrentCarrierLOVs.add("GMAC");
		autoPPACurrentCarrierLOVs.add("Hanover");
		autoPPACurrentCarrierLOVs.add("Harleysville");
		autoPPACurrentCarrierLOVs.add("Hartford");
		autoPPACurrentCarrierLOVs.add("High Point");
		autoPPACurrentCarrierLOVs.add("Homesite");
		autoPPACurrentCarrierLOVs.add("Kemper");
		autoPPACurrentCarrierLOVs.add("Liberty Mutual");
		autoPPACurrentCarrierLOVs.add("Loudoun Mutual");
		autoPPACurrentCarrierLOVs.add("Mercury");
		autoPPACurrentCarrierLOVs.add("MetLife");
		autoPPACurrentCarrierLOVs.add("Nationwide");
		autoPPACurrentCarrierLOVs.add("NJ Cure");
		autoPPACurrentCarrierLOVs.add("NJ Manufacturers");
		autoPPACurrentCarrierLOVs.add("NJ Skylands");
		autoPPACurrentCarrierLOVs.add("None");
		autoPPACurrentCarrierLOVs.add("Northern Neck");
		autoPPACurrentCarrierLOVs.add("Ohio Casualty");
		autoPPACurrentCarrierLOVs.add("Other Carrier");
		autoPPACurrentCarrierLOVs.add("Palisades");
		autoPPACurrentCarrierLOVs.add("Proformance");
		autoPPACurrentCarrierLOVs.add("Rockingham Mutual");
		autoPPACurrentCarrierLOVs.add("Safeco");
		autoPPACurrentCarrierLOVs.add("Selective");
		autoPPACurrentCarrierLOVs.add("State Farm");
		autoPPACurrentCarrierLOVs.add("Travelers");
		autoPPACurrentCarrierLOVs.add("USAA");
		autoPPACurrentCarrierLOVs.add("VA Farm Bureau");
		autoPPACurrentCarrierLOVs.add("Western United");
		autoPPACurrentCarrierLOVs.add("White Mountains");
	}

	private static ArrayList<String> autoAntiqueCurrentCarrierLOVs = new ArrayList<String>();
	static {
		autoAntiqueCurrentCarrierLOVs.add("");
		autoAntiqueCurrentCarrierLOVs.add("AAA Other");
		autoAntiqueCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		autoAntiqueCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		autoAntiqueCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		autoAntiqueCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		autoAntiqueCurrentCarrierLOVs.add("AIG");
		autoAntiqueCurrentCarrierLOVs.add("Alfa Insurance");
		autoAntiqueCurrentCarrierLOVs.add("Allstate");
		autoAntiqueCurrentCarrierLOVs.add("Assurant");
		autoAntiqueCurrentCarrierLOVs.add("Chubb");
		autoAntiqueCurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		autoAntiqueCurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		autoAntiqueCurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		autoAntiqueCurrentCarrierLOVs.add("Donegal");
		autoAntiqueCurrentCarrierLOVs.add("Erie");
		autoAntiqueCurrentCarrierLOVs.add("Farmers");
		autoAntiqueCurrentCarrierLOVs.add("GMAC");
		autoAntiqueCurrentCarrierLOVs.add("Hanover");
		autoAntiqueCurrentCarrierLOVs.add("Harleysville");
		autoAntiqueCurrentCarrierLOVs.add("Hartford");
		autoAntiqueCurrentCarrierLOVs.add("High Point");
		autoAntiqueCurrentCarrierLOVs.add("Homesite");
		autoAntiqueCurrentCarrierLOVs.add("Kemper");
		autoAntiqueCurrentCarrierLOVs.add("Liberty Mutual");
		autoAntiqueCurrentCarrierLOVs.add("Loudoun Mutual");
		autoAntiqueCurrentCarrierLOVs.add("Mercury");
		autoAntiqueCurrentCarrierLOVs.add("MetLife");
		autoAntiqueCurrentCarrierLOVs.add("Nationwide");
		autoAntiqueCurrentCarrierLOVs.add("NJ Cure");
		autoAntiqueCurrentCarrierLOVs.add("NJ Manufacturers");
		autoAntiqueCurrentCarrierLOVs.add("NJ Skylands");
		autoAntiqueCurrentCarrierLOVs.add("None");
		autoAntiqueCurrentCarrierLOVs.add("Northern Neck");
		autoAntiqueCurrentCarrierLOVs.add("Ohio Casualty");
		autoAntiqueCurrentCarrierLOVs.add("Other Carrier");
		autoAntiqueCurrentCarrierLOVs.add("Palisades");
		autoAntiqueCurrentCarrierLOVs.add("Proformance");
		autoAntiqueCurrentCarrierLOVs.add("Rockingham Mutual");
		autoAntiqueCurrentCarrierLOVs.add("Safeco");
		autoAntiqueCurrentCarrierLOVs.add("Selective");
		autoAntiqueCurrentCarrierLOVs.add("State Farm");
		autoAntiqueCurrentCarrierLOVs.add("Travelers");
		autoAntiqueCurrentCarrierLOVs.add("USAA");
		autoAntiqueCurrentCarrierLOVs.add("VA Farm Bureau");
		autoAntiqueCurrentCarrierLOVs.add("Western United");
		autoAntiqueCurrentCarrierLOVs.add("White Mountains");
	}

	private static ArrayList<String> motorcycleCurrentCarrierLOVs = new ArrayList<String>();
	static {
		motorcycleCurrentCarrierLOVs.add("");
		motorcycleCurrentCarrierLOVs.add("AAA Other");
		motorcycleCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		motorcycleCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		motorcycleCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		motorcycleCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		motorcycleCurrentCarrierLOVs.add("AIG");
		motorcycleCurrentCarrierLOVs.add("Alfa Insurance");
		motorcycleCurrentCarrierLOVs.add("Allstate");
		motorcycleCurrentCarrierLOVs.add("Assurant");
		motorcycleCurrentCarrierLOVs.add("Chubb");
		motorcycleCurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		motorcycleCurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		motorcycleCurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		motorcycleCurrentCarrierLOVs.add("Donegal");
		motorcycleCurrentCarrierLOVs.add("Erie");
		motorcycleCurrentCarrierLOVs.add("Farmers");
		motorcycleCurrentCarrierLOVs.add("GMAC");
		motorcycleCurrentCarrierLOVs.add("Hanover");
		motorcycleCurrentCarrierLOVs.add("Harleysville");
		motorcycleCurrentCarrierLOVs.add("Hartford");
		motorcycleCurrentCarrierLOVs.add("High Point");
		motorcycleCurrentCarrierLOVs.add("Homesite");
		motorcycleCurrentCarrierLOVs.add("Kemper");
		motorcycleCurrentCarrierLOVs.add("Liberty Mutual");
		motorcycleCurrentCarrierLOVs.add("Loudoun Mutual");
		motorcycleCurrentCarrierLOVs.add("Mercury");
		motorcycleCurrentCarrierLOVs.add("MetLife");
		motorcycleCurrentCarrierLOVs.add("Nationwide");
		motorcycleCurrentCarrierLOVs.add("NJ Cure");
		motorcycleCurrentCarrierLOVs.add("NJ Manufacturers");
		motorcycleCurrentCarrierLOVs.add("NJ Skylands");
		motorcycleCurrentCarrierLOVs.add("None");
		motorcycleCurrentCarrierLOVs.add("Northern Neck");
		motorcycleCurrentCarrierLOVs.add("Ohio Casualty");
		motorcycleCurrentCarrierLOVs.add("Other Carrier");
		motorcycleCurrentCarrierLOVs.add("Palisades");
		motorcycleCurrentCarrierLOVs.add("Proformance");
		motorcycleCurrentCarrierLOVs.add("Rockingham Mutual");
		motorcycleCurrentCarrierLOVs.add("Safeco");
		motorcycleCurrentCarrierLOVs.add("Selective");
		motorcycleCurrentCarrierLOVs.add("State Farm");
		motorcycleCurrentCarrierLOVs.add("Travelers");
		motorcycleCurrentCarrierLOVs.add("USAA");
		motorcycleCurrentCarrierLOVs.add("VA Farm Bureau");
		motorcycleCurrentCarrierLOVs.add("Western United");
		motorcycleCurrentCarrierLOVs.add("White Mountains");
	}

	private static ArrayList<String> motorHomeCurrentCarrierLOVs = new ArrayList<String>();
	static {
		motorHomeCurrentCarrierLOVs.add("");
		motorHomeCurrentCarrierLOVs.add("AAA Other");
		motorHomeCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		motorHomeCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		motorHomeCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		motorHomeCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		motorHomeCurrentCarrierLOVs.add("AIG");
		motorHomeCurrentCarrierLOVs.add("Alfa Insurance");
		motorHomeCurrentCarrierLOVs.add("Allstate");
		motorHomeCurrentCarrierLOVs.add("Assurant");
		motorHomeCurrentCarrierLOVs.add("Chubb");
		motorHomeCurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		motorHomeCurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		motorHomeCurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		motorHomeCurrentCarrierLOVs.add("Donegal");
		motorHomeCurrentCarrierLOVs.add("Erie");
		motorHomeCurrentCarrierLOVs.add("Farmers");
		motorHomeCurrentCarrierLOVs.add("GMAC");
		motorHomeCurrentCarrierLOVs.add("Hanover");
		motorHomeCurrentCarrierLOVs.add("Harleysville");
		motorHomeCurrentCarrierLOVs.add("Hartford");
		motorHomeCurrentCarrierLOVs.add("High Point");
		motorHomeCurrentCarrierLOVs.add("Homesite");
		motorHomeCurrentCarrierLOVs.add("Kemper");
		motorHomeCurrentCarrierLOVs.add("Liberty Mutual");
		motorHomeCurrentCarrierLOVs.add("Loudoun Mutual");
		motorHomeCurrentCarrierLOVs.add("Mercury");
		motorHomeCurrentCarrierLOVs.add("MetLife");
		motorHomeCurrentCarrierLOVs.add("Nationwide");
		motorHomeCurrentCarrierLOVs.add("NJ Cure");
		motorHomeCurrentCarrierLOVs.add("NJ Manufacturers");
		motorHomeCurrentCarrierLOVs.add("NJ Skylands");
		motorHomeCurrentCarrierLOVs.add("None");
		motorHomeCurrentCarrierLOVs.add("Northern Neck");
		motorHomeCurrentCarrierLOVs.add("Ohio Casualty");
		motorHomeCurrentCarrierLOVs.add("Other Carrier");
		motorHomeCurrentCarrierLOVs.add("Palisades");
		motorHomeCurrentCarrierLOVs.add("Proformance");
		motorHomeCurrentCarrierLOVs.add("Rockingham Mutual");
		motorHomeCurrentCarrierLOVs.add("Safeco");
		motorHomeCurrentCarrierLOVs.add("Selective");
		motorHomeCurrentCarrierLOVs.add("State Farm");
		motorHomeCurrentCarrierLOVs.add("Travelers");
		motorHomeCurrentCarrierLOVs.add("USAA");
		motorHomeCurrentCarrierLOVs.add("VA Farm Bureau");
		motorHomeCurrentCarrierLOVs.add("Western United");
		motorHomeCurrentCarrierLOVs.add("White Mountains");
	}

	private static ArrayList<String> watercraftCurrentCarrierLOVs = new ArrayList<String>();
	static {
		watercraftCurrentCarrierLOVs.add("");
		watercraftCurrentCarrierLOVs.add("AAA Other");
		watercraftCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		watercraftCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		watercraftCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		watercraftCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		watercraftCurrentCarrierLOVs.add("AIG");
		watercraftCurrentCarrierLOVs.add("Alfa Insurance");
		watercraftCurrentCarrierLOVs.add("Allstate");
		watercraftCurrentCarrierLOVs.add("Assurant");
		watercraftCurrentCarrierLOVs.add("Chubb");
		watercraftCurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		watercraftCurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		watercraftCurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		watercraftCurrentCarrierLOVs.add("Donegal");
		watercraftCurrentCarrierLOVs.add("Erie");
		watercraftCurrentCarrierLOVs.add("Farmers");
		watercraftCurrentCarrierLOVs.add("GMAC");
		watercraftCurrentCarrierLOVs.add("Hanover");
		watercraftCurrentCarrierLOVs.add("Harleysville");
		watercraftCurrentCarrierLOVs.add("Hartford");
		watercraftCurrentCarrierLOVs.add("High Point");
		watercraftCurrentCarrierLOVs.add("Homesite");
		watercraftCurrentCarrierLOVs.add("Kemper");
		watercraftCurrentCarrierLOVs.add("Liberty Mutual");
		watercraftCurrentCarrierLOVs.add("Loudoun Mutual");
		watercraftCurrentCarrierLOVs.add("Mercury");
		watercraftCurrentCarrierLOVs.add("MetLife");
		watercraftCurrentCarrierLOVs.add("Nationwide");
		watercraftCurrentCarrierLOVs.add("NJ Cure");
		watercraftCurrentCarrierLOVs.add("NJ Manufacturers");
		watercraftCurrentCarrierLOVs.add("NJ Skylands");
		watercraftCurrentCarrierLOVs.add("None");
		watercraftCurrentCarrierLOVs.add("Northern Neck");
		watercraftCurrentCarrierLOVs.add("Ohio Casualty");
		watercraftCurrentCarrierLOVs.add("Other Carrier");
		watercraftCurrentCarrierLOVs.add("Palisades");
		watercraftCurrentCarrierLOVs.add("Proformance");
		watercraftCurrentCarrierLOVs.add("Rockingham Mutual");
		watercraftCurrentCarrierLOVs.add("Safeco");
		watercraftCurrentCarrierLOVs.add("Selective");
		watercraftCurrentCarrierLOVs.add("State Farm");
		watercraftCurrentCarrierLOVs.add("Travelers");
		watercraftCurrentCarrierLOVs.add("USAA");
		watercraftCurrentCarrierLOVs.add("VA Farm Bureau");
		watercraftCurrentCarrierLOVs.add("Western United");
		watercraftCurrentCarrierLOVs.add("White Mountains");
	}

	private static ArrayList<String> recrVehicleCurrentCarrierLOVs = new ArrayList<String>();
	static {
		recrVehicleCurrentCarrierLOVs.add("AAA Other");
		recrVehicleCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		recrVehicleCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		recrVehicleCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		recrVehicleCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		recrVehicleCurrentCarrierLOVs.add("AIG");
		recrVehicleCurrentCarrierLOVs.add("Alfa Insurance");
		recrVehicleCurrentCarrierLOVs.add("Allstate");
		recrVehicleCurrentCarrierLOVs.add("Assurant");
		recrVehicleCurrentCarrierLOVs.add("Chubb");
		recrVehicleCurrentCarrierLOVs.add("Compliant w/o Ins-Military");
		recrVehicleCurrentCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		recrVehicleCurrentCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		recrVehicleCurrentCarrierLOVs.add("Donegal");
		recrVehicleCurrentCarrierLOVs.add("Erie");
		recrVehicleCurrentCarrierLOVs.add("Farmers");
		recrVehicleCurrentCarrierLOVs.add("GMAC");
		recrVehicleCurrentCarrierLOVs.add("Hanover");
		recrVehicleCurrentCarrierLOVs.add("Harleysville");
		recrVehicleCurrentCarrierLOVs.add("Hartford");
		recrVehicleCurrentCarrierLOVs.add("High Point");
		recrVehicleCurrentCarrierLOVs.add("Homesite");
		recrVehicleCurrentCarrierLOVs.add("Kemper");
		recrVehicleCurrentCarrierLOVs.add("Liberty Mutual");
		recrVehicleCurrentCarrierLOVs.add("Loudoun Mutual");
		recrVehicleCurrentCarrierLOVs.add("Mercury");
		recrVehicleCurrentCarrierLOVs.add("MetLife");
		recrVehicleCurrentCarrierLOVs.add("Nationwide");
		recrVehicleCurrentCarrierLOVs.add("NJ Cure");
		recrVehicleCurrentCarrierLOVs.add("NJ Manufacturers");
		recrVehicleCurrentCarrierLOVs.add("NJ Skylands");
		recrVehicleCurrentCarrierLOVs.add("None");
		recrVehicleCurrentCarrierLOVs.add("Northern Neck");
		recrVehicleCurrentCarrierLOVs.add("Ohio Casualty");
		recrVehicleCurrentCarrierLOVs.add("Other Carrier");
		recrVehicleCurrentCarrierLOVs.add("Palisades");
		recrVehicleCurrentCarrierLOVs.add("Proformance");
		recrVehicleCurrentCarrierLOVs.add("Rockingham Mutual");
		recrVehicleCurrentCarrierLOVs.add("Safeco");
		recrVehicleCurrentCarrierLOVs.add("Selective");
		recrVehicleCurrentCarrierLOVs.add("State Farm");
		recrVehicleCurrentCarrierLOVs.add("Travelers");
		recrVehicleCurrentCarrierLOVs.add("USAA");
		recrVehicleCurrentCarrierLOVs.add("VA Farm Bureau");
		recrVehicleCurrentCarrierLOVs.add("Western United");
		recrVehicleCurrentCarrierLOVs.add("White Mountains");
	}

}
