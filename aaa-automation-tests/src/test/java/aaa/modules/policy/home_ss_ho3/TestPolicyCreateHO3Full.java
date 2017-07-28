/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Create HSS Policy HO3-Full
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all fields on all tabs, order reports. 
 * 4. Add HS 04 92 endorsement form 
 * (after form HS 04 92 added, should go back to Reports tab and order PPC report for added in form address).
 * 5. Navigate to Premiums&Coverages Quote tab and calculate premium.
 * 6. Fill the rest of tabs.
 * 7. Purchase policy.
 * 8. Verify policy status is Active on Consolidated policy view.
 * @details
 */
public class TestPolicyCreateHO3Full extends HomeSSHO3BaseTest {

	private TestData td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
	private TestData td_orderPPC = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData_OrderPPC");
	
    @Test
    @TestInfo(component = "Policy.HomeSS")
    public void testPolicyCreation() {
        mainApp().open();
        
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(td, EndorsementTab.class, true);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        ReportsTab reportsTab = new ReportsTab();
        reportsTab.fillTab(td_orderPPC);
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        
        policy.getDefaultView().fillFromTo(td, PremiumsAndCoveragesQuoteTab.class, PurchaseTab.class, true); 
        new PurchaseTab().submitTab();       
        
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: HSS Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
    }
}
