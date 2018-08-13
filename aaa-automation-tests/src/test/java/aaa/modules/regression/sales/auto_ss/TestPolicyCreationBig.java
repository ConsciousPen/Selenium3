/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Auto policy creation with 2 NI/2Drivers/2Vehicles
 * @scenario
 * @details
 */
public class TestPolicyCreationBig extends AutoSSBaseTest {

    @Parameters({"state"})
    @StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testPolicyCreationBig(@Optional("") String state) {

    	mainApp().open();
        
        createCustomerIndividual();
        
		log.info("Policy Creation Started...");
		
        TestData bigPolicy_td = getTestSpecificTD("TestData");
		getPolicyType().get().createPolicy(bigPolicy_td);        
        
		CustomSoftAssertions.assertSoftly(softly -> {
			
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			
			softly.assertThat(PolicySummaryPage.tablePolicyDrivers).hasRows(2);
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles).hasRows(2);
			softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);
			
		});	
    }
}
