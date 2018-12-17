/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Jelena Dembovska
 * @name Test Endorsement for Auto Policy
 * @scenario
 * 1. Open existing policy and initiate flat endorsement
 * 2. Add additional insured, driver and vehicle
 * 3. Bind endorsement
 * @details
 */
public class TestPolicyEndorsementAdd extends AutoSSBaseTest {

    @Parameters({"state"})
    @StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.SMOKE, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testPolicyEndorsementAdd(@Optional("") String state) {
    	
    	if (getUserGroup().equals(UserGroups.B31.get())) {
    		mainApp().open(getLoginTD(UserGroups.QA));
			getCopiedPolicy();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			mainApp().close();
			
			//re-login with B31 user
			mainApp().open(getLoginTD(UserGroups.B31));
			MainPage.QuickSearch.buttonSearchPlus.click();
			SearchPage.openPolicy(policyNumber);
			log.info("Verifying 'Endorsement' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Endorsement' is available").doesNotContainOption("Endorsement");
    	}
    	else {
    		 mainApp().open();   	        
    		 getCopiedPolicy();

    		 Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

    		 log.info("TEST: Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
    	        
    		 TestData endorsementTd = getTestSpecificTD("TestData");
    		 policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
    	       
    		 CustomSoftAssertions.assertSoftly(softly -> {
    			 softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
    			 softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    			 softly.assertThat(PolicySummaryPage.tablePolicyDrivers).hasRows(2);
    			 softly.assertThat(PolicySummaryPage.tablePolicyVehicles).hasRows(2);
    			 softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);

    			 softly.assertThat(PolicySummaryPage.TransactionHistory.getEndingPremium()).isNotEqualTo(policyPremium);
    		 });
    	}
    }
}
