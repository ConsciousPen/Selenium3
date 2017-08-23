package aaa.modules.delta.templates;

import java.util.Arrays;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class ORDeltaScenario1 extends BaseTest { 
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
        log.info("DELTA OR SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC_verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		GeneralTab generalTab = new GeneralTab();
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("AAA-Michigan (ACG)", "AAA-NoCal (CSAA IG) Rewrite", "AAA-NoCal (CSAA IG) Sold/Bought", "AAA-SoCal (ACSC)", 
						"Allstate", "American Family", "Country Mutual", "Farmers", "Fireman's Fund", "First Time Homebuyer", "Foremost", 
						"Hartford", "Homesite", "Liberty Insurance Corp.", "Liberty Mutual", "MET", "Mutual of Enumclaw Ins Co.", 
						"Nationwide", "Oregon Mutual", "Other", "Safeco", "State Farm", "Travelers", "United Svcs Automobile Assn", 
						"USAA", "None")); 
		
		GeneralTab.buttonSaveAndExit.click();
	}	
	
	public void TC_verifyUnderwritingApprovalTab() {
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		UnderwritingAndApprovalTab underwritingTab = new UnderwritingAndApprovalTab();
        underwritingTab.fillTab(td_uw1);
        underwritingTab.submitTab();
        
        CustomAssert.enableSoftMode();   
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
        	underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES.getLabel(), 
            	"Business or farming activity is ineligible. Dwellings or applicants that perform adult day care, or pet day care are unacceptable."); 
        }
        else {
        	underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES.getLabel(), 
        		"Risk must be endorsed with the appropriate business or farming endorsement when a business or incidental farming exposure is present and deemed eligible for coverage. Applicants that perform adult day care, or pet day care, are unacceptable"); 
        }        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        new BindTab().btnPurchase.click();
        
        ErrorTab errorTab = new ErrorTab(); 
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
        	errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3151364);
        }
        else {
        	errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3150198);
        }
		errorTab.cancel(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		underwritingTab.fillTab(td_uw2);		
		UnderwritingAndApprovalTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}
	
	public void TC_verifyClaims() {
		TestData td_Claims1 = getTestSpecificTD("TestData_Claims1");
		TestData td_Claims2 = getTestSpecificTD("TestData_Claims2");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_Claims1);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
        
		CustomAssert.enableSoftMode();   
		
        ErrorTab errorTab = new ErrorTab(); 	
        if (getPolicyType().equals(PolicyType.HOME_SS_HO4)) {
        	errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1050670_OR);
        }
        else {
        	errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1020340_OR);
        }
		errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12023000);
		errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12200234);
		errorTab.cancel(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		propertyInfoTab.fillTab(td_Claims2);
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
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA OR SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC_verifyODDPolicy() {
		//TODO verify AHAUXX - Consumer Information Notice is on On-Demand Documents tab, verify AHAUXX generation
	}
	
}
