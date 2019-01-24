package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;

import aaa.common.enums.NavigationEnum;
import aaa.common.enums.NavigationEnum.HomeSSTab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;

public class MDDeltaScenario1 extends BaseTest {
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected String quoteNumber;
	protected String policyNumber; 
	
	public void createQuote(TestData td, String scenarioPolicyType) {
		policy = getPolicyType().get();
		
		mainApp().open();		
        createCustomerIndividual();
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class, true); 
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA MD SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);		
	}
	
	public void verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrierThenSaveAndExit(immediatePriorCarrierLOVs);
	}

	public void verifyEndorsementHS0495(String scenarioPolicyType) {
		Map<String, String> endorsement_HS0495 = new HashMap<>(); 
		endorsement_HS0495.put("Form ID", "HS 04 95"); 
		endorsement_HS0495.put("Name", "Water Back Up And Sump Discharge Or"); 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());

		CustomSoftAssertions.assertSoftly(softly -> {
			PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
			String policyLimit;
			if (getPolicyType().equals(PolicyType.HOME_SS_HO4)) {
				policyLimit = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE).getAsset(
						HomeSSMetaData.PropertyInfoTab.PropertyValue.PERSONAL_PROPERTY_VALUE).getValue();
			}
			else {
				policyLimit = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE).getAsset(
						HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).getValue();
			}
			policyLimit = policyLimit.substring(0, policyLimit.length()-3);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());

			if (scenarioPolicyType.equals("HO3-Legasy")) {
				editHS0495AndVerifyCoverageLimitLOVs_Legasy(endorsement_HS0495, policyLimit, softly);
			} else if (scenarioPolicyType.equals("HO3-Prestige")) {
				editHS0495AndVerifyCoverageLimitLOVs_Prestige(endorsement_HS0495, policyLimit, softly);
			} else {
				addHS0495AndVerifyCoverageLimitLOVs(endorsement_HS0495, policyLimit, softly);
			}
			new EndorsementTab().saveAndExit();
		});
	}
	
	public void verifyEndorsementDS0495() {
		Map<String, String> endorsement_DS0495 = new HashMap<>(); 
		endorsement_DS0495.put("Form ID", "DS 04 95"); 
		endorsement_DS0495.put("Name", "Water Back Up And Sump Discharge Or"); 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());

		CustomSoftAssertions.assertSoftly(softly -> {
			PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
			String policyLimit = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE).getAsset(
					HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT).getValue();
			policyLimit = policyLimit.substring(0, policyLimit.length()-3);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());

			addDS0495AndVerifyCoverageLimitLOVs(endorsement_DS0495, policyLimit, softly);
			
			new EndorsementTab().saveAndExit();
		});
	}
	
	public void verifyEndorsementHS2338() {
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

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS2338)).exists();
			endorsementTab.getAddEndorsementLink("HS 23 38").click();

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES).setValue("9");

			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38)
					.getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES))
					.hasWarningWithText(error_9918);

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES).setValue("8");

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.LOCATION_OF_BUSINESS).setValue("In the dwelling building");
			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY)).isPresent(false);
			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE)).isPresent(false);

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.LOCATION_OF_BUSINESS).setValue("Other structure of residence premises");
			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY)).isPresent();
			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE)).isPresent();

			endorsementTab.btnSaveForm.click();
			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY))
					.hasWarningWithText("'Section I limit of liability' is required");

			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE))
					.hasWarningWithText("'Section I description of structure' is required");

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY).setValue("100");
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_DESCRIPTION_OF_STRUCTURE).setValue("Test");
			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED).setValue("$100,000");

			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED))
					.hasWarningWithText("Limit of liability for Coverage E and F combined should be $300,000.");

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED).setValue("$200,000");
			softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED))
					.hasWarningWithText("Limit of liability for Coverage E and F combined should be $300,000.");

			endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_23_38).getAsset(
					HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_II_COVERAGES_E_AND_F_COMBINED).setValue("$300,000");
			endorsementTab.btnSaveForm.click();

			softly.assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(endorsement_HS2338)).exists();
			endorsementTab.saveAndExit();
		});
	}
	
	public void verifyStormShutterDiscount() {
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

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(StormShutterDiscount_row)).isPresent(false);

			PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Storm Shutter")).as("Storm Shutter Discount: wrong value in Rating Details").isEqualTo("0.0");
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

			softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(StormShutterDiscount_row)).exists();

			PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Storm Shutter")).as("Storm Shutter Discount: wrong value in Rating Details").isNotEqualTo("0.0"); //.isEqualTo("3.0%");
			if (getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Distance to shore")).as("Distance to shore: wrong value in Rating Details").isEqualTo(distanceToCoast);
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Elevation")).as("Elevation: wrong value in Rating Details").isEqualTo(elevation);
			}
			PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

			PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		});
	}

	public void verifyInspectionTypeAndEligibility() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		TestData td1 = getTestSpecificTD("TestData_Construction1");
		TestData td2 = getTestSpecificTD("TestData_Construction2");
		TestData td3 = getTestSpecificTD("TestData_Construction3");
		TestData td = getTestSpecificTD("TestData_correct");		
		
		if (getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			verifyErrorOnBind(td1, ErrorEnum.Errors.ERROR_AAA_HO_SS2103528);
			verifyErrorOnBind(td2, ErrorEnum.Errors.ERROR_AAA_HO_SS11120040);
			verifyErrorOnBind(td3, ErrorEnum.Errors.ERROR_AAA_HO_SS1162304_MD);
		}
		else {
			verifyInspectionTypeAndErrorOnBind(td1, "Interior", ErrorEnum.Errors.ERROR_AAA_HO_SS3282256);
			verifyInspectionTypeAndErrorOnBind(td2, "Interior", ErrorEnum.Errors.ERROR_AAA_HO_SS11120040);
			verifyInspectionTypeAndErrorOnBind(td3, "High Value Interior", ErrorEnum.Errors.ERROR_AAA_HO_SS1162304_MD);
		}
		
		HssQuoteDataGatherHelper.fillPropertyInfoTabWithCorrectData(td);
		if (getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
			new PropertyInfoTab().saveAndExit();
		}
		else {
			NavigationPage.toViewTab(HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
			new UnderwritingAndApprovalTab().fillTab(td);		
			new UnderwritingAndApprovalTab().saveAndExit();
		}
	}
	
	public void verifyUnderwritingApprovalTab() {
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		TestData td_uw3 = getTestSpecificTD("TestData_UW3");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
			UnderwritingAndApprovalTab underwritingTab = new UnderwritingAndApprovalTab();
			if (!getPolicyType().equals(PolicyType.HOME_SS_HO4)) {
				underwritingTab.fillTab(td_uw1);
		        underwritingTab.submitTab();
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES))
						.hasWarningWithText("Business or farming activity is ineligible");
			}
			if (getPolicyType().equals(PolicyType.HOME_SS_HO4)) {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES)).isPresent(false);
			}
			underwritingTab.fillTab(td_uw2);
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().btnPurchase.click();

			ErrorTab errorTab = new ErrorTab();
			errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS7160042);
			errorTab.cancel();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
			underwritingTab.fillTab(td_uw3);
			UnderwritingAndApprovalTab.buttonSaveAndExit.click();
		});
	}
	
	public void purchasePolicy(TestData td, String scenarioPolicyType) {
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);
		
		policy.dataGather().start(); 		
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 	
		
		NavigationPage.toViewTab(HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA MD SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);	
        
        //Verify declaration document is generated on NB
        Document decDoc; 
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
        	decDoc = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.DS02, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);
        }
        else {
        	decDoc = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.HS02, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);
        }
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(decDoc).as("Declaration Document is not generated on NB").isNotNull();
        });       
	}
	
	public void verifyODDPolicy() {
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);
		policy.policyDocGen().start();
		GenerateOnDemandDocumentActionTab odd_tab = new GenerateOnDemandDocumentActionTab();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11.setState(getState()));
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11.setState(getState()));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11.setState(getState()));
				break;
			case "HomeSS_HO4":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11_4.setState(String.format("%s4", getState())));
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11_4.setState(String.format("%s4", getState())));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11_4.setState(String.format("%s4", getState())));
				break;
			case "HomeSS_HO6":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())));
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())));
				break;
			case "HomeSS_DP3":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.DS11.setState(getState()));
				odd_tab.generateDocuments(DocGenEnum.Documents.DS11.setState(getState()));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.DS11.setState(getState()));
				break;
			default:
				break;
		}
		odd_tab.saveAndExit();
	}

	public void verifyAnimalType() {
		//TODO
	}
	
	public void verifyHSPIMDA() {
		//TODO
	}
	
	public void verifyCancelNoticeTab() {
		TestData td_plus21days = getTestSpecificTD("TestData_Plus21Days");
		
		String error_9206 = "Cancellation effective date must be at least 21 days from today when the policy is within the new business discovery period."; 
		String error_9208 = "Cancellation effective date must be before the end of the policy term.";
		
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);
		
		policy.cancelNotice().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			HssQuoteDataGatherHelper.verifyDaysOfNotice("21", 21, error_9206, error_9208, softly);

			CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();
			cancelNoticeTab.fillTab(td_plus21days);
			CancelNoticeActionTab.buttonOk.click();

			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			softly.assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		});
	}	
	
	
	private void addHS0495AndVerifyCoverageLimitLOVs(Map<String, String> hs0495, String policyLimit, ETCSCoreSoftAssertions softly) {
		EndorsementTab endorsementTab = new EndorsementTab();
		softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(hs0495)).exists();
		endorsementTab.getAddEndorsementLink("HS 04 95").click();

		softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT)).hasOptions("$5000", "$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit);
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), ComboBox.class).setValue("$20,000"); 
		endorsementTab.btnSaveForm.click();

		softly.assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(hs0495)).exists();
	}
	
	private void addDS0495AndVerifyCoverageLimitLOVs(Map<String, String> ds0495, String policyLimit, ETCSCoreSoftAssertions softly) {
		EndorsementTab endorsementTab = new EndorsementTab();
		softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(ds0495)).exists();
		endorsementTab.getAddEndorsementLink("DS 04 95").click();

		softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementDS0495.COVERAGE_LIMIT)).hasOptions("$5000", "$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit);
		endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.DS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementDS0495.COVERAGE_LIMIT.getLabel(), ComboBox.class).setValue("$20,000"); 
		endorsementTab.btnSaveForm.click();

		softly.assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(ds0495)).exists();
	}

	private void editHS0495AndVerifyCoverageLimitLOVs_Legasy(Map<String, String> hs0495, String policyLimit, ETCSCoreSoftAssertions softly) {
		EndorsementTab endorsementTab = new EndorsementTab();
		softly.assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(hs0495)).exists();
		
		endorsementTab.getEditEndorsementLink("HS 04 95", 1).click();

		softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT)).hasOptions("$5000", "$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit);
		endorsementTab.btnSaveForm.click();
	}
	
	private void editHS0495AndVerifyCoverageLimitLOVs_Prestige(Map<String, String> hs0495, String policyLimit, ETCSCoreSoftAssertions softly) {
		EndorsementTab endorsementTab = new EndorsementTab();
		softly.assertThat(endorsementTab.tblIncludedEndorsements.getRowContains(hs0495)).exists();
		
		endorsementTab.getEditEndorsementLink("HS 04 95", 1).click();

		softly.assertThat(endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_95).getAsset(
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT)).hasOptions("$10,000", "$15,000", "$20,000", "$25,000", "$50,000", policyLimit);
		endorsementTab.btnSaveForm.click();
	}	
	
	private void verifyErrorOnBind(TestData td, ErrorEnum.Errors errorCode) {
		NavigationPage.toViewTab(HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(td);
		
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorsPresent(errorCode);
		errorTab.cancel();
	}
	
	private void verifyInspectionTypeAndErrorOnBind(TestData td, String inspectionType, ErrorEnum.Errors errorCode) {
		NavigationPage.toViewTab(HomeSSTab.PROPERTY_INFO.get());
		new PropertyInfoTab().fillTab(td);
		
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		assertThat(new UnderwritingAndApprovalTab().getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE).getValue()).isEqualTo(inspectionType);
		
		NavigationPage.toViewTab(HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();		
		ErrorTab errorTab = new ErrorTab(); 
		errorTab.verify.errorsPresent(errorCode);
		errorTab.cancel();
	}
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<>();
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
