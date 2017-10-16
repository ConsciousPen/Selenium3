package aaa.modules.delta.templates.auto;


import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.util.ArrayList;

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
        policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, true);
		DocumentsAndBindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);
        
        effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue(); 		
	}
	
	public void TC_verifyPrefillTab() {
		mainApp().open(); 
		SearchPage.openQuote("QCTSS926446444");
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();

		GeneralTab.buttonSaveAndExit.click();
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
