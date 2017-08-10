package aaa.modules.delta.co.home;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class HssDeltaScenario3 extends HomeSSHO3BaseTest {
	
	private String quoteNumber;
	private String policyNumber;
	TestData td_sc3; 
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC3_TC01() {
		mainApp().open();
		
		td_sc3 = getTestSpecificTD("TestData");
		
        createCustomerIndividual();     
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td_sc3, BindTab.class, true);
        
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CO SC3: HO3-Prestige Quote created with #" + quoteNumber);

        CustomAssert.assertAll();              
	}
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC3_TC02() {
		mainApp().open();
		
		SearchPage.openQuote(quoteNumber);	

		TestData td_sc3_add_Forms = getTestSpecificTD("TestData_add_Forms"); 
		
		Map<String, String> endorsement_HS0312 = new HashMap<>();
		endorsement_HS0312.put("Form ID", "HS 03 12");
		endorsement_HS0312.put("Name", "Windstorm Or Hail Deductible - Percentage"); 
				
		Map<String, String> endorsement_HS0493 = new HashMap<>(); 
		endorsement_HS0493.put("Form ID", "HS 04 93"); 
		endorsement_HS0493.put("Name", "Actual Cash Value - Windstorm Or Hail Losses"); 
				
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab(); 

		CustomAssert.enableSoftMode();
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present();	
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present();
		
		endorsementTab.fillTab(td_sc3_add_Forms);
		
		endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present();			
		CustomAssert.assertTrue(endorsementTab.verifyLinkEditIsPresent("HS 03 12")); 
		CustomAssert.assertTrue(endorsementTab.verifyLinkRemoveIsPresent("HS 03 12"));
		
		EndorsementTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}

	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC3_TC03() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		//Generate On-Demand documents for quote
	}

	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC3_TC04() {
		mainApp().open();
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td_sc3, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA CO SC3: HO3-Prestige Policy created with #" + policyNumber);
	}
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC3_TC05() {
		mainApp().open(); 
		
		SearchPage.openPolicy(policyNumber);
		//Generate On-Demand documents for policy
	}
	
}
