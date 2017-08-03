package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeSSMetaData;
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
	/* TestData_SC1_1: 
		Home Renovation: set Roof renovation = '3+ layers' 
		Stoves: set incorrect values, 
		Pets or animals: set Animal type = 'Dog - Wolf'	
	*/
	
	/* TestData_SC1_2: 
		Home Renovation: remove incorrect value,  
		Stoves: set to No, 
		Pets or animals: set correct Animal type = 'Dog - Other breed', 
		Add 3 Detached Structures with Rented to others = Yes
	*/
	
	/* TestData_SC1_3: 
		Add 4th Detached Structures with Rented to others = No
	*/	
	private String ER0906 = "Dwellings with more than 2 roof layers are ineligible.";
	private String ER0908 = "Wood burning stoves as the sole source of heat are ineligible.";
	private String ER0909 = "Wood burning stoves are ineligible unless professionally installed by a licensed contractor.";
	private String ER0522 = "Dwellings with a wood burning stove without at least one smoke detector installed per floor are ineligible.";
	private String ER0679 = "Dwellings with more than 2 detached building structures rented to others on";
	private String WM0561 = "Dwellings with more than 3 detached building structures on the residence";
	private String ER0680 = "Coverage B cannot exceed Coverage A";
	private String WM0566 = "Coverage B must be less than 50% of Coverage A to bind";
	private String ER0903 = "Applicants/Insureds with vicious dogs or exotic animals are ineligible.";
	
	private String WM0548 = "Dwellings built prior to 1900 are ineligible."; 
	private String WM0549 = "Dwellings built prior to 1940 must have all four major systems fully renovated.";
	private String WM0550 = "Risks with more than 3 horses or 4 livestock are unacceptable.";
	private String ER0913 = "Underwriting approval required. Primary home of the applicant is not insured";
	
	@Test
    @TestInfo(component = "Quote.HomeSS")
	public void testDetermineEligibility_SC1() {
		mainApp().open();

		TestData td_sc1_1 = getTestSpecificTD("TestData_SC1_1"); 
		TestData td_sc1_2 = getTestSpecificTD("TestData_SC1_2"); 
		TestData td_sc1_3 = getTestSpecificTD("TestData_SC1_3");
		
		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();
		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_sc1_1);
		propertyInfoTab.submitTab();
		
		CustomAssert.enableSoftMode(); 
		
		propertyInfoTab.verifyFieldHasMessage(HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION.getLabel(), ER0906);		
		propertyInfoTab.verifyFieldHasMessage(HomeSSMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT.getLabel(), ER0908);
		propertyInfoTab.verifyFieldHasMessage(HomeSSMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR.getLabel(), ER0909);
		propertyInfoTab.verifyFieldHasMessage(HomeSSMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY.getLabel(), ER0522); 
		propertyInfoTab.verifyFieldHasMessage(HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE.getLabel(), ER0903);
		
		propertyInfoTab.fillTab(td_sc1_2);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        BindTab bindTab = new BindTab();
        bindTab.btnPurchase.click(); 
        
        ErrorTab errorTab = new ErrorTab();
         
    	errorTab.tblErrorsList.getRowContains("Message", ER0679).verify.present();
    	errorTab.tblErrorsList.getRowContains("Message", WM0566).verify.present();    	
    	errorTab.cancel();
    	
    	NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
    	propertyInfoTab.fillTab(td_sc1_3);
    	
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

	@Test
    @TestInfo(component = "Quote.HomeSS")
	public void testDetermineEligibility_SC2() {
		mainApp().open();

		TestData td_sc2_1 = getTestSpecificTD("TestData_SC2_1"); 
		TestData td_sc2_2 = getTestSpecificTD("TestData_SC2_2");
		
		//getCopiedQuote();
		createCustomerIndividual();
		createQuote();
		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_sc2_1);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        BindTab bindTab = new BindTab();
        bindTab.btnPurchase.click(); 
        
        ErrorTab errorTab = new ErrorTab();
         
        CustomAssert.enableSoftMode(); 
    	errorTab.tblErrorsList.getRowContains("Message", WM0548).verify.present();
    	errorTab.tblErrorsList.getRowContains("Message", WM0550).verify.present();  
    	errorTab.tblErrorsList.getRowContains("Message", ER0913).verify.present();  
    	errorTab.cancel();
    	
    	NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
    	propertyInfoTab.fillTab(td_sc2_2); 
    	
    	NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click(); 
        
        errorTab.tblErrorsList.getRowContains("Message", WM0549).verify.present();
    	errorTab.tblErrorsList.getRowContains("Message", WM0550).verify.present();  
    	errorTab.tblErrorsList.getRowContains("Message", ER0913).verify.present();  
    	errorTab.cancel();
        
    	BindTab.buttonSaveAndExit.click();
        log.info("TEST Determine Eligibility SC2: HSS Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
         
        CustomAssert.assertAll();
	}
	
}
