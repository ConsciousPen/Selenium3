/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Jelena Dembovska
 * @name Test Endorsement for Auto Policy with removal
 * @scenario
 * @details
 */
public class TestPolicyEndorsementRemove extends AutoSSBaseTest {

	private TestData class_td = getTestSpecificTD("TestData"); 
	
    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyEndorsementRemove() {
    	
        mainApp().open();
        
        createCustomerIndividual();
        
		log.info("Policy Creation Started...");
		
        TestData bigPolicy_td = getPolicyTD("TestPolicyCreationBig", "TestData");
		getPolicyType().get().createPolicy(bigPolicy_td);
		
        CustomAssert.enableSoftMode();
        
		//1. initiate endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		
		//2. remove second named insured
		new GeneralTab().removeInsured(2);

		//3. driver tab is opened, driver which is related to removed insured is removed automatically
		DriverTab.tableDriverList.verify.rowsCount(1);
		
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
		
		//fill 'authorized by = qa' required on Bind
		new GeneralTab().fillTab(class_td);
		
		
		//4. go to Vehicle tab, remove second vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		
		new VehicleTab().removeVehicle(2);
		VehicleTab.tblVehicleList.verify.rowsCount(1);
				
		//5. fill all mandatory fields required to bind
        policy.getDefaultView().fillFromTo(class_td, VehicleTab.class, DocumentsAndBindTab.class, true);

        new DocumentsAndBindTab().submitTab();
        
        //6. check drivers and vehicles are removed
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        PolicySummaryPage.tablePolicyDrivers.verify.rowsCount(1);
        PolicySummaryPage.tablePolicyVehicles.verify.rowsCount(1);
        PolicySummaryPage.tableInsuredInformation.verify.rowsCount(1);
        
        CustomAssert.assertAll();
    }
    
    
}
