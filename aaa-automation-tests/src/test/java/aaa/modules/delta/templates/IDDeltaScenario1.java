package aaa.modules.delta.templates;

import java.util.Arrays;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
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

public class IDDeltaScenario1 extends BaseTest {
	
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
        log.info("DELTA ID SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC_verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		GeneralTab generalTab = new GeneralTab();
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), ComboBox.class).verify.options(
				Arrays.asList("AAA-Michigan (ACG)", "AAA-NoCal (CSAA IG) Rewrite", "AAA-NoCal (CSAA IG) Sold/Bought", "AAA-SoCal (ACSC)", 
						"ACIC", "Allstate", "American Family", "Auto-Owners", "Chubb", "Country Insurance", "Farm Bureau", "Farmers", "Fireman's Fund", 
						"First Time Homebuyer", "Foremost", "Hartford", "Liberty Insurance", "Liberty Mutual", "Metropolitian", 
						"Mutual of Enumclaw Ins Co.", "Nationwide", "Other", "Safeco", "State Farm", "Travelers", "United Heritage", 
						"United Services", "USAA", "Vigilant Insurance", "None")); 
		
		GeneralTab.buttonSaveAndExit.click();
	}	
	
	public void TC_verifyErrorForZipCode83213() {
		TestData td_zip83213 = getTestSpecificTD("TestData_ZipCode83213");
		TestData td_zip83212 = getTestSpecificTD("TestData_ZipCode83212");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		ApplicantTab applicantTab = new ApplicantTab();
		applicantTab.fillTab(td_zip83213); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab();
		reportsTab.fillTab(td_zip83213); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(td_zip83213);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab(); 
		premiumsTab.calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab(); 
		CustomAssert.enableSoftMode();	
		errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS14061993);
		errorTab.cancel(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.fillTab(td_zip83212); 
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		reportsTab.fillTab(td_zip83212); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();		
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
        
        log.info("DELTA ID SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC_verifyODDPolicy() {
		//TODO verify AHAUXX - Consumer Information Notice is on On-Demand Documents tab, verify AHAUXX generation
	}
	
}
