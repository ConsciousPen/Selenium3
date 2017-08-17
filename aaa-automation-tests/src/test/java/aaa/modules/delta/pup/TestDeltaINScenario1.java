package aaa.modules.delta.pup;

import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;

public class TestDeltaINScenario1 extends PersonalUmbrellaBaseTest { 
	private String quoteNumber;
	private String policyNumber;
	
	public void TC01_createQuote() {
		mainApp().open();
		
		TestData td = getTestSpecificTD("TestData");		

        createCustomerIndividual();
       
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA IN SC1: PUP Quote created with #" + quoteNumber);		
	}

}
