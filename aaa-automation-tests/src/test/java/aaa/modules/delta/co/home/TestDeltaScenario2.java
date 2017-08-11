package aaa.modules.delta.co.home;

import java.util.HashMap;
import java.util.Map;

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

public class TestDeltaScenario2 extends HomeSSHO3BaseTest {
	
	private String quoteNumber;
	private String policyNumber;
	private TestData td_sc2; 
	
	private static Map<String, String> endorsement_HS0312 = new HashMap<>();
	static {
		endorsement_HS0312.put("Form ID", "HS 03 12");
		endorsement_HS0312.put("Name", "Windstorm Or Hail Deductible - Percentage"); 
	}
	
	private static Map<String, String> endorsement_HS0493 = new HashMap<>(); 
	static {
		endorsement_HS0493.put("Form ID", "HS 04 93"); 
		endorsement_HS0493.put("Name", "Actual Cash Value - Windstorm Or Hail Losses"); 
	}
	
	@Test
    @TestInfo(component = "Policy.HomeSS")
	public void testSC2_TC01() {
		mainApp().open();
		
		td_sc2 = getTestSpecificTD("TestData");
		
        createCustomerIndividual();     
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td_sc2, EndorsementTab.class);
        
        EndorsementTab endorsementTab = new EndorsementTab(); 
        
        CustomAssert.enableSoftMode();
        endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0312).verify.present();	
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present(false);
		
		endorsementTab.fillTab(td_sc2);
		
		endorsementTab.tblIncludedEndorsements.getRow(endorsement_HS0312).verify.present();			
		CustomAssert.assertTrue(endorsementTab.verifyLinkEditIsPresent("HS 03 12")); 
		CustomAssert.assertTrue(endorsementTab.verifyLinkRemoveIsPresent("HS 03 12"));
		endorsementTab.tblOptionalEndorsements.getRowContains(endorsement_HS0493).verify.present(); 
		
		endorsementTab.submitTab();
		
		policy.getDefaultView().fillFromTo(td_sc2, PremiumsAndCoveragesQuoteTab.class, BindTab.class);
        BindTab.buttonSaveAndExit.click();
        
        quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("DELTA CO SC2: HO3-Legasy Quote created with #" + quoteNumber);

        CustomAssert.assertAll();             
	}
	
	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testSC2_TC02() {
		mainApp().open();
		SearchPage.openQuote(quoteNumber);	
		//Generate On-Demand documents for quote
	}

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testSC2_TC03() {
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
        
        log.info("DELTA CO SC2: HO3-Legasy Policy created with #" + policyNumber);
	}
	
	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testSC2_TC04() {
		mainApp().open(); 
		
		SearchPage.openPolicy(policyNumber);
		//Generate On-Demand documents for policy
	}
	
}
