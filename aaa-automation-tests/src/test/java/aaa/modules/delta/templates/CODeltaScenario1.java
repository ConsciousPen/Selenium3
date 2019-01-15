package aaa.modules.delta.templates;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyEndorsementsTab;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static toolkit.verification.CustomAssertions.assertThat;

public class CODeltaScenario1 extends BaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected String quoteNumber;
	protected String policyNumber;
	protected String effectiveDate;

	public void createQuote(TestData td, String scenarioPolicyType) {
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

	public void verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrierThenSaveAndExit(immediatePriorCarrierLOVs);
	}

	public void verifyEndorsementsTab() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");

		Map<String, String> HS_03_12 = new HashMap<>();
		HS_03_12.put("Form ID", "HS 03 12");
		HS_03_12.put("Name", "Windstorm Or Hail Deductible - Percentage");

		Map<String, String> HS_04_93 = new HashMap<>();
		HS_04_93.put("Form ID", "HS 04 93");
		HS_04_93.put("Name", "Actual Cash Value - Windstorm Or Hail Losses");

		Map<String, String> HS_04_36 = new HashMap<>();
		HS_04_36.put("Form ID", "HS 04 36");
		HS_04_36.put("Name", "Loss Assessment Coverage For Earthquake");

		Map<String, String> HS_04_52 = new HashMap<>();
		HS_04_52.put("Form ID", "HS 04 52");
		HS_04_52.put("Name", "Livestock Collision Coverage");

		Map<String, String> DS_04_68 = new HashMap<>();
		DS_04_68.put("Form ID", "DS 04 68");
		DS_04_68.put("Name", "Loss Assessment Coverage For Earthquake");
		
		mainApp().open();
		SearchPage.openQuote(quoteNumber);			
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(HS_03_12)).isPresent();
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(HS_04_93)).isPresent();
				endorsementTab.fillTab(td_add_Forms);
				assertThat(PropertyEndorsementsTab.tblIncludedEndorsements.getRow(HS_03_12)).isPresent();
				assertThat(endorsementTab.getLinkEdit("HS 03 12")).isPresent();
				assertThat(endorsementTab.getLinkRemove("HS 03 12")).isPresent();
				break;
			case "HomeSS_HO4":
			case "HomeSS_HO6":
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(HS_03_12)).isPresent(false);
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(HS_04_93)).isPresent(false);
				assertThat(PropertyEndorsementsTab.tblIncludedEndorsements.getRow(HS_03_12)).isPresent(false);
				endorsementTab.fillTab(td_add_Forms);
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(HS_04_36)).isPresent();
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(HS_04_52)).isPresent();
				break;
			case "HomeSS_DP3":
				endorsementTab.fillTab(td_add_Forms);
				assertThat(PropertyEndorsementsTab.tblOptionalEndorsements.getRowContains(DS_04_68)).isPresent();
				break;
			default:
				break;
		}
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 		
		PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();	
	}

	public void verifyAdverselyImpacted() {
		TestData td_Declined_with_Score700 = getTestSpecificTD("TestData_Declined_with_Score700");
		TestData td_Dissolution_with_Score700 = getTestSpecificTD("TestData_Dissolution_with Score700");
		TestData td_IdentityTheft_with_Score800 = getTestSpecificTD("TestData_IdentityTheft_with_Score800");
		TestData td_IdentityTheft_with_Score999 = getTestSpecificTD("TestData_IdentityTheft_with_Score999");
		TestData td_AdverselyImpacted_None = getTestSpecificTD("TestData_AdverselyImpacted_None");

		String messageAdverselyImpacted = "Adversely Impacted was applied to the policy effective " + effectiveDate;
		
		mainApp().open();		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			GeneralTab generalTab = new GeneralTab();
			softly.assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.ADVERSELY_IMPACTED)).hasValue("None");
			softly.assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.ADVERSELY_IMPACTED))
					.containsAllOptions("None", "Dissolution of marriage or Credit information of a former spouse", "Identity Theft", "Declined");

			generalTab.submitTab();

			HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_Declined_with_Score700, "700", softly);

			HssQuoteDataGatherHelper.verifyBestFRScoreApplied(td_Dissolution_with_Score700, "751", messageAdverselyImpacted, softly);

			HssQuoteDataGatherHelper.verifyBestFRScoreNotApplied(td_IdentityTheft_with_Score800, "800", softly);

			HssQuoteDataGatherHelper.verifyBestFRScoreApplied(td_IdentityTheft_with_Score999, "751", messageAdverselyImpacted, softly);


			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().btnPurchase.click();

			ErrorTab errorTab = new ErrorTab();
			errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS10060735);
			errorTab.cancel();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());
			generalTab.fillTab(td_AdverselyImpacted_None);

			GeneralTab.buttonSaveAndExit.click();
		});
	}

	public void verifyIneligibleRoofType() {
		TestData td_eligibleData = getTestSpecificTD("TestData");
		TestData td_construction1 = getTestSpecificTD("TestData_Construction1");
		TestData td_construction2 = getTestSpecificTD("TestData_Construction2");
		TestData td_construction3 = getTestSpecificTD("TestData_Construction3");
		
		mainApp().open(); 
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			if (getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
				log.info("DELTA CO SC1: Roof Type 'Asphalt/Fiberglass' verification");
				HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_construction1, ErrorEnum.Errors.ERROR_AAA_HO_SS624530_CO, softly);

				log.info("DELTA CO SC1: Roof Type 'Wood shingle/shake' verification");
				HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_construction2, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560, softly);

				log.info("DELTA CO SC1: Roof Type 'Builtup Tar & Gravel' verification");
				HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_construction3, ErrorEnum.Errors.ERROR_AAA_HO_SS624530_CO, softly);
			} else if (getPolicyType().equals(PolicyType.HOME_SS_HO4) || getPolicyType().equals(PolicyType.HOME_SS_HO6)) {
				log.info("DELTA CO SC1: Roof Type 'Wood shingle/shake' verification");
				HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_construction2, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560, softly);
			}

			HssQuoteDataGatherHelper.fillPropertyInfoTabWithCorrectData(td_eligibleData);

			PropertyInfoTab.buttonSaveAndExit.click();
		}); 
	}

	public void purchasePolicy(TestData td, String scenarioPolicyType) {
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
        
        log.info("DELTA CO SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);		
	}

	public void verifyPolicyODD() {
		mainApp().open(); 		
		SearchPage.openPolicy(policyNumber);

		policy.policyDocGen().start();
		GenerateOnDemandDocumentActionTab odd_tab = new GenerateOnDemandDocumentActionTab();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11.setState(getState()));
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11.setState(getState()));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11.setState(getState()),
						DocGenEnum.Documents.HS0312,
						DocGenEnum.Documents.HS0454,
						DocGenEnum.Documents.HS2473,
						DocGenEnum.Documents.HSSCCOA);
				break;
			case "HomeSS_HO4":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11_4.setState(String.format("%s4", getState())));
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11_4.setState(String.format("%s4", getState())));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11_4.setState(String.format("%s4", getState())),
						DocGenEnum.Documents.HSSCCOB);
				break;
			case "HomeSS_HO6":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())));
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11_6.setState(String.format("%s6", getState())),
						DocGenEnum.Documents.HSSCCOC);
				break;
			case "HomeSS_DP3":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.DS11.setState(getState()));
				odd_tab.generateDocuments(DocGenEnum.Documents.DS11.setState(getState()));
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.DS11.setState(getState()),
						DocGenEnum.Documents.HSSCCOD);
				break;
			default:
				break;
		}		

	}
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<>();
	static {
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("Allied");
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("Amco Ins Co");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("American National");
		immediatePriorCarrierLOVs.add("Auto Owners");
		immediatePriorCarrierLOVs.add("Bear River Mutual");
		immediatePriorCarrierLOVs.add("Chartis");
		immediatePriorCarrierLOVs.add("Cincinnati");
		immediatePriorCarrierLOVs.add("Country");
		immediatePriorCarrierLOVs.add("CSAA IG");
		immediatePriorCarrierLOVs.add("CSE Safeguard");
		immediatePriorCarrierLOVs.add("Farm Bureau");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Fire Insurance");
		immediatePriorCarrierLOVs.add("First Time Homebuyer");
		immediatePriorCarrierLOVs.add("Foremost");
		immediatePriorCarrierLOVs.add("Great Northern");
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Metropolitan");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Other Carrier");
		immediatePriorCarrierLOVs.add("Owners Insurance");
		immediatePriorCarrierLOVs.add("Pacific Indemnity");
		immediatePriorCarrierLOVs.add("Safeco");
		immediatePriorCarrierLOVs.add("Standard Fire");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("Unigard");
		immediatePriorCarrierLOVs.add("USAA");		
	}
}
