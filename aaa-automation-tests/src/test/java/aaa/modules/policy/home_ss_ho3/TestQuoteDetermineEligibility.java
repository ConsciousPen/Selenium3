package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestQuoteDetermineEligibility extends HomeSSHO3BaseTest {
	
	private TestData td_3DetachedStructures = getTestSpecificTD("TestData_3_Detached_Structures"); 
	private TestData td_4DetachedStructures = getTestSpecificTD("TestData_4_Detached_Structures"); 
	private String ER0679 = "Dwellings with more than 2 detached building structures rented to others on";
	private String WM0561 = "Dwellings with more than 3 detached building structures on the residence";
	private String ER0680 = "Coverage B cannot exceed Coverage A";
	private String WM0566 = "Coverage B must be less than 50% of Coverage A to bind";
	
	@Test
    @TestInfo(component = "Quote.HomeSS")
	public void testDetermineEligibility_SC1() {
		mainApp().open();

		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();
		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_3DetachedStructures);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        BindTab bindTab = new BindTab();
        bindTab.btnPurchase.click(); 
        
        ErrorTab errorTab = new ErrorTab();
        
        CustomAssert.enableSoftMode(); 
    	errorTab.tblErrorsList.getRowContains("Message", ER0679).verify.present();
    	errorTab.tblErrorsList.getRowContains("Message", WM0566).verify.present();    	
    	errorTab.cancel();
    	
    	NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
    	propertyInfoTab.fillTab(td_4DetachedStructures);
    	
    	NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsTab.calculatePremium();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();
    	
        errorTab.tblErrorsList.getRowContains("Message", WM0561).verify.present();
        errorTab.tblErrorsList.getRowContains("Message", ER0680).verify.present();
        errorTab.cancel();
        
        BindTab.buttonSaveAndExit.click();
        log.info("TEST Determine Eligibility SC1: HSS Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        CustomAssert.assertAll();
	}

}
