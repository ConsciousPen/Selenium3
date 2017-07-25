/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
//import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Multi-Policy discount
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium. 
 * 4. Purchase policy.
 * 5. Verify policy status is Active on Consolidated policy view.
 * @details
 */
public class TestPolicyDiscounts extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.HomeSS")
    public void testPolicyDiscounts() {
        mainApp().open();
        
        createCustomerIndividual();
        
        createPolicy(getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData"));    
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        new HomeSSPolicyActions.PolicyInquiry().start();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        
        Map<String, String> multiPolicyDiscount_auto = new HashMap<>();
        multiPolicyDiscount_auto.put("Discount Category", "Multi-Policy");
        multiPolicyDiscount_auto.put("Discounts Applied", "AAA Auto");
		
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(multiPolicyDiscount_auto).verify.present();
		PremiumsAndCoveragesQuoteTab.buttonCancel.click();
		
		new HomeSSPolicyActions.Endorse().perform(tdPolicy.getTestData("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		
		TestData td_endorsement = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_Endorsement");
		
		ApplicantTab applicantTab = new ApplicantTab();
		applicantTab.fillTab(td_endorsement);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        
        Map<String, String> multiPolicyDiscount_auto_pup = new HashMap<>();
        multiPolicyDiscount_auto_pup.put("Discount Category", "Multi-Policy");
        multiPolicyDiscount_auto_pup.put("Discounts Applied", "AAA Personal Umbrella, AAA Auto");
        
        PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(multiPolicyDiscount_auto_pup).verify.present();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        new BindTab().submitTab();
  
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
    }
}

