package aaa.modules.delta.templates;

import java.util.HashMap;
import java.util.Map;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
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
	
	public void TC01_createQuote(TestData td, String scenarioPolicyType) {
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
	
	//public void TC02_verifyEndorsements() {}
	
	
	public void TC02_verifyWindstormMitigationDiscount() {
		mainApp().open(); 
		
		TestData td_WindstormMitigationYes = getTestSpecificTD("TestData_WindstormMitigationYes"); 
		
		Map<String, String> windstormMitigationDiscount_row = new HashMap<>();
		windstormMitigationDiscount_row.put("Discount Category", "Safe Home");
		windstormMitigationDiscount_row.put("Discounts Applied", "Windstorm Mitigation"); 
		
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
	
	public void TC03_verifyELC() {
		mainApp().open(); 
		
		TestData td_None_with_Score599 = getTestSpecificTD("TestData_None_with_Score599"); 
		TestData td_Declined_with_Score999 = getTestSpecificTD("TestData_Declined_with_Score999"); 
		TestData td_IdentityTheft_with_Score750 = getTestSpecificTD("TestData_IdentityTheft_with_Score750"); 
		TestData td_MilitaryDeployment_with_Score599 = getTestSpecificTD("TestData_MilitaryDeployment_with_Score599"); 
		TestData td_OtherEvents_with_Score999 = getTestSpecificTD("TestData_OtherEvents_with_Score999"); 
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();		
		GeneralTab generalTab = new GeneralTab();
		generalTab.verifyFieldHasValue("Extraordinary Life Circumstance", "None"); 
		
		verifyELCNotApplied(td_Declined_with_Score999, "999");
		verifyELCNotApplied(td_IdentityTheft_with_Score750, "750");
		
		verifyELCApplied(td_MilitaryDeployment_with_Score599, "607");
		verifyELCApplied(td_OtherEvents_with_Score999, "607");
		
		verifyELCNotApplied(td_None_with_Score599, "599");
		
		ReportsTab.buttonSaveAndExit.click();		
		CustomAssert.assertAll();		
	}
	
	public void TC04_purchasePolicy(TestData td, String scenarioPolicyType) {
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
        
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC05_verifyODDPolicy() {} 
	
	
	public void TC06_verifyCancelNoticeTab(TestData td1, TestData td2) {
		String er9931 = "Cancellation effective date must be at least 34 days from today when the policy is within the new business discovery period.";
		
		mainApp().open(); 
		
		SearchPage.openPolicy(policyNumber);
		
		policy.cancelNotice().start(); 
		CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();
		CustomAssert.enableSoftMode();	
		CustomAssert.assertTrue(cancelNoticeTab.getAssetList().getAsset(HomeSSMetaData.CancelNoticeActionTab.DAYS_OF_NOTICE.getLabel()).getValue().toString().equals("34"));
		
		cancelNoticeTab.fillTab(td1);
		cancelNoticeTab.verifyFieldHasMessage(HomeSSMetaData.CancelNoticeActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel(), er9931); 
		
		cancelNoticeTab.fillTab(td2);
		cancelNoticeTab.submitTab();
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelCancelNotice.verify.present();
		CustomAssert.assertAll();
	}
	

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
	
}
