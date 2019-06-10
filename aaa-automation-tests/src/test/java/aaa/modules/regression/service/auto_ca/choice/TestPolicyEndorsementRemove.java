/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.regression.sales.auto_ca.select.TestPolicyCreationBig;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Jelena Dembovska
 * <b> Test Endorsement for Auto Policy with removal </b>
 * <p> Steps:
 * <p> 1. initiate endorsement
 * <p> 2. remove second named insured
 * <p> 3. driver tab is opened, driver which is related to removed insured is removed automatically
 * <p> 4. go to Vehicle tab, remove second vehicle
 * <p> 5. fill all mandatory fields required to bind
 * <p> 6. check drivers and vehicles are removed
 *
 */
public class TestPolicyEndorsementRemove extends AutoCaSelectBaseTest {


    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE)
    public void testPolicyEndorsementRemove(@Optional("CA") String state) {
    	
    	new TestPolicyCreationBig().testPolicyCreationBig(state);
		
		//1. initiate endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		
		//2. remove second named insured
		GeneralTab.tableInsuredList.removeRow(2);

		//3. driver tab is opened, driver which is related to removed insured is removed automatically
		assertThat(DriverTab.tableDriverList).hasRows(1);
		
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.GENERAL.get());
		
		TestData class_td = getTestSpecificTD("TestData"); 
		
		//fill 'authorized by = qa' required on Bind
		new GeneralTab().fillTab(class_td);
		
		
		//4. go to Vehicle tab, remove second vehicle
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		
		VehicleTab.tableVehicleList.removeRow(2);
		assertThat(VehicleTab.tableVehicleList).hasRows(1);
				
		//5. fill all mandatory fields required to bind
        policy.getDefaultView().fillFromTo(class_td, VehicleTab.class, DocumentsAndBindTab.class, true);

        new DocumentsAndBindTab().submitTab();

	    CustomSoftAssertions.assertSoftly(softly -> {
		    //6. check drivers and vehicles are removed
		    softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		    softly.assertThat(PolicySummaryPage.tablePolicyDrivers).hasRows(1);
		    softly.assertThat(PolicySummaryPage.tablePolicyVehicles).hasRows(1);
		    softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(1);
	    });
    }
    
    
}
