package aaa.modules.delta.templates;


import java.util.ArrayList;

import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class MDDeltaScenario1 extends BaseTest {
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
        log.info("DELTA MD SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);		
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

	
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<String>();
	static {
		immediatePriorCarrierLOVs.add("AAA Other");
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("ACE");
		immediatePriorCarrierLOVs.add("Agency Insurance of MD");
		immediatePriorCarrierLOVs.add("AIG");
		immediatePriorCarrierLOVs.add("Allianz");
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("American Independent");
		immediatePriorCarrierLOVs.add("Ameriprise");
		immediatePriorCarrierLOVs.add("Amica Mutual");
		immediatePriorCarrierLOVs.add("Brethren Mutual");
		immediatePriorCarrierLOVs.add("Chubb");
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military");
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		immediatePriorCarrierLOVs.add("Donegal");
		immediatePriorCarrierLOVs.add("Erie");
		immediatePriorCarrierLOVs.add("Esurance");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Frederick Mutual");
		immediatePriorCarrierLOVs.add("GEICO");
		immediatePriorCarrierLOVs.add("Grange");
		immediatePriorCarrierLOVs.add("Hanover");
		immediatePriorCarrierLOVs.add("Harleysville"); 
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("IFA");
		immediatePriorCarrierLOVs.add("Infinity");
		immediatePriorCarrierLOVs.add("Kemper");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Maryland Automobile Fund"); 
		immediatePriorCarrierLOVs.add("Mercury");
		immediatePriorCarrierLOVs.add("MetLife");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Other Carrier");
		immediatePriorCarrierLOVs.add("Penn National");
		immediatePriorCarrierLOVs.add("Progressive");
		immediatePriorCarrierLOVs.add("Safeco");
		immediatePriorCarrierLOVs.add("Selective");
		immediatePriorCarrierLOVs.add("State Auto");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Tower"); 
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("Unitrin");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("Western United");
		immediatePriorCarrierLOVs.add("White Mountains");
	}
}
