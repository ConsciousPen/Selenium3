package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
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
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

public class MDDeltaScenario1 extends BaseTest {
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
        log.info("DELTA MD SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);		
	}
	
	public void TC_verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		CustomAssert.enableSoftMode();
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrier(immediatePriorCarrierLOVs);
		
		GeneralTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}	

	public void TC_verifyEndorsementHS0495(String scenarioPolicyType) {
		Map<String, String> endorsement_HS0495 = new HashMap<>(); 
		endorsement_HS0495.put("Form ID", "HS 04 95"); 
		endorsement_HS0495.put("Name", "Water Back Up And Sump Discharge Or"); 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		String policyLimit = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE).getAsset(
				HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).getValue();
		policyLimit = policyLimit.substring(0, policyLimit.length()-3);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());		
		
		CustomAssert.enableSoftMode();

		if (scenarioPolicyType.equals("HO3-Legasy")) {
			editHS0495AndVerifyCoverageLimitLOVs_Legasy(endorsement_HS0495, policyLimit);
		}
		else if (scenarioPolicyType.equals("HO3-Prestige")) {
			editHS0495AndVerifyCoverageLimitLOVs_Prestige(endorsement_HS0495, policyLimit);
		}
		else {
			addHS0495AndVerifyCoverageLimitLOVs(endorsement_HS0495, policyLimit);
		}		
		CustomAssert.assertAll();
	}
	
	public void TC_verifyEndorsementHS2338() {
		Map<String, String> endorsement_HS2338 = new HashMap<>(); 
		endorsement_HS2338.put("Form ID", "HS 23 38"); 
		endorsement_HS2338.put("Name", "Home day Care Coverage Endorsement - Maryland"); 
		
		String error_9918 = "More than 8 persons receiving day care services are ineligible."; 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab(); 
		
		CustomAssert.enableSoftMode();
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS2338).verify.present();	
		endorsementTab.getAddEndorsementLink("HS 23 38").click();
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES).setValue("9");
		
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES))
				.hasWarningWithText(error_9918);
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES).setValue("8");
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.LOCATION_OF_BUSINESS).setValue("In the dwelling building"); 		
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY)).isPresent(false);
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE)).isPresent(false);
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.LOCATION_OF_BUSINESS).setValue("Other structure of residence premises"); 
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY)).isPresent();
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE)).isPresent();
		
		endorsementTab.btnSaveForm.click();
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY))
				.hasWarningWithText("'Section I limit of liability' is required");

		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE))
				.hasWarningWithText("'Section I description of structure' is required");
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY).setValue("100");
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE).setValue("Test");
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED).setValue("$100,000");

		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED))
				.hasWarningWithText("Limit of liability for Coverage E and F combined should be $300,000.");

		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED).setValue("$200,000");
		assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED))
				.hasWarningWithText("Limit of liability for Coverage E and F combined should be $300,000.");
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED).setValue("$300,000"); 
		endorsementTab.btnSaveForm.click();
		
		endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_HS2338).verify.present();			
		CustomAssert.assertAll();
	}
	
	public void TC_verifyStormShutterDiscount() {
		TestData td_StormShutterYes = getTestSpecificTD("TestData_StormShutterYes"); 
		
		Map<String, String> StormShutterDiscount_row = new HashMap<>();
		StormShutterDiscount_row.put("Discount Category", "Safe Home");
		StormShutterDiscount_row.put("Discounts Applied", "Storm Shutter");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab(); 
		premiumsTab.calculatePremium(); 
		
		CustomAssert.enableSoftMode();		
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(StormShutterDiscount_row).verify.present(false);
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Storm Shutter")).as("Storm Shutter Discount: wrong value in Rating Details").isEqualTo("0.0");
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab(); 
		
		String distanceToCoast = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RISKMETER).getAsset(
				HomeSSMetaData.PropertyInfoTab.Riskmeter.DISTANCE_TO_COAST_MILES).getValue();
		String elevation = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RISKMETER).getAsset(
				HomeSSMetaData.PropertyInfoTab.Riskmeter.ELEVATION_FEET).getValue();
		
		propertyInfoTab.fillTab(td_StormShutterYes);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsTab.calculatePremium(); 
				
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(StormShutterDiscount_row).verify.present(); 
		
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open(); 
		assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Storm Shutter")).as("Storm Shutter Discount: wrong value in Rating Details").isEqualTo("3.0%");
		if (getPolicyType().equals("HO3")||getPolicyType().equals("DP3")) {
			assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Distance to shore")).as("Distance to shore: wrong value in Rating Details").isEqualTo(distanceToCoast);
			assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Elevation")).as("Elevation: wrong value in Rating Details").isEqualTo(elevation);
		} 
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}
	
	public void TC_verifyUnderwritingApprovalTab() {
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		TestData td_uw3 = getTestSpecificTD("TestData_UW3");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		UnderwritingAndApprovalTab underwritingTab = new UnderwritingAndApprovalTab();
        underwritingTab.fillTab(td_uw1);
        underwritingTab.submitTab();
        
		CustomAssert.enableSoftMode();
		underwritingTab.verifyFieldHasMessage(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES.getLabel(), 
        		"Business or farming activity is ineligible");

		underwritingTab.fillTab(td_uw2);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        new BindTab().btnPurchase.click();
        
        ErrorTab errorTab = new ErrorTab(); 
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS7160042);
        errorTab.cancel(); 
		
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		underwritingTab.fillTab(td_uw3);		
		UnderwritingAndApprovalTab.buttonSaveAndExit.click();
		CustomAssert.assertAll();
	}
	
	public void TC_verifyInspectionTypeAndEligibility() {
		//TODO
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
        
        log.info("DELTA MD SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);	
	}
	
	public void TC_verifyDeclarationDocumentsGenerated() {
		//TODO
	} 
	
	//HO3, DP3
	public void TC_verifyHSHU1MDGenerated() {
		//TODO
	}
	
	public void TC_verifyODDPolicy() {
		//TODO
	}

	public void TC_verifyAnimalType() {
		//TODO
	}
	
	public void TC_verifyHSPIMDA() {
		//TODO
	}
	
	public void TC_verifyCancelNoticeTab() {
		TestData td_plus21days = getTestSpecificTD("TestData_Plus21Days");
		
		String error_9206 = "Cancellation effective date must be at least 21 days from today when the policy is within the new business discovery period."; 
		String error_9208 = "Cancellation effective date must be before the end of the policy term.";
		
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);
		
		policy.cancelNotice().start(); 
		CustomAssert.enableSoftMode();	
		
		HssQuoteDataGatherHelper.verifyDaysOfNotice("21", 21, error_9206, error_9208);
		
		CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();
		cancelNoticeTab.fillTab(td_plus21days);
		CancelNoticeActionTab.buttonOk.click();
		
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelCancelNotice.verify.present();
		CustomAssert.assertAll();
		
	}	
	
	
	private void addHS0495AndVerifyCoverageLimitLOVs(Map<String, String> hs0495, String policyLimit) {
		EndorsementTab endorsementTab = new EndorsementTab(); 
		endorsementTab.tblOptionalEndorsements.getRowContains(hs0495).verify.present();	
		endorsementTab.getAddEndorsementLink("HS 04 95").click();
	
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), ComboBox.class).verify.options(
						Arrays.asList("$5000", "$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit)); 
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), ComboBox.class).setValue("$20,000"); 
		endorsementTab.btnSaveForm.click();
		
		endorsementTab.tblIncludedEndorsements.getRowContains(hs0495).verify.present();	
	}

	private void editHS0495AndVerifyCoverageLimitLOVs_Legasy(Map<String, String> hs0495, String policyLimit) {
		EndorsementTab endorsementTab = new EndorsementTab(); 
		endorsementTab.tblIncludedEndorsements.getRowContains(hs0495).verify.present();	
		
		endorsementTab.getEditEndorsementLink("HS 04 95", 1).click(); 
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), ComboBox.class).verify.options(
						Arrays.asList("$5000", "$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit)); 
		endorsementTab.btnSaveForm.click();
	}
	
	private void editHS0495AndVerifyCoverageLimitLOVs_Prestige(Map<String, String> hs0495, String policyLimit) {
		EndorsementTab endorsementTab = new EndorsementTab(); 
		endorsementTab.tblIncludedEndorsements.getRowContains(hs0495).verify.present();	
		
		endorsementTab.getEditEndorsementLink("HS 04 95", 1).click(); 
		
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), ComboBox.class).verify.options(
						Arrays.asList("$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit)); 
		endorsementTab.btnSaveForm.click();
	}	
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<String>();
	static {
		immediatePriorCarrierLOVs.add("AAA Other");
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("ACE");
		immediatePriorCarrierLOVs.add("Agency Insurance of MD");
		immediatePriorCarrierLOVs.add("AIG");
		immediatePriorCarrierLOVs.add("Allianz");
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("American Independent");
		immediatePriorCarrierLOVs.add("Ameriprise");
		immediatePriorCarrierLOVs.add("Amica Mutual");
		immediatePriorCarrierLOVs.add("Brethren Mutual");
		immediatePriorCarrierLOVs.add("Chubb");
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military");
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military AAA");
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Newly Purchased");
		immediatePriorCarrierLOVs.add("Donegal");
		immediatePriorCarrierLOVs.add("Erie");
		immediatePriorCarrierLOVs.add("Esurance");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Frederick Mutual");
		immediatePriorCarrierLOVs.add("GEICO");
		immediatePriorCarrierLOVs.add("Grange");
		immediatePriorCarrierLOVs.add("Hanover");
		immediatePriorCarrierLOVs.add("Harleysville"); 
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("IFA");
		immediatePriorCarrierLOVs.add("Infinity");
		immediatePriorCarrierLOVs.add("Kemper");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Maryland Automobile Fund"); 
		immediatePriorCarrierLOVs.add("Mercury");
		immediatePriorCarrierLOVs.add("MetLife");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Other Carrier");
		immediatePriorCarrierLOVs.add("Penn National");
		immediatePriorCarrierLOVs.add("Progressive");
		immediatePriorCarrierLOVs.add("Safeco");
		immediatePriorCarrierLOVs.add("Selective");
		immediatePriorCarrierLOVs.add("State Auto");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Tower"); 
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("Unitrin");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("Western United");
		immediatePriorCarrierLOVs.add("White Mountains");
	}
}
