package aaa.modules.delta.templates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class KSDeltaScenario1 extends BaseTest {
	protected IPolicy policy;
	protected String quoteNumber;
	protected String policyNumber;
	protected String effectiveDate;
	
	public void TC01_createQuote(TestData td, String scenarioPolicyType) {
		policy = getPolicyType().get();
		
		mainApp().open();
		
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class, true); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA KS SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);
        
        effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue(); 		
	}
	
	public void TC02_verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		GeneralTab generalTab = new GeneralTab();
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("AAA-Michigan (ACG)", "AAA-NoCal (CSAA IG) Rewrite", "AAA-NoCal (CSAA IG) Sold/Bought", "AAA-SoCal (ACSC)",
						"Allied", "Allstate", "Amco Ins Co", "American Family", "American National", "Auto Owners", "Chartis",
						"Cincinnati", "CSAA IG", "CSE Safeguard", "Farm Bureau", "Farmers", "Fire Insurance", "First Time Homebuyer",
						"Foremost", "Hartford", "Homesite", "Liberty Mutual", "Metropolitan", "Nationwide", "No Prior",
						"Other Carrier", "Owners Insurance", "Pacific Indemnity", "Safeco", "Shelter Mutual",
						"State Farm", "Travelers", "Unigard", "USAA")); 
		
		GeneralTab.buttonSaveAndExit.click();
	}
	
	public void TC03_verifyEndorsements(TestData td_forms) {
		mainApp().open();
		
		Map<String, String> endorsement_HS0312 = new HashMap<>();
		endorsement_HS0312.put("Form ID", "HS 03 12");
		endorsement_HS0312.put("Name", "Windstorm Or Hail Deductible - Percentage"); 
				
		Map<String, String> endorsement_HS0493 = new HashMap<>(); 
		endorsement_HS0493.put("Form ID", "HS 04 93"); 
		endorsement_HS0493.put("Name", "Actual Cash Value - Windstorm Or Hail Losses"); 
		
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab(); 

		CustomAssert.enableSoftMode();
		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present();	
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present();
			
			endorsementTab.fillTab(td_forms);
			
			endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present();			
			CustomAssert.assertTrue(endorsementTab.verifyLinkEditIsPresent("HS 03 12")); 
			CustomAssert.assertTrue(endorsementTab.verifyLinkRemoveIsPresent("HS 03 12"));
		}
		else if (getPolicyType().equals(PolicyType.HOME_SS_HO4)||getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present(false);	
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present(false);		
			endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present(false);
		}
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}
	
	public void TC04_verifyELC() {
		mainApp().open(); 
		
		TestData td_None_with_Score740 = getTestSpecificTD("TestData_None_with_Score740"); 
		TestData td_Declined_with_Score999 = getTestSpecificTD("TestData_Declined_with_Score999"); 
		TestData td_IdentityTheft_with_Score750 = getTestSpecificTD("TestData_IdentityTheft_with_Score750"); 
		TestData td_MilitaryDeployment_with_Score740 = getTestSpecificTD("TestData_MilitaryDeployment_with_Score740"); 
		TestData td_OtherEvents_with_Score999 = getTestSpecificTD("TestData_OtherEvents_with_Score999"); 
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();		
		GeneralTab generalTab = new GeneralTab();
		generalTab.verifyFieldHasValue("Extraordinary Life Circumstance", "None"); 
		
		verifyELCNotApplied(td_Declined_with_Score999, "999");
		verifyELCNotApplied(td_IdentityTheft_with_Score750, "750");
		
		verifyELCApplied(td_MilitaryDeployment_with_Score740, "745");
		verifyELCApplied(td_OtherEvents_with_Score999, "745");
		
		//verify AAA_HO_SS7230342 - "Underwriting approval is required for the option you have selected"
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS7230342);
		errorTab.cancel();
		
		verifyELCNotApplied(td_None_with_Score740, "740");
		
		ReportsTab.buttonSaveAndExit.click();		
		CustomAssert.assertAll();	
	}
	
	public void TC05_verifyHailResistanceRating() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();		
		CustomAssert.enableSoftMode();
		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)||getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
			verifyHailResistanceRating_NotApplied();
			verifyHailResistanceRating_Applied();
		}
		else if (getPolicyType().equals(PolicyType.HOME_SS_HO4)||getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			verifyHailResistanceRating_NotDisplaying();
		} 
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();	
	}
	
	public void TC06_purchasePolicy(TestData td, String scenarioPolicyType) {
		mainApp().open(); 
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA KS SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC07_verifyODDPolicy() {}

	
	private void verifyELCNotApplied(TestData td, String scoreInRatingDetails) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("FR Score value is wrong in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score").equals(scoreInRatingDetails));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		reportsTab.lblELCMessage.verify.present(false);
	}

	private void verifyELCApplied(TestData td, String scoreInRatingDetails) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("FR Score value is wrong in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score").equals(scoreInRatingDetails));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		CustomAssert.assertTrue("Extraordinary life circumstance is not applied on Reports Tab",
				reportsTab.lblELCMessage.getValue().equals("Extraordinary life circumstance was applied to the policy effective "+effectiveDate));		
	}	

	private void verifyHailResistanceRating_NotApplied() {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).verify.present();
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).setValue("No");
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium();
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("Hail Resistive Rating: wrong value in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Hail Resistive Rating").equals("No"));
		CustomAssert.assertTrue("Hail zone flag: wrong value in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Hail zone flag").equals("No"));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
	}
	
	private void verifyHailResistanceRating_Applied() {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		TestData td_hailResistanceRating = getTestSpecificTD("TestData_hailResistanceRating");
		propertyInfoTab.fillTab(td_hailResistanceRating);
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).setAnyValueExcept("No");
		String hailResistanceRating = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).getValue();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium();
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("Hail Resistive Rating: wrong value in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Hail Resistive Rating").equals(hailResistanceRating));
		CustomAssert.assertTrue("Hail zone flag: wrong value in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Hail zone flag").equals("Yes"));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
	}
	
	private void verifyHailResistanceRating_NotDisplaying() {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).verify.present(false);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.enableSoftMode();
		CustomAssert.assertFalse("Hail Resistive Rating is present in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.propertyInformation.getLabel("Hail Resistive Rating").isPresent());
		CustomAssert.assertFalse("Hail zone flag is present in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getLabel("Hail zone flag").isPresent());
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();	
	}
}
