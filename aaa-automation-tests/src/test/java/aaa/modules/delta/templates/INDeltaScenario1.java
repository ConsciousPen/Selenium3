package aaa.modules.delta.templates;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
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

public class INDeltaScenario1 extends BaseTest { 
	
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
        log.info("DELTA IN SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}

	public void verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrierThenSaveAndExit(immediatePriorCarrierLOVs);
	}

	public void verifyEndorsementsTab() {
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
				softly.assertThat(endorsementTab.getLinkEdit("HS 03 12")).isPresent();
				softly.assertThat(endorsementTab.getLinkRemove("HS 03 12")).isPresent();
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

	public void verifyEndorsementHS2383() {	
		TestData td_hs2383 = getTestSpecificTD("TestData_addHS2383"); 
		
		Map<String, String> endorsement_HS2383 = new HashMap<>(); 
		endorsement_HS2383.put("Form ID", "HS 23 83"); 
		endorsement_HS2383.put("Name", "Mine Subsidence Endorsement"); 
		
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS2383)).isPresent(false);
			softly.assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS2383)).isPresent(false);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
			new ApplicantTab().fillTab(td_hs2383);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
			new ReportsTab().fillTab(td_hs2383);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());

			softly.assertThat(endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS2383)).exists();

			endorsementTab.fillTab(td_hs2383);

			softly.assertThat(endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS2383)).exists();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
			new PremiumsAndCoveragesQuoteTab().calculatePremium();

			PremiumsAndCoveragesQuoteTab.buttonSaveAndExit.click();
		});
	}

	public void verifyQuoteODD() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	

		policy.quoteDocGen().start();		
		//TODO add verification On-Demand Documents tab
		GenerateOnDemandDocumentActionTab.buttonSaveAndExit.click();
	}

	public void verifyHailResistanceRating() {
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

	public void verifyIneligibleRoofType() {
		TestData td_eligibleData = getTestSpecificTD("TestData");
		TestData td_ineligibleRoofType = getTestSpecificTD("TestData_IneligibleRoofType");
		
		mainApp().open(); 		
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();

		CustomSoftAssertions.assertSoftly(softly -> {
			HssQuoteDataGatherHelper.verifyErrorForIneligibleRoofType(td_ineligibleRoofType, ErrorEnum.Errors.ERROR_AAA_HO_SS10030560, softly);
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
        
        log.info("DELTA IN SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
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
		immediatePriorCarrierLOVs.add("American Modern");
		immediatePriorCarrierLOVs.add("American National");
		immediatePriorCarrierLOVs.add("Auto Owners");
		immediatePriorCarrierLOVs.add("Chartis");
		immediatePriorCarrierLOVs.add("Cincinnati");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Foremost");
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("IDS");
		immediatePriorCarrierLOVs.add("Kemper");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("Metropolitan");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Other Carrier");
		immediatePriorCarrierLOVs.add("Owners Insurance");
		immediatePriorCarrierLOVs.add("Pacific Indemnity");
		immediatePriorCarrierLOVs.add("SafeCo");
		immediatePriorCarrierLOVs.add("Sentinel Insurance");
		immediatePriorCarrierLOVs.add("Standard Guaranty");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("None");
	}
	
}
