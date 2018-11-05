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
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

import java.util.ArrayList;

import static toolkit.verification.CustomAssertions.assertThat;

public class ORDeltaScenario1 extends BaseTest { 
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
        log.info("DELTA OR SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}

	public void verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrierThenSaveAndExit(immediatePriorCarrierLOVs);
	}

	public void verifyUnderwritingApprovalTab() {
		TestData td_uw1 = getTestSpecificTD("TestData_UW1");
		TestData td_uw2 = getTestSpecificTD("TestData_UW2");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
		UnderwritingAndApprovalTab underwritingTab = new UnderwritingAndApprovalTab();
        underwritingTab.fillTab(td_uw1);
        underwritingTab.submitTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES))
						.hasWarningWithText("Business or farming activity is ineligible. Dwellings or applicants that perform adult day care, or pet day care are unacceptable.");
			} else {
				softly.assertThat(underwritingTab.getAssetList().getAsset(HomeSSMetaData.UnderwritingAndApprovalTab.IS_ANY_BUSINESS__ADULT_DAY_CARE_OR_FARMING_ACTIVITY_CONDUCTED_ON_THE_PREMISES))
						.hasWarningWithText("Risk must be endorsed with the appropriate business or farming endorsement when a business or incidental farming exposure is present and deemed eligible for coverage. Applicants that perform adult day care, or pet day care, are unacceptable");
			}
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			new BindTab().btnPurchase.click();

			ErrorTab errorTab = new ErrorTab();
			if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
				errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS3151364);
			} else {
				errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS3150198);
			}
			errorTab.cancel();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
			underwritingTab.fillTab(td_uw2);
			UnderwritingAndApprovalTab.buttonSaveAndExit.click();
		});
	}

	public void verifyClaims() {
		TestData td_Claims1 = getTestSpecificTD("TestData_Claims1");
		TestData td_Claims2 = getTestSpecificTD("TestData_Claims2");
		
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
		propertyInfoTab.fillTab(td_Claims1);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		new BindTab().btnPurchase.click();

		CustomSoftAssertions.assertSoftly(softly -> {
			ErrorTab errorTab = new ErrorTab();
			if (getPolicyType().equals(PolicyType.HOME_SS_HO4)) {
				errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS1050670_OR);
			} else {
				errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS1020340_OR);
			}
			errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS12023000);
			errorTab.verify.errorsPresent(softly, ErrorEnum.Errors.ERROR_AAA_HO_SS12200234);
			errorTab.cancel();

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
			propertyInfoTab.fillTab(td_Claims2);
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
        
        log.info("DELTA OR SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);
	}

	public void verifyODDPolicy() {
		//verify AHAUXX - Consumer Information Notice is on On-Demand Documents tab, verify AHAUXX generation
		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		policy.policyDocGen().start();
		GenerateOnDemandDocumentActionTab odd_tab = new GenerateOnDemandDocumentActionTab();

		switch (getPolicyType().getShortName()) {
			case "HomeSS":
				odd_tab.verify.documentsPresent(DocGenEnum.Documents.HS11.setState(getState()), DocGenEnum.Documents.AHAUXX);
				odd_tab.generateDocuments(DocGenEnum.Documents.HS11.setState(getState()), DocGenEnum.Documents.AHAUXX);
				WebDriverHelper.switchToDefault();
				DocGenHelper.verifyDocumentsGenerated(policyNumber, DocGenEnum.Documents.HS11.setState(getState()),
						DocGenEnum.Documents.AHAUXX);
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
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("American Family");
		immediatePriorCarrierLOVs.add("Country Mutual");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("Fireman's Fund");
		immediatePriorCarrierLOVs.add("First Time Homebuyer");
		immediatePriorCarrierLOVs.add("Foremost");
		immediatePriorCarrierLOVs.add("Hartford");
		immediatePriorCarrierLOVs.add("Homesite");
		immediatePriorCarrierLOVs.add("Liberty Insurance Corp.");
		immediatePriorCarrierLOVs.add("Liberty Mutual");
		immediatePriorCarrierLOVs.add("MET");
		immediatePriorCarrierLOVs.add("Mutual of Enumclaw Ins Co.");
		immediatePriorCarrierLOVs.add("Nationwide");
		immediatePriorCarrierLOVs.add("Oregon Mutual");
		immediatePriorCarrierLOVs.add("Other");
		immediatePriorCarrierLOVs.add("Safeco");
		immediatePriorCarrierLOVs.add("State Farm");
		immediatePriorCarrierLOVs.add("Travelers");
		immediatePriorCarrierLOVs.add("United Svcs Automobile Assn");
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("None");			
	}
}
