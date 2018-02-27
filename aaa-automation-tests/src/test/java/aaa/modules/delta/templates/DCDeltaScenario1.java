package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

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
		
		CustomAssert.enableSoftMode();
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrier(immediatePriorCarrierLOVs);
		
		GeneralTab.buttonSaveAndExit.click();
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
        
        log.info("DELTA DC SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);	 
	}
	
	public void TC_verifyDeclarationDocumentsGenerated() {
		 //TODO add verification that Declaration documents are generated at NB policy issue and in DB. 
	}
	
	public void TC_verifyPolicyODD() {
		//TODO add verification of On-Demand Documents Tab
	}
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<String>();
	static {
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA Other");
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)"); 
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("AIG");
		immediatePriorCarrierLOVs.add("Alfa Insurance"); 
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("Assurant"); 
		immediatePriorCarrierLOVs.add("Chubb"); 
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military"); 
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military AAA"); 
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Newly Purchased"); 
		immediatePriorCarrierLOVs.add("Donegal"); 
		immediatePriorCarrierLOVs.add("Erie");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("GMAC"); 
		immediatePriorCarrierLOVs.add("Hanover"); 
		immediatePriorCarrierLOVs.add("Harleysville");
		immediatePriorCarrierLOVs.add("Hartford"); 
		immediatePriorCarrierLOVs.add("High Point"); 
		immediatePriorCarrierLOVs.add("Homesite"); 
		immediatePriorCarrierLOVs.add("Kemper"); 
		immediatePriorCarrierLOVs.add("Liberty Mutual"); 
		immediatePriorCarrierLOVs.add("Loudoun Mutual");
		immediatePriorCarrierLOVs.add("Mercury"); 
		immediatePriorCarrierLOVs.add("MetLife"); 
		immediatePriorCarrierLOVs.add("Nationwide"); 
		immediatePriorCarrierLOVs.add("NJ Cure");
		immediatePriorCarrierLOVs.add("NJ Manufacturers"); 
		immediatePriorCarrierLOVs.add("NJ Skylands"); 
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Northern Neck"); 
		immediatePriorCarrierLOVs.add("Ohio Casualty"); 
		immediatePriorCarrierLOVs.add("Other Carrier"); 
		immediatePriorCarrierLOVs.add("Palisades"); 
		immediatePriorCarrierLOVs.add("Proformance");
		immediatePriorCarrierLOVs.add("Rockingham Mutual"); 
		immediatePriorCarrierLOVs.add("Safeco"); 
		immediatePriorCarrierLOVs.add("Selective"); 
		immediatePriorCarrierLOVs.add("State Farm"); 
		immediatePriorCarrierLOVs.add("Travelers"); 
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("VA Farm Bureau"); 
		immediatePriorCarrierLOVs.add("Western United");
		immediatePriorCarrierLOVs.add("White Mountains");	
	}
}
