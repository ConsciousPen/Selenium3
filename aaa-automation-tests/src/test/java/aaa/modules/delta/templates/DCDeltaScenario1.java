package aaa.modules.delta.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.delta.HssQuoteDataGatherHelper;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import aaa.toolkit.webdriver.WebDriverHelper;
import toolkit.datax.TestData;

public class DCDeltaScenario1 extends BaseTest {
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
        log.info("DELTA CT SC1: "+scenarioPolicyType+" Quote created with #" + quoteNumber); 		
	}
	
	public void verifyLOVsOfImmediatePriorCarrier() {
		mainApp().open(); 
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		HssQuoteDataGatherHelper.verifyLOVsOfImmediatePriorCarrierThenSaveAndExit(immediatePriorCarrierLOVs);
	}
	
	public void purchasePolicy(TestData td,  String scenarioPolicyType) {
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
        
        log.info("DELTA DC SC1: "+scenarioPolicyType+" Policy created with #" + policyNumber);	 
	}
	
	public void verifyDeclarationDocumentsGenerated() {
		 //TODO add verification that Declaration documents are generated at NB policy issue and in DB. 
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
				//odd_tab.saveAndExit();
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
		//mainApp().close();
	}
	
	private static ArrayList<String> immediatePriorCarrierLOVs = new ArrayList<>();
	static {
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)");
		immediatePriorCarrierLOVs.add("AAA Other");
		immediatePriorCarrierLOVs.add("AAA-Michigan (ACG)"); 
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Rewrite");
		immediatePriorCarrierLOVs.add("AAA-NoCal (CSAA IG) Sold/Bought");
		immediatePriorCarrierLOVs.add("AAA-SoCal (ACSC)");
		immediatePriorCarrierLOVs.add("AIG");
		immediatePriorCarrierLOVs.add("Alfa Insurance"); 
		immediatePriorCarrierLOVs.add("Allstate");
		immediatePriorCarrierLOVs.add("Assurant"); 
		immediatePriorCarrierLOVs.add("Chubb"); 
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military"); 
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Military AAA"); 
		immediatePriorCarrierLOVs.add("Compliant w/o Ins-Newly Purchased"); 
		immediatePriorCarrierLOVs.add("Donegal"); 
		immediatePriorCarrierLOVs.add("Erie");
		immediatePriorCarrierLOVs.add("Farmers");
		immediatePriorCarrierLOVs.add("GMAC"); 
		immediatePriorCarrierLOVs.add("Hanover"); 
		immediatePriorCarrierLOVs.add("Harleysville");
		immediatePriorCarrierLOVs.add("Hartford"); 
		immediatePriorCarrierLOVs.add("High Point"); 
		immediatePriorCarrierLOVs.add("Homesite"); 
		immediatePriorCarrierLOVs.add("Kemper"); 
		immediatePriorCarrierLOVs.add("Liberty Mutual"); 
		immediatePriorCarrierLOVs.add("Loudoun Mutual");
		immediatePriorCarrierLOVs.add("Mercury"); 
		immediatePriorCarrierLOVs.add("MetLife"); 
		immediatePriorCarrierLOVs.add("Nationwide"); 
		immediatePriorCarrierLOVs.add("NJ Cure");
		immediatePriorCarrierLOVs.add("NJ Manufacturers"); 
		immediatePriorCarrierLOVs.add("NJ Skylands"); 
		immediatePriorCarrierLOVs.add("No Prior");
		immediatePriorCarrierLOVs.add("Northern Neck"); 
		immediatePriorCarrierLOVs.add("Ohio Casualty"); 
		immediatePriorCarrierLOVs.add("Other Carrier"); 
		immediatePriorCarrierLOVs.add("Palisades"); 
		immediatePriorCarrierLOVs.add("Proformance");
		immediatePriorCarrierLOVs.add("Rockingham Mutual"); 
		immediatePriorCarrierLOVs.add("Safeco"); 
		immediatePriorCarrierLOVs.add("Selective"); 
		immediatePriorCarrierLOVs.add("State Farm"); 
		immediatePriorCarrierLOVs.add("Travelers"); 
		immediatePriorCarrierLOVs.add("USAA");
		immediatePriorCarrierLOVs.add("VA Farm Bureau"); 
		immediatePriorCarrierLOVs.add("Western United");
		immediatePriorCarrierLOVs.add("White Mountains");	
	}
}
