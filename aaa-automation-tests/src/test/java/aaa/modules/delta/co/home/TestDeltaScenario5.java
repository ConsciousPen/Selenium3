package aaa.modules.delta.co.home;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class TestDeltaScenario5 extends HomeSSHO6BaseTest{
	
	private String quoteNumber; 
	private String policyNumber;
	private String effectiveDate; 
	private TestData td; 
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3) 
	public void testSC5_TC01() {
		mainApp().open();

		td = getTestSpecificTD("TestData"); 
		
        createCustomerIndividual();
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CO SC5: HO6 Quote created with #" + quoteNumber);
        
        effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue(); 
	}

	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC5_TC02() {
		mainApp().open();
		
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
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present(false);	
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present(false);		
		endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present(false);			
		EndorsementTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}

	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC5_TC03() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		//Generate On-Demand documents for quote
	}
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC5_TC04() {
		mainApp().open();
	
		TestData td_Declined_with_Score700 = getTestSpecificTD("TestData_Declined_with_Score700");
		TestData td_Dissolution_with_Score700 = getTestSpecificTD("TestData_Dissolution_with Score700");
		TestData td_IdentityTheft_with_Score800 = getTestSpecificTD("TestData_IdentityTheft_with_Score800");
		TestData td_IdentityTheft_with_Score999 = getTestSpecificTD("TestData_IdentityTheft_with_Score999");
		TestData td_AdverselyImpacted_None = getTestSpecificTD("TestData_AdverselyImpacted_None");
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();		
		GeneralTab generalTab = new GeneralTab();
		generalTab.verifyFieldHasValue("Adversely Impacted", "None"); 
		generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.ADVERSELY_IMPACTED.getLabel(), ComboBox.class).verify.optionsContain(
				Arrays.asList("None", "Dissolution of marriage or Credit information of a former spouse", "Identity Theft", "Declined"));
		
		verifyAdverselyImpactedNotApplied(td_Declined_with_Score700, "700");
		
		verifyAdverselyImpactedApplied(td_Dissolution_with_Score700, "751");
		
		verifyAdverselyImpactedNotApplied(td_IdentityTheft_with_Score800, "800");
		
		verifyAdverselyImpactedApplied(td_IdentityTheft_with_Score999, "751"); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab();
		//TODO-dchubkov: replace with errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_XXXXXX);
		errorTab.errorsList.getTable().getRow("Message", "Underwriter approval is required when Adversely Impacted is selected.").verify.present();
		errorTab.cancel();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		generalTab.fillTab(td_AdverselyImpacted_None);
		
		GeneralTab.buttonSaveAndExit.click();		
		CustomAssert.assertAll();		
	}	

	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC5_TC05() {
		mainApp().open(); 
		
		//TestData td_construction1 = getTestSpecificTD("TestData_Construction1");
		TestData td_construction2 = getTestSpecificTD("TestData_Construction2");
		//TestData td_construction3 = getTestSpecificTD("TestData_Construction3");
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();		
		//verifyErrorMessageOnBind(td_construction1, "Dwellings that have not had the roof replaced within the past 25 years if " );
		
		verifyErrorMessageOnBind(td_construction2, "Dwellings with a wood shake/shingle roof are unacceptable.");
		
		//verifyErrorMessageOnBind(td_construction3, "Dwellings that have not had the roof replaced within the past 25 years if ");
		BindTab.buttonSaveAndExit.click();
		
		CustomAssert.assertAll();	
	}
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC5_TC06() {
		mainApp().open(); 
		
		SearchPage.openQuote(quoteNumber);
		
		policy.dataGather().start(); 
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA CO SC5: HO6 Policy created with #" + policyNumber);
		
	}
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testSC5_TC07() {
		mainApp().open(); 
		
		SearchPage.openPolicy(policyNumber);
		//Generate On-Demand documents for policy
	}
	
	private void verifyAdverselyImpactedNotApplied(TestData td, String scoreInRatingDetails) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("FR Score value is wrong in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score").equals(scoreInRatingDetails));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		reportsTab.lblAdversalyImpactedMessage.verify.present(false);
	}
	
	private void verifyAdverselyImpactedApplied(TestData td, String scoreInRatingDetails) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		new GeneralTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		ReportsTab reportsTab = new ReportsTab(); 
		reportsTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		CustomAssert.assertTrue("FR Score value is wrong in Rating Details", 
				PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("FR Score").equals(scoreInRatingDetails));
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get()); 
		CustomAssert.assertTrue("Adversely Impacted is not applied on Reports Tab",
				reportsTab.lblAdversalyImpactedMessage.getValue().equals("Adversely Impacted was applied to the policy effective "+effectiveDate));		
	}

	private void verifyErrorMessageOnBind(TestData td, String message) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab();
		//TODO-dchubkov: replace with errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_XXXXXX);
		errorTab.errorsList.getTable().getRowContains("Message", message).verify.present();
		errorTab.cancel();
	}
	
	
}
