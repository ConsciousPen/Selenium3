/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
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
public class TestPolicyDiscountMultiPolicy extends HomeSSHO3BaseTest {

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void testPolicyDiscounts(@Optional("") String state) {
        mainApp().open();
        
        createCustomerIndividual();
        
        createPolicy(getTestSpecificTD("TestData"));    
        
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        new HomeSSPolicyActions.PolicyInquiry().start();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        
        Map<String, String> multiPolicyDiscount_auto = new HashMap<>();
        multiPolicyDiscount_auto.put("Discount Category", "Multi-Policy");
        multiPolicyDiscount_auto.put("Discounts Applied", "AAA Auto");
		
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(multiPolicyDiscount_auto).verify.present();
		PremiumsAndCoveragesQuoteTab.buttonCancel.click();
		
		new HomeSSPolicyActions.Endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		
		TestData td_endorsement = getTestSpecificTD("TestData_Endorsement");
		
		ApplicantTab applicantTab = new ApplicantTab();
		applicantTab.fillTab(td_endorsement);
		
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        
        Map<String, String> multiPolicyDiscount_auto_pup = new HashMap<>();
        multiPolicyDiscount_auto_pup.put("Discount Category", "Multi-Policy");
        multiPolicyDiscount_auto_pup.put("Discounts Applied", "AAA Personal Umbrella, AAA Auto");
        
        assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(multiPolicyDiscount_auto_pup)).exists();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        new BindTab().submitTab();
  
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}

