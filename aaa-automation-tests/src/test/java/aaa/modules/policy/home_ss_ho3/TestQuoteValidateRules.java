package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 *
 * <pre>
 * TestPolicyFuturedated steps:
 * 1. Create a customer or open existed.
 * 2. Initiate new HSS quote creation.
 * 3. On General tab set Effective Date as Today + 91 days and verify error message is displaying under Effective Date field.
 * 4. Set Effective Date as Today + 10 days. 
 * 5. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 6. Bind a policy.
 * 7. Verify policy status is Policy Pending.  
 * 
 * TestPolicyBackdated steps:
 * 1. Create a customer or open existed.
 * 2. Initiate new HSS quote creation.
 * 3. On General tab set Effective Date as Today - 10 days. 
 * 4. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 5. Bind a policy and verify Error tab is displaying with error message. 
 * 6. Navigate to General tab, change Effective Date to Today - 3 days. 
 * 7. Recalculate premium and bind policy. 
 * 8. Verify policy status is Active.
 * 
 * 5700:US PO-02 Capture Effective Date
 * 19740:US CL Capture Effective Date - V02
 * </pre>
 **/
public class TestQuoteValidateRules extends HomeSSBaseTest {
	
	private TestData td = getStateTestData(tdPolicy, "DataGather", "TestData");
		
	@Test
    @TestInfo(component = "Quote.HomeSS")
    public void testPolicyFuturedated() {
        mainApp().open();
        
        createCustomerIndividual();

        TestData effective_date_today_plus_10_days = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_TodayPlus10Days");
        TestData effective_date_today_plus_91_days = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_TodayPlus91Days");
        		
        policy.initiate();
       
        GeneralTab generalTab = new GeneralTab();
        generalTab.fillTab(td);
        generalTab.fillTab(effective_date_today_plus_91_days);
        
        generalTab.verifyFieldHasMessage("Effective date", "Policy effective date cannot be more than 90 days from today's date.");
        
        generalTab.fillTab(effective_date_today_plus_10_days);
        generalTab.submitTab();
        
        policy.getDefaultView().fillFromTo(td, ApplicantTab.class, PurchaseTab.class, true);  
        new PurchaseTab().submitTab();
                
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);        
        
	}

	@Test
	@TestInfo(component = "Quote.HomeSS")
	public void testBackdatedPolicy() {
		mainApp().open();
        
        createCustomerIndividual();
        
        TestData effective_date_today_minus_10_days = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_TodayMinus10Days");
        TestData effective_date_today_minus_3_days = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_TodayMinus3Days");
        
        policy.initiate();
        GeneralTab generalTab = new GeneralTab();
        generalTab.fillTab(td);
        generalTab.fillTab(effective_date_today_minus_10_days);
        generalTab.submitTab();
        
        policy.getDefaultView().fillFromTo(td, ApplicantTab.class, BindTab.class);  
        new BindTab().btnPurchase.click();
        
        ErrorTab errorTab = new ErrorTab();
        errorTab.tblErrorsList.getRow(1).getCell("Message").verify.value("Policy effective date cannot be backdated more than three days from today's d...");    
        errorTab.cancel();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.GENERAL.get());
        generalTab.fillTab(effective_date_today_minus_3_days);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);  
        new PurchaseTab().submitTab();
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);     
        
	}
	
}
