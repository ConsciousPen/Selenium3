package aaa.modules.delta.pup;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
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

/**
 * 
 * @author Ryan Yu
 *
 */
public class TestCODeltaScenario1 extends PersonalUmbrellaBaseTest {
	
	private String quoteNumber;
	private String policyNumber;
	private GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();

	/**
	 * 1. Create new account. 
	 * 2. Initiate new IN PUP-quote creation. 
	 * 3. Fill all mandatory fields on all tabs. 
	 * - On Underlying Risks 
	 * - Property tab add one more residence. 
	 * - On Underlying Risks 
	 * - Auto tab add one "Private Passenger Auto" and one "Limited Production/Antique" automobile, add one Motorcycle and one Motor Home. 
	 * - On Underlying Risks 
	 * - Other Vehicles tab add one Watercraft and one Recreational vehicles (off-road).
	 * - Calculate premium. 
	 * 4. Save & Exit quote. 
	 * 5. Navigate to On-Demand Documents page and check there is PSIQXX document. Document PSIQXX -
	 * Personal Umbrella Liability Insurance Quote Page is on On-Demand Documents page.
	 */
	@Test(groups = { Groups.DELTA, Groups.HIGH })
	public void pupDeltaSC1_TC01() {
		mainApp().open();
        createCustomerIndividual();

        Map<String, String> primaryPolicies = getPrimaryPoliciesForPup();
        TestData tdPolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
        PrefillTab prefillTab = policy.getDefaultView().getTab(PrefillTab.class);
        tdPolicy = prefillTab.adjustWithRealPolicies(tdPolicy, primaryPolicies);
        quoteNumber = createQuote(tdPolicy);
		
		policy.quoteDocGen().start();
		goddTab.verify.documentsPresent(DocGenEnum.Documents.PSIQXX);
		goddTab.buttonCancel.click();
	}

	/**
	 * 1. Open quote in edit mode. 
	 * 2. Navigate to Underlying Risks - Property tab and verify Current Carrier LOVs of Residence.
	 * 3. Navigate to Underlying Risks - Auto tab and verify Current Carrier LOVs of: 
	 * - Private Passenger Auto; 
	 * - Antique/Limited Prodaction Auto; 
	 * - Motorcycle;
 	 * - MotorHome.
 	 * 4. Navigate to Underlying Risks - Other Vehicles tab and verify Current Carrier LOVs of Watercraft and Recreational Vehicle. 
	 * 5. Save & Exit quote.
	 */
	@Test(groups = { Groups.DELTA, Groups.HIGH })
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

	/**
	 * 1. Open quote in edit mode. 
	 * 2. Navigate to Premium & Coverages tab and recalculate premium. 
	 * 3. Navigate to Bind tab and purchase policy
	 * 4. Verify Declaration Documents PS02 is generated at policy issue. 
	 */
	@Test(groups = { Groups.DELTA, Groups.HIGH })
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

	/**
	 * 1. Open Policy Consolidated screen. 
	 * 2. Go to On-Demand Documents tab.
 	 * 3. Verify document PS11 is on ODD tab
	 * 4. Select documents PS11 and press Generate button.
	 */
	@Test(groups = { Groups.DELTA, Groups.HIGH })
	public void pupDeltaSC1_TC04() {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		policy.quoteDocGen().start();
		goddTab.verify.documentsPresent(DocGenEnum.Documents.PS11);
		goddTab.buttonCancel.click();
	}
	
	private List<String> residence_CurrentCarrierLOVs = Arrays.asList("AAA-Michigan (ACG)","AAA-NoCal (CSAA IG) Rewrite","AAA-NoCal (CSAA IG) Sold/Bought","AAA-SoCal (ACSC)","Allied","Allstate","Amco Ins Co","American Family","American National","Auto Owners","Bear River Mutual","Chartis","Cincinnati","Country","CSAA IG","CSE Safeguard","Farm Bureau","Farmers","Fire Insurance","First Time Homebuyer","Foremost","Great Northern","Hartford","Homesite","Liberty Mutual","Metropolitan","Nationwide","No Prior","Other Carrier","Owners Insurance","Pacific Indemnity","Safeco","Standard Fire","State Farm","Travelers","Unigard","USAA");

	private List<String> auto_CurrentCarrierLOVs = Arrays.asList("","AAA-Michigan (ACG)","AAA-NoCal (CSAA IG)","AAA-SoCal (ACSC)","21st Century","AIG","Allied","Allstate","American Family","American Modern","American National","AMEX","AMICA Mutual","CA Casualty","Charter Oak Fire","Chubb","Commerce West","Country","CSAA IG","Dairyland","Deerbrook","Encompass","Farm Bureau","Farmers","Foremost","GEICO","GMAC","Grange","Great American","Guaranty National","GuideOne","Hartford","Horace Mann","Infinity","Kemper","Leader","Liberty Mutual","Mercury","Met Life","Mid-Century","Nationwide","None","North Pacific","Omaha","Omni","Other Carrier","Progressive","Prudential","Regal","Safeco","Safeway Insurance Group","State Farm","Travelers","Unigard","Unitrin","USAA","Viking","Wawanesa","Windsor","Workmens Auto");

	private List<String> antq_CurrentCarrierLOVs = Arrays.asList("","AAA-Michigan (ACG)","AAA-NoCal (CSAA IG)","AAA-SoCal (ACSC)","21st Century","AIG","Allied","Allstate","American Family","American Modern","American National","AMEX","AMICA Mutual","CA Casualty","Charter Oak Fire","Chubb","Commerce West","Country","CSAA IG","Dairyland","Deerbrook","Encompass","Farm Bureau","Farmers","Foremost","GEICO","GMAC","Grange","Great American","Guaranty National","GuideOne","Hartford","Horace Mann","Infinity","Kemper","Leader","Liberty Mutual","Mercury","Met Life","Mid-Century","Nationwide","None","North Pacific","Omaha","Omni","Other Carrier","Progressive","Prudential","Regal","Safeco","Safeway Insurance Group","State Farm","Travelers","Unigard","Unitrin","USAA","Viking","Wawanesa","Windsor","Workmens Auto");

	private List<String> motorcycle_CurrentCarrierLOVs = Arrays.asList("","AAA-Michigan (ACG)","AAA-SoCal (ACSC)","AAA-NoCal (CSAA IG)","21st Century","AIG","Allied","Allstate","American Family","American Modern","American National","AMEX","AMICA Mutual","CA Casualty","Charter Oak Fire","Chubb","Commerce West","Country","CSAA IG","Dairyland","Deerbrook","Encompass","Farm Bureau","Farmers","Foremost","GEICO","GMAC","Grange","Great American","Guaranty National","GuideOne","Hartford","Horace Mann","Infinity","Kemper","Leader","Liberty Mutual","Mercury","Met Life","Mid-Century","Nationwide","None","North Pacific","Omaha","Omni","Other Carrier","Progressive","Prudential","Regal","Safeco","Safeway Insurance Group","State Farm","Travelers","Unigard","Unitrin","USAA","Viking","Wawanesa","Windsor","Workmens Auto");

	private List<String> motorhome_CurrentCarrierLOVs = Arrays.asList("","AAA-Michigan (ACG)","AAA-SoCal (ACSC)","AAA-NoCal (CSAA IG)","21st Century","AIG","Allied","Allstate","American Family","American Modern","American National","AMEX","AMICA Mutual","CA Casualty","Charter Oak Fire","Chubb","Commerce West","Country","CSAA IG","Dairyland","Deerbrook","Encompass","Farm Bureau","Farmers","Foremost","GEICO","GMAC","Grange","Great American","Guaranty National","GuideOne","Hartford","Horace Mann","Infinity","Kemper","Leader","Liberty Mutual","Mercury","Met Life","Mid-Century","Nationwide","None","North Pacific","Omaha","Omni","Other Carrier","Progressive","Prudential","Regal","Safeco","Safeway Insurance Group","State Farm","Travelers","Unigard","Unitrin","USAA","Viking","Wawanesa","Windsor","Workmens Auto");

	private List<String> watercraft_CurrentCarrierLOVs = Arrays.asList("", "AAA-Michigan (ACG)","AAA-NoCal (CSAA IG)","AAA-SoCal (ACSC)","21st Century","AIG","Allied","Allstate","American Family","American Modern","American National","AMEX","AMICA Mutual","CA Casualty","Charter Oak Fire","Chubb","Commerce West","Country","CSAA IG","Dairyland","Deerbrook","Encompass","Farm Bureau","Farmers","Foremost","GEICO","GMAC","Grange","Great American","Guaranty National","GuideOne","Hartford","Horace Mann","Infinity","Kemper","Leader","Liberty Mutual","Mercury","Met Life","Mid-Century","Nationwide","None","North Pacific","Omaha","Omni","Other Carrier","Progressive","Prudential","Regal","Safeco","Safeway Insurance Group","State Farm","Travelers","Unigard","Unitrin","USAA","Viking","Wawanesa","Windsor","Workmens Auto");

	private List<String> recrVehicle_CurrentCarrierLOVs = Arrays.asList("AAA-Michigan (ACG)","AAA-SoCal (ACSC)","AAA-NoCal (CSAA IG)","21st Century","AIG","Allied","Allstate","American Family","American Modern","American National","AMEX","AMICA Mutual","CA Casualty","Charter Oak Fire","Chubb","Commerce West","Country","CSAA IG","Dairyland","Deerbrook","Encompass","Farm Bureau","Farmers","Foremost","GEICO","GMAC","Grange","Great American","Guaranty National","GuideOne","Hartford","Horace Mann","Infinity","Kemper","Leader","Liberty Mutual","Mercury","Met Life","Mid-Century","Nationwide","None","North Pacific","Omaha","Omni","Other Carrier","Progressive","Prudential","Regal","Safeco","Safeway Insurance Group","State Farm","Travelers","Unigard","Unitrin","USAA","Viking","Wawanesa","Windsor","Workmens Auto");
}
