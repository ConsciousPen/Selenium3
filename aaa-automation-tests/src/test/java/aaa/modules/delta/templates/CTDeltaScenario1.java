package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.CancelNoticeActionTab;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class CTDeltaScenario1 extends BaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected String quoteNumber;
	protected String policyNumber;
	protected String effectiveDate;

	public void createQuote(TestData policyCreationTD, String scenarioPolicyType) {
		policy = getPolicyType().get();		
		mainApp().open();		
        createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(policyCreationTD, BindTab.class, true);
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber);
        
        effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue(); 		
	}

	public void verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrierThenSaveAndExit(immediatePriorCarrierLOVs);
	}
	
	//public void TC_verifyEndorsements() {}	

	public void verifyWindstormMitigationDiscount() {		
		TestData td_WindstormMitigationYes = getTestSpecificTD("TestData_WindstormMitigationYes"); 
		
		Map<String, String> windstormMitigationDiscount_row = new HashMap<>();
		windstormMitigationDiscount_row.put("Discount Category", "Safe Home");
		windstormMitigationDiscount_row.put("Discounts Applied", "Windstorm Mitigation"); 
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab(); 
		premiumsTab.calculatePremium();

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(windstormMitigationDiscount_row)).isPresent(false);

			PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Windstorm Mitigation Discount")).as("Windstorm Mitigation Discount: wrong value in Rating Details")
					.isEqualTo("");
			PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
			PropertyInfoTab propertyInfoTab = new PropertyInfoTab();

			String distanceToCoast =
					propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RISKMETER).getAsset(HomeSSMetaData.PropertyInfoTab.Riskmeter.DISTANCE_TO_COAST_MILES).getValue();
			log.info("Distance to coast value is " + distanceToCoast);
			String elevation = propertyInfoTab.getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.RISKMETER).getAsset(HomeSSMetaData.PropertyInfoTab.Riskmeter.ELEVATION_FEET).getValue();
			log.info("Elevation value is " + elevation);

			propertyInfoTab.fillTab(td_WindstormMitigationYes);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			premiumsTab.calculatePremium();

			softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRowContains(windstormMitigationDiscount_row)).exists();

			PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Windstorm Mitigation Discount")).as("Windstorm Mitigation Discount: wrong value in Rating Details")
					.isNotEqualTo("0.00");
			if (getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Distance to shore")).as("Distance to shore: wrong value in Rating Details").isEqualTo(distanceToCoast);
				softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.values.getValueByKey("Elevation")).as("Elevation: wrong value in Rating Details").isEqualTo(elevation);
			}
			PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

			PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		});
	}

	public void verifyELC() {		
		TestData td_None_with_Score599 = getTestSpecificTD("TestData_None_with_Score599"); 
		TestData td_Declined_with_Score999 = getTestSpecificTD("TestData_Declined_with_Score999"); 
		TestData td_IdentityTheft_with_Score750 = getTestSpecificTD("TestData_IdentityTheft_with_Score750"); 
		TestData td_MilitaryDeployment_with_Score599 = getTestSpecificTD("TestData_MilitaryDeployment_with_Score599");
		TestData td_OtherEvents_with_Score999 = getTestSpecificTD("TestData_OtherEvents_with_Score999");
		String messageOnReportsTab = "Extraordinary life circumstance was applied to the policy effective " + effectiveDate;
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			GeneralTab generalTab = new GeneralTab();
			softly.assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.EXTRAORDINARY_LIFE_CIRCUMSTANCE)).hasValue("None");
			generalTab.submitTab();

			HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_Declined_with_Score999, "999", softly);
			HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_IdentityTheft_with_Score750, "750", softly);

			HssQuoteDataGatherHelper.verifyBestFRScoreApplied(td_MilitaryDeployment_with_Score599, "607", messageOnReportsTab, softly);
			HssQuoteDataGatherHelper.verifyBestFRScoreApplied(td_OtherEvents_with_Score999, "607", messageOnReportsTab, softly);

			HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_None_with_Score599, "599", softly);

			ReportsTab.buttonSaveAndExit.click();
		});
	}

	public void purchasePolicy(TestData policyCreationTD, String scenarioPolicyType) {
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);
		
		policy.dataGather().start(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(policyCreationTD, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
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
				odd_tab.saveAndExit();
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
		mainApp().close();
	}

	public void verifyCancelNoticeTab() {
		TestData td_plus34days = getTestSpecificTD("TestData_Plus34Days");
		
		String error_9931 = "Cancellation effective date must be at least 34 days from today when the policy is within the new business discovery period.";
		String error_9208 = "Cancellation effective date must be before the end of the policy term.";
		
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);
		
		policy.cancelNotice().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			HssQuoteDataGatherHelper.verifyDaysOfNotice("34", 34, error_9931, error_9208, softly);

			CancelNoticeActionTab cancelNoticeTab = new CancelNoticeActionTab();
			cancelNoticeTab.fillTab(td_plus34days);
			CancelNoticeActionTab.buttonOk.click();

			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			softly.assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
		});
	}
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<>();
	static {
		immediatePriorCarrierLOVs.add("21st Century");
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("Allied");
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("American Modern");
		immediatePriorCarrierLOVs.add("American National");
		immediatePriorCarrierLOVs.add("Amica");
		immediatePriorCarrierLOVs.add("Andover");
		immediatePriorCarrierLOVs.add("Chartis");
		immediatePriorCarrierLOVs.add("CSE Safeguard");
		immediatePriorCarrierLOVs.add("Farm Bureau");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Fire Insurance");
		immediatePriorCarrierLOVs.add("First Time Homebuyer");
		immediatePriorCarrierLOVs.add("Foremost");
		immediatePriorCarrierLOVs.add("Hanover");
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Metropolitan");
		immediatePriorCarrierLOVs.add("Middlesex Mutual");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("New London County");
		immediatePriorCarrierLOVs.add("No Prior Insurance");
		immediatePriorCarrierLOVs.add("Other Carriers");
		immediatePriorCarrierLOVs.add("Owners Insurance");
		immediatePriorCarrierLOVs.add("Refusal");
		immediatePriorCarrierLOVs.add("SafeCo");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Tower Insurance");
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("Unigard");
		immediatePriorCarrierLOVs.add("Union Mutual");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("Vermont Mutual");		
	}
}
