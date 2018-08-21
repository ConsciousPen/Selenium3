package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class OHDeltaScenario1 extends BaseTest {
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
        log.info("DELTA OH SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void TC_verifyImmediatePriorCarrier() {
		mainApp().open(); 

		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			GeneralTab generalTab = new GeneralTab();
			generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).setValue("First Time Homebuyer");
			softly.assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER)).isPresent(false);

			generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).setValue("No Prior");
			softly.assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER)).isPresent(false);

			generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER).setValue("Allstate");
			softly.assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER)).isPresent();
			generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER).setValue("5");

			GeneralTab.buttonSaveAndExit.click();
		});
	}
	
	public void TC_verifyEndorsementsTab() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		
		Map<String, String> endorsement_HS0312 = new HashMap<>();
		endorsement_HS0312.put("Form ID", "HS 03 12");
		endorsement_HS0312.put("Name", "Windstorm Or Hail Deductible - Percentage"); 
				
		Map<String, String> endorsement_HS0493 = new HashMap<>(); 
		endorsement_HS0493.put("Form ID", "HS 04 93"); 
		endorsement_HS0493.put("Name", "Actual Cash Value - Windstorm Or Hail Losses"); 

		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
				softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312)).exists();
				softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493)).exists();

				endorsementTab.fillTab(td_add_Forms);

				softly.assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312)).exists();
				softly.assertThat(endorsementTab.linkEdit("HS 03 12")).isPresent();
				softly.assertThat(endorsementTab.linkRemove("HS 03 12")).isPresent();
			} else if (getPolicyType().equals(PolicyType.HOME_SS_HO4) || getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
				softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312)).isPresent(false);
				softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493)).isPresent(false);
				softly.assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312)).isPresent(false);
			}

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			new PremiumsAndCoveragesQuoteTab().calculatePremium();
			PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		});
	}
	
	public void TC_verifyHailResistanceRating() {
		TestData td_hailResistanceRating = getTestSpecificTD("TestData_hailResistanceRating");
		
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			if (getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
				HssQuoteDataGatherHelper.verifyHailResistanceRatingNotApplied(softly);
				HssQuoteDataGatherHelper.verifyHailResistanceRatingApplied(td_hailResistanceRating, softly);
			} else if (getPolicyType().equals(PolicyType.HOME_SS_HO4) || getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
				HssQuoteDataGatherHelper.verifyHailResistanceRatingNotDisplaying(softly);
			}
			PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		});
	}
	
	public void TC_verifyIneligibleRoofType() {
		TestData td_eligibleData = getTestSpecificTD("TestData");
		TestData td_IneligibleRoofType = getTestSpecificTD("TestData_IneligibleRoofType");
		
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_IneligibleRoofType, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560, softly);

			HssQuoteDataGatherHelper.fillPropertyInfoTabWithCorrectData(td_eligibleData);

			PropertyInfoTab.buttonSaveAndExit.click();
		});
	}
	
	public void TC_purchasePolicy(String scenarioPolicyType) {
		TestData td = getTestSpecificTD("TestData");
		
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA OH SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}
	
	public void TC_verifyODDPolicy() {
		//TODO add verification On-Demand Documents Tab
	}
}
