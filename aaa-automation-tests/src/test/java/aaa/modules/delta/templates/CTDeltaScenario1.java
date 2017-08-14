package aaa.modules.delta.templates;

import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

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
	
	public void TC02_verifyEndorsements() {}
	
	public void TC03_verifyWindstormMitigationDiscount() {}
	
	public void TC04_verifyELC() {}
	
	public void TC05_purchasePolicy() {}
	
	public void TC06_verifyODDPolicy() {} 
	
	public void TC07_verifyCancelNoticeAction() {}

}
