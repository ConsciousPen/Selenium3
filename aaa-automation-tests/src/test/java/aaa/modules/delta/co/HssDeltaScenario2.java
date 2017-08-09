package aaa.modules.delta.co;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class HssDeltaScenario2 extends HomeSSHO3BaseTest {
	
	private String quoteNumber;
	private String policyNumber;
	TestData td_sc2; 
	
	@Test
    @TestInfo(component = "Policy.HomeSS")
	public void testSC1_TC01() {
		mainApp().open();
		
		td_sc2 = getTestSpecificTD("TestData");
		
        createCustomerIndividual();     
        quoteNumber = createQuote(td_sc2);
        
        log.info("DELTA CO SC2: HO3 Quote created with #" + quoteNumber);              
	}
	
	@Test
	@TestInfo(component="Policy.HomeSS")
	public void testSC2_TC02() {
		mainApp().open();
		
		SearchPage.openQuote(quoteNumber);	
		
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.ENDORSEMENT.get());
		EndorsementTab endorsementTab = new EndorsementTab(); 

		CustomAssert.enableSoftMode();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "438B FUNS").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 03 12").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 10").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 20").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 35").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 40").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 41").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 54").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 55").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 90").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 04 95").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 09 06").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 09 26").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 09 65").verify.present();
		endorsementTab.tblIncludedEndorsements.getRow("Form ID", "HS 24 73").verify.present();
		
		
		EndorsementTab.buttonSaveAndExit.click();	
		CustomAssert.assertAll();
	}

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testSC2_TC03() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		//Generate On-Demand documents for quote
	}

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testSC2_TC04() {
		mainApp().open();
		
		SearchPage.openQuote(quoteNumber);	
		policy.dataGather().start();
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		new PremiumsAndCoveragesQuoteTab().calculatePremium(); 
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		policy.getDefaultView().fillFromTo(td_sc2, BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CO SC2: HO3 Policy created with #" + policyNumber);
	}
	
	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testSC1_TC05() {
		mainApp().open(); 
		
		SearchPage.openPolicy(policyNumber);
		//Generate On-Demand documents for policy
	}
	
}
