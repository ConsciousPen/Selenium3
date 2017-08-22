package aaa.modules.delta.templates;

import java.util.Arrays;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

public class DCDeltaScenario1 extends BaseTest {
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
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC_verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		GeneralTab generalTab = new GeneralTab();
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("AAA-Michigan (ACG)", "AAA Other", "AAA-Michigan (ACG)", "AAA-NoCal (CSAA IG) Rewrite", 
						"AAA-NoCal (CSAA IG) Sold/Bought", "AAA-SoCal (ACSC)", "AIG", "Alfa Insurance", "Allstate", "Assurant",  "Chubb", 
						"Compliant w/o Ins-Military", "Compliant w/o Ins-Military AAA", "Compliant w/o Ins-Newly Purchased", "Donegal", "Erie", 
						"Farmers", "GMAC", "Hanover", "Harleysville", "Hartford", "High Point", "Homesite", "Kemper", "Liberty Mutual", 
						"Loudoun Mutual", "Mercury", "MetLife", "Nationwide", "NJ Cure", "NJ Manufacturers", "NJ Skylands", "No Prior", 
						"Northern Neck", "Ohio Casualty", "Other Carrier", "Palisades", "Proformance", "Rockingham Mutual", "Safeco", 
						"Selective", "State Farm", "Travelers", "USAA", "VA Farm Bureau", "Western United", "White Mountains")); 
		
		GeneralTab.buttonSaveAndExit.click();
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
        
        log.info("DELTA DC SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);	 
	}
	
	public void TC_verifyDeclarationDocumentsGenerated() {
		 //TODO add verification that Declaration documents are generated at NB policy issue and in DB. 
	}
	
	public void TC_verifyPolicyODD() {
		//TODO add verification of On-Demand Documents Tab
	}
}
