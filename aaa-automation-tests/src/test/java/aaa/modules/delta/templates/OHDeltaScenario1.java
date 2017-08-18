package aaa.modules.delta.templates;

import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

public class OHDeltaScenario1 extends BaseTest {
	protected IPolicy policy;
	protected String quoteNumber;
	protected String policyNumber;
	
	public void TC01_createQuote(TestData td, String scenarioPolicyType) {
		policy = getPolicyType().get();
		
		mainApp().open();
		
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class, true); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA OH SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC02_verifyImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
	}
	
	public void TC03_verifyEndorsements() {
		
	}
	
	public void TC04_verifyRoofTypeUneligible() {
		
	}
	
	public void TC05_purchasePolicy() {
		
	}
	
	public void TC06_verifyODDPolicy() {}
}
