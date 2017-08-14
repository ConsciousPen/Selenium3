package aaa.modules.delta.templates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class CODeltaScenario1 extends BaseTest {
	
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
        log.info("DELTA CO SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);
        
        effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue(); 
		
	}
	
	public void TC02_verifyEndorsements(TestData td_forms) {
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
		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present();	
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present();
			
			endorsementTab.fillTab(td_forms);
			
			endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present();			
			CustomAssert.assertTrue(endorsementTab.verifyLinkEditIsPresent("HS 03 12")); 
			CustomAssert.assertTrue(endorsementTab.verifyLinkRemoveIsPresent("HS 03 12"));
		}
		else if (getPolicyType().equals(PolicyType.HOME_SS_HO4)||getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present(false);	
			endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present(false);		
			endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present(false);
		}
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}
	
	public void TC03_verifyQuoteODD() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	

		policy.quoteDocGen().start();		
		CustomAssert.enableSoftMode();	
		/*
		GenerateOnDemandDocumentActionTab goddTab = new GenerateOnDemandDocumentActionTab();
		goddTab.tableOnDemandDocuments.getRow("Document #", "HS11CO").verify.present();
		goddTab.tableOnDemandDocuments.getRow("Document #", "HSIQXX").verify.present();
		*/
		GenerateOnDemandDocumentActionTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}
	
	public void TC04_verifyAdverselyImpacted() {
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
		errorTab.verify.errorPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS10060735);
		errorTab.cancel();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get()); 
		generalTab.fillTab(td_AdverselyImpacted_None);
		
		GeneralTab.buttonSaveAndExit.click();		
		CustomAssert.assertAll();
	}
	
	public void TC05_verifyRoofTypeUneligible(TestData td) {
		mainApp().open(); 
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();
		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO3)||getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
			TestData td_construction1 = getTestSpecificTD("TestData_Construction1");
			TestData td_construction2 = getTestSpecificTD("TestData_Construction2");
			TestData td_construction3 = getTestSpecificTD("TestData_Construction3");
			log.info("DELTA CO SC1: Roof Type: Asphalt/Fiberglass verification");
			verifyErrorMessageOnBind(td_construction1, ErrorEnum.Errors.ERROR_AAA_HO_SS624530_CO);
			log.info("DELTA CO SC1: Roof Type: Wood shingle/shake verification");
			verifyErrorMessageOnBind(td_construction2, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560);
			log.info("DELTA CO SC1: Roof Type: Builtup Tar & Gravel verification");
			verifyErrorMessageOnBind(td_construction3, ErrorEnum.Errors.ERROR_AAA_HO_SS624530_CO);
		}
		else if (getPolicyType().equals(PolicyType.HOME_SS_HO4)||getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			TestData td_construction2 = getTestSpecificTD("TestData_Construction2");
			log.info("DELTA CO SC1: Roof Type: Wood shingle/shake verification");
			verifyErrorMessageOnBind(td_construction2, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560);
		}
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td);
		PropertyInfoTab.buttonSaveAndExit.click();
		
		CustomAssert.assertAll();	
	}
	
	public void TC06_purchasePolicy(TestData td, String scenarioPolicyType) {
		mainApp().open(); 
		
		SearchPage.openQuote(quoteNumber);
		
		policy.dataGather().start(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA CO SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
		
	}
	
	public void TC07_verifyPolicyODD() {
		mainApp().open(); 
		
		SearchPage.openPolicy(policyNumber);
		
		//TestData td_godd = getTestSpecificTD("TestData_GODD");
		
		policy.policyDocGen().start();
		//policy.policyDocGen().perform(td_godd);
		
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

	private void verifyErrorMessageOnBind(TestData td, ErrorEnum.Errors errorCode) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(td);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();
		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorPresent(errorCode);
		errorTab.cancel();
	}
	
}
