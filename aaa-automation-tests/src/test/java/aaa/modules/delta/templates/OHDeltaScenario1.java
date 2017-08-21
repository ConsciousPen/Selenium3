package aaa.modules.delta.templates;

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
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class OHDeltaScenario1 extends BaseTest {
	protected IPolicy policy;
	protected String quoteNumber;
	protected String policyNumber;
	
	public void TC_createQuote(TestData td, String scenarioPolicyType) {
		policy = getPolicyType().get();
		
		mainApp().open();
		
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class, true); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA OH SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC_verifyImmediatePriorCarrier() {
		mainApp().open(); 

		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();			
		GeneralTab generalTab = new GeneralTab();		
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).setValue("First Time Homebuyer"); 
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER.getLabel()).verify.present(false); 
		
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).setValue("No Prior"); 
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER.getLabel()).verify.present(false); 
		
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).setValue("Allstate"); 
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER.getLabel()).verify.present(); 
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER).setValue("5");
		
		GeneralTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}
	
	public void TC_verifyEndorsements(TestData td_forms) {
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
	
	public void TC_verifyHailResistanceRating() {
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
	
	public void TC_verifyRoofTypeUneligible(TestData td) {
		mainApp().open(); 
		
		TestData td_RoofTypeUneligible = getTestSpecificTD("TestData_RoofTypeUneligible");
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab(); 
		propertyInfoTab.fillTab(td_RoofTypeUneligible);
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE).setValue("Wood shingle/Wood shake");
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS10030560);
		errorTab.cancel();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(td);
		PropertyInfoTab.buttonSaveAndExit.click();
		
		CustomAssert.assertAll();
	}
	
	public void TC_purchasePolicy(TestData td, String scenarioPolicyType) {
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
        
        log.info("DELTA OH SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC_verifyODDPolicy() {}
	
	private void verifyHailResistanceRating_NotApplied() {
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING.getLabel()).verify.present(); 
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
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING).setValue("3");
		String hailResistanceRating = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING.getLabel()).getValue().toString(); 
		
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
		propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CONSTRUCTION).getAsset(HomeSSMetaData.PropertyInfoTab.Construction.HAIL_RESISTANCE_RATING.getLabel()).verify.present(false); 
		
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
