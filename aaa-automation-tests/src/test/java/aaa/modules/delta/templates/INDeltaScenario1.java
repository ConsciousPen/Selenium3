package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class INDeltaScenario1 extends BaseTest { 
	
	protected IPolicy policy;
	protected String quoteNumber;
	protected String policyNumber;
	
	public void TC_createQuote(String scenarioPolicyType) {
		TestData td = getTestSpecificTD("TestData");
		policy = getPolicyType().get();
		
		mainApp().open();		
        createCustomerIndividual();
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class, true); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA IN SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC_verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrier(immediatePriorCarrierLOVs);
		
		GeneralTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}
	
	public void TC_verifyEndorsementsTab() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		
		Map<String, String> endorsement_HS0312 = new HashMap<>();
		endorsement_HS0312.put("Form ID", "HS 03 12");
		endorsement_HS0312.put("Name", "Windstorm Or Hail Deductible - Percentage"); 
				
		Map<String, String> endorsement_HS0493 = new HashMap<>(); 
		endorsement_HS0493.put("Form ID", "HS 04 93"); 
		endorsement_HS0493.put("Name", "Actual Cash Value - Windstorm Or Hail Losses"); 
			
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab(); 

		CustomAssert.enableSoftMode();		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present();	
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present();	
			
			endorsementTab.fillTab(td_add_Forms);
			
			assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312)).exists();
			CustomAssert.assertTrue(endorsementTab.verifyLinkEditIsPresent("HS 03 12")); 
			CustomAssert.assertTrue(endorsementTab.verifyLinkRemoveIsPresent("HS 03 12"));
		}
		else if (getPolicyType().equals(PolicyType.HOME_SS_HO4)||getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present(false);	
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present(false);
			assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312)).isPresent(false);
		}
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}
	
	public void TC_verifyEndorsementHS2383() {	
		TestData td_hs2383 = getTestSpecificTD("TestData_addHS2383"); 
		
		Map<String, String> endorsement_HS2383 = new HashMap<>(); 
		endorsement_HS2383.put("Form ID", "HS 23 83"); 
		endorsement_HS2383.put("Name", "Mine Subsidence Endorsement"); 
		
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab(); 
		
		CustomAssert.enableSoftMode();
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS2383).verify.present(false);		
		assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS2383)).isPresent(false);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		new ApplicantTab().fillTab(td_hs2383); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		new ReportsTab().fillTab(td_hs2383); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS2383).verify.present();	
		
		endorsementTab.fillTab(td_hs2383);
		
		assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS2383)).exists();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();		
	}

	public void TC_verifyQuoteODD() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	

		policy.quoteDocGen().start();		
		CustomAssert.enableSoftMode();	
		//TODO add verification On-Demand Documents tab
		GenerateOnDemandDocumentActionTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}
	
	public void TC_verifyHailResistanceRating() {
		TestData td_hailResistanceRating = getTestSpecificTD("TestData_hailResistanceRating");
		
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();		
		CustomAssert.enableSoftMode();
		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)||getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
			HssQuoteDataGatherHelper.verifyHailResistanceRatingNotApplied();
			HssQuoteDataGatherHelper.verifyHailResistanceRatingApplied(td_hailResistanceRating);
		}
		else if (getPolicyType().equals(PolicyType.HOME_SS_HO4)||getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			HssQuoteDataGatherHelper.verifyHailResistanceRatingNotDisplaying();
		} 
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();			
	}
	
	public void TC_verifyIneligibleRoofType() {
		TestData td_eligibleData = getTestSpecificTD("TestData");
		TestData td_ineligibleRoofType = getTestSpecificTD("TestData_IneligibleRoofType");
		
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();		
		CustomAssert.enableSoftMode();	
		
		HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_ineligibleRoofType, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560);
		
		HssQuoteDataGatherHelper.fillPropertyInfoTabWithCorrectData(td_eligibleData);
		/*
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
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS10030560);
		errorTab.cancel();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(td);
		*/
		
		PropertyInfoTab.buttonSaveAndExit.click();		
		CustomAssert.assertAll();
	}
	
	public void TC_purchasePolicy(String scenarioPolicyType) {
		TestData td = getTestSpecificTD("TestData");
		
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA IN SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}

	public void TC_verifyPolicyODD() {
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);
		//TODO verify On-Demand Documents generation
	}

	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<String>();
	static {
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("Allied");
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("Amco Ins Co");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("American Modern");
		immediatePriorCarrierLOVs.add("American National");
		immediatePriorCarrierLOVs.add("Auto Owners");
		immediatePriorCarrierLOVs.add("Chartis");
		immediatePriorCarrierLOVs.add("Cincinnati");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Foremost");
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("IDS");
		immediatePriorCarrierLOVs.add("Kemper");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Metropolitan");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Other Carrier");
		immediatePriorCarrierLOVs.add("Owners Insurance");
		immediatePriorCarrierLOVs.add("Pacific Indemnity");
		immediatePriorCarrierLOVs.add("SafeCo");
		immediatePriorCarrierLOVs.add("Sentinel Insurance");
		immediatePriorCarrierLOVs.add("Standard Guaranty");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("None");
	}
	
}
