package aaa.modules.delta.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.PersonalUmbrellaTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.actiontabs.CancelNoticeActionTab;
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
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static aaa.main.enums.DocGenConstants.OnDemandDocumentsTable.*;


@Parameters({"state"})
	@Test(groups = {Groups.DELTA, Groups.HIGH})
public class TestCTDeltaScenario1 extends PersonalUmbrellaBaseTest{
	
	private String quoteNumber;
	private String policyNumber;
	UnderlyingRisksPropertyTab underlyingriskproperty= policy.getDefaultView().getTab(UnderlyingRisksPropertyTab.class);
	UnderlyingRisksAutoTab underlyingrisksauto = policy.getDefaultView().getTab(UnderlyingRisksAutoTab.class);
	UnderlyingRisksOtherVehiclesTab underlyingrisksothervehicles= policy.getDefaultView().getTab(UnderlyingRisksOtherVehiclesTab.class);
	private String ER_9933 = "Cancellation effective date must be at least 49 days from today when the policy is within the new business discovery period.";
	private String ER_9208 = "Cancellation effective date must be before the end of the policy term.";
	
	/**
	 * @author Lina Li
	 * @name Create a new quote, CT state [TC01]
	 * @scenario 
	 * 1. Create customer
	 * 2. Initiate CT PUP quote creation
	 * 3. On Underlying Risks - Auto tab add:
	 *  - 1 Driver, Excluded = No; 
     *  - 2 Automobiles: 1 PPA and 1 Antique auto are not excluded; 
     *  - 1 Motorcycle, Excluded = No;
     *  - 1 MotorHome, Excluded = No. 
     * 4. On Underlying Risks - Other Vehicles add:
     *  - 1 Watercraft, Excluded = No; 
     *  - 1 Recreational Vehicle, Excluded = No. 
     * 5.  Navigate to Premium & Coverages - Quote tab and calculate premium. 
     * 6. Fill Underwriting & Approval tab, Documents tab. 
     * 7. Save & Exit quote.  
	 * @details
	 */
	
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void TC01_createQuote(@Optional("") String state) {		
		mainApp().open();		
        createCustomerIndividual();
        TestData td = getTestSpecificTD("TestData");
        td = policy.getDefaultView().getTab(PrefillTab.class).adjustWithRealPolicies(td, getPrimaryPoliciesForPup());       
        createQuote(td);
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CT SC1: PUP Quote created with #" + quoteNumber);
	}
	
	/**
	 * @author Lina Li
	 * @name Verify the lookup value for Current Carrier, CT state [TC02]
	 * @scenario 
	 * 1. Open the quote of TC01 in edit mode. 
	 * 2. Navigate to Underlying Risks - Property tab and verify Current Carrier LOVs of Residence.
	 * 3. Navigate to Underlying Risks - Auto tab and verify Current Carrier LOVs of: 
     *  - Private Passenger Auto; 
     *  - Antique/Limited Prodaction Auto; 
     *  - Motorcycle;
     *  - MotorHome
     * 4. Navigate to Underlying Risks - Other Vehicles tab and verify Current Carrier LOVs of Watercraft and Recreational Vehicle. 
     * 5. Save & Exit quote.  
	 * @details
	 */
	
	@Parameters({"state"})
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void TC02_currentcarrierLOVs(@Optional("") String state) {
		mainApp().open();
		CustomAssert.enableSoftMode();
		SearchPage.openQuote(quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		UnderlyingRisksPropertyTab.tableAdditionalResidences.getRow(2).getCell(9).controls.links.get("View/Edit").click();
		underlyingriskproperty.getAdditionalResidenciesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(residenceCurrentCarrierLOVs);
		
		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		UnderlyingRisksAutoTab.tableAutomobiles.getRow(1).getCell(9).controls.links.get("View/Edit").click();
		underlyingrisksauto.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(autoPPACurrentCarrierLOVs);
		UnderlyingRisksAutoTab.tableAutomobiles.getRow(2).getCell(9).controls.links.get("View/Edit").click();
		underlyingrisksauto.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(autoAntiqueCurrentCarrierLOVs);
		underlyingrisksauto.getMotorcyclesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(motorcycleCurrentCarrierLOVs);
		underlyingrisksauto.getMotorHomesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(motorHomeCurrentCarrierLOVs);
		
		NavigationPage.toViewTab(PersonalUmbrellaTab.UNDERLYING_RISKS_OTHER_VEHICLES.get());
		underlyingrisksothervehicles.getWatercraftAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(watercraftCurrentCarrierLOVs);
		underlyingrisksothervehicles.getRecreationalVehicleAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.CURRENT_CARRIER.getLabel(),ComboBox.class).verify.options(recrVehicleCurrentCarrierLOVs);
		
		Tab.buttonSaveAndExit.click();
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}	
	
	
	/**
	 * @author Lina Li
	 * @name Bind Policy, CT state [TC03]
	 * @scenario 
	 * 1. Open the quote of TC01 in edit mode. 
	 * 2. Navigate to Premium & Coverages tab and recalculate premium.  
	 * 3. Navigate to Bind tab and purchase policy. 
	 * @details
	 */
	
	@Parameters({"state"})
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void TC03_BindPolicy(@Optional("") String state) {
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
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("DELTA CT SC1: PUP policy bound with #" + policyNumber);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, PS02);

	}
	
	/**
	 * @author Lina Li
	 * @name Verify On-Demand Documents tab, CT state [TC04]
	 * @scenario 
	 * 1. Open Policy Consolidated screen. 
	 * 2. Go to On-Demand Documents tab. 
	 * 3. Navigate to Bind tab and purchase policy. 
	 * 4. Verify documents are present and absent on ODD tab.
	 * 5. Verify document PS11 present and enabled on ODD tab.
	 * 6. Select PS11 and generate the form
	 * @details
	 */
	
	@Parameters({"state"})
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void TC04_verifyODDPolicy(@Optional("") String state) {
		GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();
		mainApp().open();
    	SearchPage.openPolicy(policyNumber);
//    	SearchPage.openQuote(quoteNumber);
//    	policy.quoteDocGen().start();
		policy.policyDocGen().start();

		goddTab.verify.documentsPresent(PS11);
		goddTab.getDocumentsControl().getTable().getRow(DOCUMENT_NUM, PS11.getId()).getCell(SELECT).controls.checkBoxes.getFirst().verify.enabled(true);
		goddTab.generateDocuments(PS11);
		DocGenHelper.verifyDocumentsGenerated(policyNumber, PS11);
	}
	
	/**
	 * @author Lina Li
	 * @name Verify Cancel Notice, CT state [TC05]
	 * @scenario 
	 * 1. Open Policy Consolidated screen. 
	 * 2. Select Cancel Notice in Move To drop down 
	 * 3. Verify values of prefilled "Cancellation effective date" and "Days of notice".  
	 * 4. Set "Cancellation effective date" to current date + 48 days and verify error message. 
	 * 5. Set "Cancellation effective date" to date after policy expiration date and verify error message. 
	 * 6. Fill all mandatory fields with correct values on Cancel Notice page and click Ok. 
	 * 7. Verify Policy status is Active but Cancel Notice flag is set on the policy.
	 * @details
	 */
	
	@Parameters({"state"})
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void TC05_verifyCancelNoticeTab(@Optional("") String state) {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.cancelNotice().start(); 
		CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();
		assertThat(cancelNoticeTab.getAssetList().getAsset(PersonalUmbrellaMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE)).hasValue(DateTimeUtils.getCurrentDateTime().plusDays(49).format(DateTimeUtils.MM_DD_YYYY));
		assertThat(cancelNoticeTab.getAssetList().getAsset(PersonalUmbrellaMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE)).hasValue("49");
		cancelNoticeTab.getAssetList().getAsset(PersonalUmbrellaMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE).setValue(DateTimeUtils.getCurrentDateTime().plusDays(48).format(DateTimeUtils.MM_DD_YYYY));
		assertThat(cancelNoticeTab.getAssetList().getWarning(PersonalUmbrellaMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE)).hasValue(ER_9933);
		cancelNoticeTab.getAssetList().getAsset(PersonalUmbrellaMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE).setValue(DateTimeUtils.getCurrentDateTime().plusDays(367).format(DateTimeUtils.MM_DD_YYYY));
		assertThat(cancelNoticeTab.getAssetList().getWarning(PersonalUmbrellaMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE)).hasValue(ER_9208);
		
		TestData tdCancelNotice=getTestSpecificTD("TestData_Plus49Days");
		cancelNoticeTab.fillTab(tdCancelNotice);
		CancelNoticeActionTab.buttonOk.click();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelCancelNotice.verify.present();
		
		
	}
	
	
	private static ArrayList<String> residenceCurrentCarrierLOVs = new ArrayList<String>();
	static {
		residenceCurrentCarrierLOVs.add("ACIC Non-Conversion");
		residenceCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		residenceCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		residenceCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		residenceCurrentCarrierLOVs.add("21st Century");
		residenceCurrentCarrierLOVs.add("AIG");
		residenceCurrentCarrierLOVs.add("Allied");
		residenceCurrentCarrierLOVs.add("Allstate");
		residenceCurrentCarrierLOVs.add("American Family");
		residenceCurrentCarrierLOVs.add("American National");
		residenceCurrentCarrierLOVs.add("AMEX");
		residenceCurrentCarrierLOVs.add("AMICA Mutual");
		residenceCurrentCarrierLOVs.add("CA Casualty");
		residenceCurrentCarrierLOVs.add("Charter Oak Fire");
		residenceCurrentCarrierLOVs.add("Chubb");
		residenceCurrentCarrierLOVs.add("Commerce West");
		residenceCurrentCarrierLOVs.add("Country");
		residenceCurrentCarrierLOVs.add("CSAA IG");
		residenceCurrentCarrierLOVs.add("Dairyland");
		residenceCurrentCarrierLOVs.add("Deerbrook");
		residenceCurrentCarrierLOVs.add("Encompass");
		residenceCurrentCarrierLOVs.add("Farm Bureau");
		residenceCurrentCarrierLOVs.add("Farmers");
		residenceCurrentCarrierLOVs.add("Foremost");
		residenceCurrentCarrierLOVs.add("GEICO");
		residenceCurrentCarrierLOVs.add("GMAC");
		residenceCurrentCarrierLOVs.add("Grange");
		residenceCurrentCarrierLOVs.add("Great American");
		residenceCurrentCarrierLOVs.add("Guaranty National");
		residenceCurrentCarrierLOVs.add("GuideOne");
		residenceCurrentCarrierLOVs.add("Hartford");
		residenceCurrentCarrierLOVs.add("Horace Mann");
		residenceCurrentCarrierLOVs.add("Infinity");
		residenceCurrentCarrierLOVs.add("Kemper");
		residenceCurrentCarrierLOVs.add("Leader");
		residenceCurrentCarrierLOVs.add("Liberty Mutual");
		residenceCurrentCarrierLOVs.add("Mercury");
		residenceCurrentCarrierLOVs.add("Met Life");
		residenceCurrentCarrierLOVs.add("Mid-Century");
		residenceCurrentCarrierLOVs.add("Nationwide");
		residenceCurrentCarrierLOVs.add("North Pacific");
		residenceCurrentCarrierLOVs.add("Omaha");
		residenceCurrentCarrierLOVs.add("Omni");
		residenceCurrentCarrierLOVs.add("Progressive");
		residenceCurrentCarrierLOVs.add("Prudential");
		residenceCurrentCarrierLOVs.add("Regal");
		residenceCurrentCarrierLOVs.add("Safeco");
		residenceCurrentCarrierLOVs.add("Safeway Insurance Group");
		residenceCurrentCarrierLOVs.add("State Farm");
		residenceCurrentCarrierLOVs.add("Travelers");
		residenceCurrentCarrierLOVs.add("Unigard");
		residenceCurrentCarrierLOVs.add("Unitrin");
		residenceCurrentCarrierLOVs.add("USAA");
		residenceCurrentCarrierLOVs.add("Viking");
		residenceCurrentCarrierLOVs.add("Wawanesa");
		residenceCurrentCarrierLOVs.add("Windsor");
		residenceCurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> autoPPACurrentCarrierLOVs = new ArrayList<String>();
	static {
		autoPPACurrentCarrierLOVs.add("");
		autoPPACurrentCarrierLOVs.add("ACIC Non-Conversion");
		autoPPACurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		autoPPACurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		autoPPACurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		autoPPACurrentCarrierLOVs.add("21st Century");
		autoPPACurrentCarrierLOVs.add("AIG");
		autoPPACurrentCarrierLOVs.add("Allied");
		autoPPACurrentCarrierLOVs.add("Allstate");
		autoPPACurrentCarrierLOVs.add("American Family");
		autoPPACurrentCarrierLOVs.add("American National");
		autoPPACurrentCarrierLOVs.add("AMEX");
		autoPPACurrentCarrierLOVs.add("AMICA Mutual");
		autoPPACurrentCarrierLOVs.add("CA Casualty");
		autoPPACurrentCarrierLOVs.add("Charter Oak Fire");
		autoPPACurrentCarrierLOVs.add("Chubb");
		autoPPACurrentCarrierLOVs.add("Commerce West");
		autoPPACurrentCarrierLOVs.add("Country");
		autoPPACurrentCarrierLOVs.add("CSAA IG");
		autoPPACurrentCarrierLOVs.add("Dairyland");
		autoPPACurrentCarrierLOVs.add("Deerbrook");
		autoPPACurrentCarrierLOVs.add("Encompass");
		autoPPACurrentCarrierLOVs.add("Farm Bureau");
		autoPPACurrentCarrierLOVs.add("Farmers");
		autoPPACurrentCarrierLOVs.add("Foremost");
		autoPPACurrentCarrierLOVs.add("GEICO");
		autoPPACurrentCarrierLOVs.add("GMAC");
		autoPPACurrentCarrierLOVs.add("Grange");
		autoPPACurrentCarrierLOVs.add("Great American");
		autoPPACurrentCarrierLOVs.add("Guaranty National");
		autoPPACurrentCarrierLOVs.add("GuideOne");
		autoPPACurrentCarrierLOVs.add("Hartford");
		autoPPACurrentCarrierLOVs.add("Horace Mann");
		autoPPACurrentCarrierLOVs.add("Infinity");
		autoPPACurrentCarrierLOVs.add("Kemper");
		autoPPACurrentCarrierLOVs.add("Leader");
		autoPPACurrentCarrierLOVs.add("Liberty Mutual");
		autoPPACurrentCarrierLOVs.add("Mercury");
		autoPPACurrentCarrierLOVs.add("Met Life");
		autoPPACurrentCarrierLOVs.add("Mid-Century");
		autoPPACurrentCarrierLOVs.add("Nationwide");
		autoPPACurrentCarrierLOVs.add("North Pacific");
		autoPPACurrentCarrierLOVs.add("Omaha");
		autoPPACurrentCarrierLOVs.add("Omni");
		autoPPACurrentCarrierLOVs.add("Progressive");
		autoPPACurrentCarrierLOVs.add("Prudential");
		autoPPACurrentCarrierLOVs.add("Regal");
		autoPPACurrentCarrierLOVs.add("Safeco");
		autoPPACurrentCarrierLOVs.add("Safeway Insurance Group");
		autoPPACurrentCarrierLOVs.add("State Farm");
		autoPPACurrentCarrierLOVs.add("Travelers");
		autoPPACurrentCarrierLOVs.add("Unigard");
		autoPPACurrentCarrierLOVs.add("Unitrin");
		autoPPACurrentCarrierLOVs.add("USAA");
		autoPPACurrentCarrierLOVs.add("Viking");
		autoPPACurrentCarrierLOVs.add("Wawanesa");
		autoPPACurrentCarrierLOVs.add("Windsor");
		autoPPACurrentCarrierLOVs.add("Workmens Auto");
	}
	private static ArrayList<String> autoAntiqueCurrentCarrierLOVs = new ArrayList<String>();
	static {
		autoAntiqueCurrentCarrierLOVs.add("");
		autoAntiqueCurrentCarrierLOVs.add("ACIC Non-Conversion");
		autoAntiqueCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		autoAntiqueCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		autoAntiqueCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		autoAntiqueCurrentCarrierLOVs.add("21st Century");
		autoAntiqueCurrentCarrierLOVs.add("AIG");
		autoAntiqueCurrentCarrierLOVs.add("Allied");
		autoAntiqueCurrentCarrierLOVs.add("Allstate");
		autoAntiqueCurrentCarrierLOVs.add("American Family");
		autoAntiqueCurrentCarrierLOVs.add("American Modern");
		autoAntiqueCurrentCarrierLOVs.add("American National");
		autoAntiqueCurrentCarrierLOVs.add("AMEX");
		autoAntiqueCurrentCarrierLOVs.add("AMICA Mutual");
		autoAntiqueCurrentCarrierLOVs.add("CA Casualty");
		autoAntiqueCurrentCarrierLOVs.add("Charter Oak Fire");
		autoAntiqueCurrentCarrierLOVs.add("Chubb");
		autoAntiqueCurrentCarrierLOVs.add("Commerce West");
		autoAntiqueCurrentCarrierLOVs.add("Country");
		autoAntiqueCurrentCarrierLOVs.add("CSAA IG");
		autoAntiqueCurrentCarrierLOVs.add("Dairyland");
		autoAntiqueCurrentCarrierLOVs.add("Deerbrook");
		autoAntiqueCurrentCarrierLOVs.add("Encompass");
		autoAntiqueCurrentCarrierLOVs.add("Farm Bureau");
		autoAntiqueCurrentCarrierLOVs.add("Farmers");
		autoAntiqueCurrentCarrierLOVs.add("Foremost");
		autoAntiqueCurrentCarrierLOVs.add("GEICO");
		autoAntiqueCurrentCarrierLOVs.add("GMAC");
		autoAntiqueCurrentCarrierLOVs.add("Grange");
		autoAntiqueCurrentCarrierLOVs.add("Great American");
		autoAntiqueCurrentCarrierLOVs.add("Guaranty National");
		autoAntiqueCurrentCarrierLOVs.add("GuideOne");
		autoAntiqueCurrentCarrierLOVs.add("Hartford");
		autoAntiqueCurrentCarrierLOVs.add("Horace Mann");
		autoAntiqueCurrentCarrierLOVs.add("Infinity");
		autoAntiqueCurrentCarrierLOVs.add("Kemper");
		autoAntiqueCurrentCarrierLOVs.add("Leader");
		autoAntiqueCurrentCarrierLOVs.add("Liberty Mutual");
		autoAntiqueCurrentCarrierLOVs.add("Mercury");
		autoAntiqueCurrentCarrierLOVs.add("Met Life");
		autoAntiqueCurrentCarrierLOVs.add("Mid-Century");
		autoAntiqueCurrentCarrierLOVs.add("Nationwide");
		autoAntiqueCurrentCarrierLOVs.add("North Pacific");
		autoAntiqueCurrentCarrierLOVs.add("Omaha");
		autoAntiqueCurrentCarrierLOVs.add("Omni");
		autoAntiqueCurrentCarrierLOVs.add("Progressive");
		autoAntiqueCurrentCarrierLOVs.add("Prudential");
		autoAntiqueCurrentCarrierLOVs.add("Regal");
		autoAntiqueCurrentCarrierLOVs.add("Safeco");
		autoAntiqueCurrentCarrierLOVs.add("Safeway Insurance Group");
		autoAntiqueCurrentCarrierLOVs.add("State Farm");
		autoAntiqueCurrentCarrierLOVs.add("Travelers");
		autoAntiqueCurrentCarrierLOVs.add("Unigard");
		autoAntiqueCurrentCarrierLOVs.add("Unitrin");
		autoAntiqueCurrentCarrierLOVs.add("USAA");
		autoAntiqueCurrentCarrierLOVs.add("Viking");
		autoAntiqueCurrentCarrierLOVs.add("Wawanesa");
		autoAntiqueCurrentCarrierLOVs.add("Windsor");
		autoAntiqueCurrentCarrierLOVs.add("Workmens Auto");
	}
	private static ArrayList<String> motorcycleCurrentCarrierLOVs = new ArrayList<String>();
	static {
		motorcycleCurrentCarrierLOVs.add("");
		motorcycleCurrentCarrierLOVs.add("ACIC Non-Conversion");
		motorcycleCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		motorcycleCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		motorcycleCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		motorcycleCurrentCarrierLOVs.add("21st Century");
		motorcycleCurrentCarrierLOVs.add("AIG");
		motorcycleCurrentCarrierLOVs.add("Allied");
		motorcycleCurrentCarrierLOVs.add("Allstate");
		motorcycleCurrentCarrierLOVs.add("American Family");
		motorcycleCurrentCarrierLOVs.add("American National");
		motorcycleCurrentCarrierLOVs.add("AMEX");
		motorcycleCurrentCarrierLOVs.add("AMICA Mutual");
		motorcycleCurrentCarrierLOVs.add("CA Casualty");
		motorcycleCurrentCarrierLOVs.add("Charter Oak Fire");
		motorcycleCurrentCarrierLOVs.add("Chubb");
		motorcycleCurrentCarrierLOVs.add("Commerce West");
		motorcycleCurrentCarrierLOVs.add("Country");
		motorcycleCurrentCarrierLOVs.add("CSAA IG");
		motorcycleCurrentCarrierLOVs.add("Dairyland");
		motorcycleCurrentCarrierLOVs.add("Deerbrook");
		motorcycleCurrentCarrierLOVs.add("Encompass");
		motorcycleCurrentCarrierLOVs.add("Farm Bureau");
		motorcycleCurrentCarrierLOVs.add("Farmers");
		motorcycleCurrentCarrierLOVs.add("Foremost");
		motorcycleCurrentCarrierLOVs.add("GEICO");
		motorcycleCurrentCarrierLOVs.add("GMAC");
		motorcycleCurrentCarrierLOVs.add("Grange");
		motorcycleCurrentCarrierLOVs.add("Great American");
		motorcycleCurrentCarrierLOVs.add("Guaranty National");
		motorcycleCurrentCarrierLOVs.add("GuideOne");
		motorcycleCurrentCarrierLOVs.add("Hartford");
		motorcycleCurrentCarrierLOVs.add("Horace Mann");
		motorcycleCurrentCarrierLOVs.add("Infinity");
		motorcycleCurrentCarrierLOVs.add("Kemper");
		motorcycleCurrentCarrierLOVs.add("Leader");
		motorcycleCurrentCarrierLOVs.add("Liberty Mutual");
		motorcycleCurrentCarrierLOVs.add("Mercury");
		motorcycleCurrentCarrierLOVs.add("Met Life");
		motorcycleCurrentCarrierLOVs.add("Mid-Century");
		motorcycleCurrentCarrierLOVs.add("Nationwide");
		motorcycleCurrentCarrierLOVs.add("North Pacific");
		motorcycleCurrentCarrierLOVs.add("Omaha");
		motorcycleCurrentCarrierLOVs.add("Omni");
		motorcycleCurrentCarrierLOVs.add("Progressive");
		motorcycleCurrentCarrierLOVs.add("Prudential");
		motorcycleCurrentCarrierLOVs.add("Regal");
		motorcycleCurrentCarrierLOVs.add("Safeco");
		motorcycleCurrentCarrierLOVs.add("Safeway Insurance Group");
		motorcycleCurrentCarrierLOVs.add("State Farm");
		motorcycleCurrentCarrierLOVs.add("Travelers");
		motorcycleCurrentCarrierLOVs.add("Unigard");
		motorcycleCurrentCarrierLOVs.add("Unitrin");
		motorcycleCurrentCarrierLOVs.add("USAA");
		motorcycleCurrentCarrierLOVs.add("Viking");
		motorcycleCurrentCarrierLOVs.add("Wawanesa");
		motorcycleCurrentCarrierLOVs.add("Windsor");
		motorcycleCurrentCarrierLOVs.add("Workmens Auto");
	}
	
	private static ArrayList<String> motorHomeCurrentCarrierLOVs = new ArrayList<String>();
	static {
		motorHomeCurrentCarrierLOVs.add("");
		motorHomeCurrentCarrierLOVs.add("ACIC Non-Conversion");
		motorHomeCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		motorHomeCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		motorHomeCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		motorHomeCurrentCarrierLOVs.add("21st Century");
		motorHomeCurrentCarrierLOVs.add("AIG");
		motorHomeCurrentCarrierLOVs.add("Allied");
		motorHomeCurrentCarrierLOVs.add("Allstate");
		motorHomeCurrentCarrierLOVs.add("American Family");
		motorHomeCurrentCarrierLOVs.add("American National");
		motorHomeCurrentCarrierLOVs.add("AMEX");
		motorHomeCurrentCarrierLOVs.add("AMICA Mutual");
		motorHomeCurrentCarrierLOVs.add("CA Casualty");
		motorHomeCurrentCarrierLOVs.add("Charter Oak Fire");
		motorHomeCurrentCarrierLOVs.add("Chubb");
		motorHomeCurrentCarrierLOVs.add("Commerce West");
		motorHomeCurrentCarrierLOVs.add("Country");
		motorHomeCurrentCarrierLOVs.add("CSAA IG");
		motorHomeCurrentCarrierLOVs.add("Dairyland");
		motorHomeCurrentCarrierLOVs.add("Deerbrook");
		motorHomeCurrentCarrierLOVs.add("Encompass");
		motorHomeCurrentCarrierLOVs.add("Farm Bureau");
		motorHomeCurrentCarrierLOVs.add("Farmers");
		motorHomeCurrentCarrierLOVs.add("Foremost");
		motorHomeCurrentCarrierLOVs.add("GEICO");
		motorHomeCurrentCarrierLOVs.add("GMAC");
		motorHomeCurrentCarrierLOVs.add("Grange");
		motorHomeCurrentCarrierLOVs.add("Great American");
		motorHomeCurrentCarrierLOVs.add("Guaranty National");
		motorHomeCurrentCarrierLOVs.add("GuideOne");
		motorHomeCurrentCarrierLOVs.add("Hartford");
		motorHomeCurrentCarrierLOVs.add("Horace Mann");
		motorHomeCurrentCarrierLOVs.add("Infinity");
		motorHomeCurrentCarrierLOVs.add("Kemper");
		motorHomeCurrentCarrierLOVs.add("Leader");
		motorHomeCurrentCarrierLOVs.add("Liberty Mutual");
		motorHomeCurrentCarrierLOVs.add("Mercury");
		motorHomeCurrentCarrierLOVs.add("Met Life");
		motorHomeCurrentCarrierLOVs.add("Mid-Century");
		motorHomeCurrentCarrierLOVs.add("Nationwide");
		motorHomeCurrentCarrierLOVs.add("North Pacific");
		motorHomeCurrentCarrierLOVs.add("Omaha");
		motorHomeCurrentCarrierLOVs.add("Omni");
		motorHomeCurrentCarrierLOVs.add("Progressive");
		motorHomeCurrentCarrierLOVs.add("Prudential");
		motorHomeCurrentCarrierLOVs.add("Regal");
		motorHomeCurrentCarrierLOVs.add("Safeco");
		motorHomeCurrentCarrierLOVs.add("Safeway Insurance Group");
		motorHomeCurrentCarrierLOVs.add("State Farm");
		motorHomeCurrentCarrierLOVs.add("Travelers");
		motorHomeCurrentCarrierLOVs.add("Unigard");
		motorHomeCurrentCarrierLOVs.add("Unitrin");
		motorHomeCurrentCarrierLOVs.add("USAA");
		motorHomeCurrentCarrierLOVs.add("Viking");
		motorHomeCurrentCarrierLOVs.add("Wawanesa");
		motorHomeCurrentCarrierLOVs.add("Windsor");
		motorHomeCurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> watercraftCurrentCarrierLOVs = new ArrayList<String>();
	static {
		watercraftCurrentCarrierLOVs.add("");
		watercraftCurrentCarrierLOVs.add("ACIC Non-Conversion");
		watercraftCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		watercraftCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		watercraftCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		watercraftCurrentCarrierLOVs.add("21st Century");
		watercraftCurrentCarrierLOVs.add("AIG");
		watercraftCurrentCarrierLOVs.add("Allied");
		watercraftCurrentCarrierLOVs.add("Allstate");
		watercraftCurrentCarrierLOVs.add("American Family");
		watercraftCurrentCarrierLOVs.add("American National");
		watercraftCurrentCarrierLOVs.add("AMEX");
		watercraftCurrentCarrierLOVs.add("AMICA Mutual");
		watercraftCurrentCarrierLOVs.add("CA Casualty");
		watercraftCurrentCarrierLOVs.add("Charter Oak Fire");
		watercraftCurrentCarrierLOVs.add("Chubb");
		watercraftCurrentCarrierLOVs.add("Commerce West");
		watercraftCurrentCarrierLOVs.add("Country");
		watercraftCurrentCarrierLOVs.add("CSAA IG");
		watercraftCurrentCarrierLOVs.add("Dairyland");
		watercraftCurrentCarrierLOVs.add("Deerbrook");
		watercraftCurrentCarrierLOVs.add("Encompass");
		watercraftCurrentCarrierLOVs.add("Farm Bureau");
		watercraftCurrentCarrierLOVs.add("Farmers");
		watercraftCurrentCarrierLOVs.add("Foremost");
		watercraftCurrentCarrierLOVs.add("GEICO");
		watercraftCurrentCarrierLOVs.add("GMAC");
		watercraftCurrentCarrierLOVs.add("Grange");
		watercraftCurrentCarrierLOVs.add("Great American");
		watercraftCurrentCarrierLOVs.add("Guaranty National");
		watercraftCurrentCarrierLOVs.add("GuideOne");
		watercraftCurrentCarrierLOVs.add("Hartford");
		watercraftCurrentCarrierLOVs.add("Horace Mann");
		watercraftCurrentCarrierLOVs.add("Infinity");
		watercraftCurrentCarrierLOVs.add("Kemper");
		watercraftCurrentCarrierLOVs.add("Leader");
		watercraftCurrentCarrierLOVs.add("Liberty Mutual");
		watercraftCurrentCarrierLOVs.add("Mercury");
		watercraftCurrentCarrierLOVs.add("Met Life");
		watercraftCurrentCarrierLOVs.add("Mid-Century");
		watercraftCurrentCarrierLOVs.add("Nationwide");
		watercraftCurrentCarrierLOVs.add("North Pacific");
		watercraftCurrentCarrierLOVs.add("Omaha");
		watercraftCurrentCarrierLOVs.add("Omni");
		watercraftCurrentCarrierLOVs.add("Progressive");
		watercraftCurrentCarrierLOVs.add("Prudential");
		watercraftCurrentCarrierLOVs.add("Regal");
		watercraftCurrentCarrierLOVs.add("Safeco");
		watercraftCurrentCarrierLOVs.add("Safeway Insurance Group");
		watercraftCurrentCarrierLOVs.add("State Farm");
		watercraftCurrentCarrierLOVs.add("Travelers");
		watercraftCurrentCarrierLOVs.add("Unigard");
		watercraftCurrentCarrierLOVs.add("Unitrin");
		watercraftCurrentCarrierLOVs.add("USAA");
		watercraftCurrentCarrierLOVs.add("Viking");
		watercraftCurrentCarrierLOVs.add("Wawanesa");
		watercraftCurrentCarrierLOVs.add("Windsor");
		watercraftCurrentCarrierLOVs.add("Workmens Auto");
	}

	private static ArrayList<String> recrVehicleCurrentCarrierLOVs = new ArrayList<String>();
	static {
		recrVehicleCurrentCarrierLOVs.add("ACIC Non-Conversion");
		recrVehicleCurrentCarrierLOVs.add("AAA-Michigan (ACG)");
		recrVehicleCurrentCarrierLOVs.add("AAA-NoCal (CSAA IG)");
		recrVehicleCurrentCarrierLOVs.add("AAA-SoCal (ACSC)");
		recrVehicleCurrentCarrierLOVs.add("21st Century");
		recrVehicleCurrentCarrierLOVs.add("AIG");
		recrVehicleCurrentCarrierLOVs.add("Allied");
		recrVehicleCurrentCarrierLOVs.add("Allstate");
		recrVehicleCurrentCarrierLOVs.add("American Family");
		recrVehicleCurrentCarrierLOVs.add("American Modern");
		recrVehicleCurrentCarrierLOVs.add("American National");
		recrVehicleCurrentCarrierLOVs.add("AMEX");
		recrVehicleCurrentCarrierLOVs.add("AMICA Mutual");
		recrVehicleCurrentCarrierLOVs.add("CA Casualty");
		recrVehicleCurrentCarrierLOVs.add("Charter Oak Fire");
		recrVehicleCurrentCarrierLOVs.add("Chubb");
		recrVehicleCurrentCarrierLOVs.add("Commerce West");
		recrVehicleCurrentCarrierLOVs.add("Country");
		recrVehicleCurrentCarrierLOVs.add("CSAA IG");
		recrVehicleCurrentCarrierLOVs.add("Dairyland");
		recrVehicleCurrentCarrierLOVs.add("Deerbrook");
		recrVehicleCurrentCarrierLOVs.add("Encompass");
		recrVehicleCurrentCarrierLOVs.add("Farm Bureau");
		recrVehicleCurrentCarrierLOVs.add("Farmers");
		recrVehicleCurrentCarrierLOVs.add("Foremost");
		recrVehicleCurrentCarrierLOVs.add("GEICO");
		recrVehicleCurrentCarrierLOVs.add("GMAC");
		recrVehicleCurrentCarrierLOVs.add("Grange");
		recrVehicleCurrentCarrierLOVs.add("Great American");
		recrVehicleCurrentCarrierLOVs.add("Guaranty National");
		recrVehicleCurrentCarrierLOVs.add("GuideOne");
		recrVehicleCurrentCarrierLOVs.add("Hartford");
		recrVehicleCurrentCarrierLOVs.add("Horace Mann");
		recrVehicleCurrentCarrierLOVs.add("Infinity");
		recrVehicleCurrentCarrierLOVs.add("Kemper");
		recrVehicleCurrentCarrierLOVs.add("Leader");
		recrVehicleCurrentCarrierLOVs.add("Liberty Mutual");
		recrVehicleCurrentCarrierLOVs.add("Mercury");
		recrVehicleCurrentCarrierLOVs.add("Met Life");
		recrVehicleCurrentCarrierLOVs.add("Mid-Century");
		recrVehicleCurrentCarrierLOVs.add("Nationwide");
		recrVehicleCurrentCarrierLOVs.add("North Pacific");
		recrVehicleCurrentCarrierLOVs.add("Omaha");
		recrVehicleCurrentCarrierLOVs.add("Omni");
		recrVehicleCurrentCarrierLOVs.add("Progressive");
		recrVehicleCurrentCarrierLOVs.add("Prudential");
		recrVehicleCurrentCarrierLOVs.add("Regal");
		recrVehicleCurrentCarrierLOVs.add("Safeco");
		recrVehicleCurrentCarrierLOVs.add("Safeway Insurance Group");
		recrVehicleCurrentCarrierLOVs.add("State Farm");
		recrVehicleCurrentCarrierLOVs.add("Travelers");
		recrVehicleCurrentCarrierLOVs.add("Unigard");
		recrVehicleCurrentCarrierLOVs.add("Unitrin");
		recrVehicleCurrentCarrierLOVs.add("USAA");
		recrVehicleCurrentCarrierLOVs.add("Viking");
		recrVehicleCurrentCarrierLOVs.add("Wawanesa");
		recrVehicleCurrentCarrierLOVs.add("Windsor");
		recrVehicleCurrentCarrierLOVs.add("Workmens Auto");
	}
}
