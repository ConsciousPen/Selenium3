package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class CTDeltaScenario1 extends BaseTest { 
	
	protected IPolicy policy;
	protected String quoteNumber;
	protected String policyNumber;
	protected String effectiveDate;
	
	public void TC_createQuote(String scenarioPolicyType) {
		TestData td = getTestSpecificTD("TestData");
		policy = getPolicyType().get();
		
		mainApp().open();		
        createCustomerIndividual();
        
        policy.initiate();        
        policy.getDefaultView().fillUpTo(td, BindTab.class, true); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);
        
        effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue(); 		
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
	
	//public void TC_verifyEndorsements() {}	
	
	public void TC_verifyWindstormMitigationDiscount() {		
		TestData td_WindstormMitigationYes = getTestSpecificTD("TestData_WindstormMitigationYes"); 
		
		Map<String, String> windstormMitigationDiscount_row = new HashMap<>();
		windstormMitigationDiscount_row.put("Discount Category", "Safe Home");
		windstormMitigationDiscount_row.put("Discounts Applied", "Windstorm Mitigation"); 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab(); 
		premiumsTab.calculatePremium(); 
		
		CustomAssert.enableSoftMode();		
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(windstormMitigationDiscount_row).verify.present(false);
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("Windstorm Mitigation Discount: wrong value in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Windstorm Mitigation Discount").equals("")); 
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab(); 
		
		String distanceToCoast = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RISKMETER).getAsset(HomeSSMetaData.PropertyInfoTab.Riskmeter.DISTANCE_TO_COAST_MILES.getLabel()).getValue().toString();
		log.info("Distance to coast value is "+distanceToCoast);
		String elevation = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RISKMETER).getAsset(HomeSSMetaData.PropertyInfoTab.Riskmeter.ELEVATION_FEET.getLabel()).getValue().toString(); 
		log.info("Elevation value is "+elevation);
		
		propertyInfoTab.fillTab(td_WindstormMitigationYes);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium(); 
				
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(windstormMitigationDiscount_row).verify.present(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertFalse("Windstorm Mitigation Discount: wrong value in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Windstorm Mitigation Discount").equals("0.00")); 
		if (getPolicyType().equals("HO3")||getPolicyType().equals("DP3")) {
			CustomAssert.assertTrue("Distance to shore: wrong value in Rating Details", 
					PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Distance to shore").equals(distanceToCoast));
			CustomAssert.assertTrue("Elevation: wrong value in Rating Details", 
					PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Elevation").equals(elevation)); 
		} 
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();		
	}
	
	public void TC_verifyELC() {		
		TestData td_None_with_Score599 = getTestSpecificTD("TestData_None_with_Score599"); 
		TestData td_Declined_with_Score999 = getTestSpecificTD("TestData_Declined_with_Score999"); 
		TestData td_IdentityTheft_with_Score750 = getTestSpecificTD("TestData_IdentityTheft_with_Score750"); 
		TestData td_MilitaryDeployment_with_Score599 = getTestSpecificTD("TestData_MilitaryDeployment_with_Score599"); 
		TestData td_OtherEvents_with_Score999 = getTestSpecificTD("TestData_OtherEvents_with_Score999"); 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();		
		GeneralTab generalTab = new GeneralTab();
		generalTab.verifyFieldHasValue("Extraordinary Life Circumstance", "None"); 

		String messageOnReportsTab = "Extraordinary life circumstance was applied to the policy effective "+effectiveDate;
		
		HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_Declined_with_Score999, "999"); 
		HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_IdentityTheft_with_Score750, "750");
		
		HssQuoteDataGatherHelper.verifyBestFRScoreApplied(td_MilitaryDeployment_with_Score599, "607", messageOnReportsTab);
		HssQuoteDataGatherHelper.verifyBestFRScoreApplied(td_OtherEvents_with_Score999, "607", messageOnReportsTab);
		
		HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_None_with_Score599, "599");
		
		ReportsTab.buttonSaveAndExit.click();		
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
        
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC_verifyODDPolicy() {
		//TODO add
	} 
	
	public void TC_verifyCancelNoticeTab() {
		TestData td_plus34days = getTestSpecificTD("TestData_Plus34Days");
		
		String error_9931 = "Cancellation effective date must be at least 34 days from today when the policy is within the new business discovery period.";
		String error_9208 = "Cancellation effective date must be before the end of the policy term.";
		
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);
		
		policy.cancelNotice().start(); 
		CustomAssert.enableSoftMode();	
		
		HssQuoteDataGatherHelper.verifyDaysOfNotice("34", 34, error_9931, error_9208);
		
		CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();
		cancelNoticeTab.fillTab(td_plus34days);
		CancelNoticeActionTab.buttonOk.click();
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelCancelNotice.verify.present();
		CustomAssert.assertAll();
	}
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<String>();
	static {
		immediatePriorCarrierLOVs.add("21st Century");
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("Allied");
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("American Modern");
		immediatePriorCarrierLOVs.add("American National");
		immediatePriorCarrierLOVs.add("Amica");
		immediatePriorCarrierLOVs.add("Andover");
		immediatePriorCarrierLOVs.add("Chartis");
		immediatePriorCarrierLOVs.add("CSE Safeguard");
		immediatePriorCarrierLOVs.add("Farm Bureau");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Fire Insurance");
		immediatePriorCarrierLOVs.add("First Time Homebuyer");
		immediatePriorCarrierLOVs.add("Foremost");
		immediatePriorCarrierLOVs.add("Hanover");
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Metropolitan");
		immediatePriorCarrierLOVs.add("Middlesex Mutual");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("New London County");
		immediatePriorCarrierLOVs.add("No Prior Insurance");
		immediatePriorCarrierLOVs.add("Other Carriers");
		immediatePriorCarrierLOVs.add("Owners Insurance");
		immediatePriorCarrierLOVs.add("Refusal");
		immediatePriorCarrierLOVs.add("SafeCo");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Tower Insurance");
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("Unigard");
		immediatePriorCarrierLOVs.add("Union Mutual");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("Vermont Mutual");		
	}
}
