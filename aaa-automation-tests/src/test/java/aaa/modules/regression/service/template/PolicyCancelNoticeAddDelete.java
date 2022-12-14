/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;

import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.impl.SimpleDataProvider;

/**
 * @author Lina Li
 * @name Test Policy Cancel Notice Add and Delete
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Cancel Notice for Policy
 * 4. Verify 'Cancel Notice' flag is displayed in the policy overview header
 * 5. Delete Cancel Notice for Policy
 * 6. Verify 'Cancel Notice' flag is not displayed in the policy overview header
 * @details
 */

public abstract class PolicyCancelNoticeAddDelete extends PolicyBaseTest {
	   
    public void testPolicyCancelNoticeAddDelete() {
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
			log.info("Verifying 'Cancel Notice' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Cancel Notice' is available").doesNotContainOption("Cancel Notice");	
    	}
    	else if (getUserGroup().equals(UserGroups.F35.get()) || getUserGroup().equals(UserGroups.G36.get())) {
    		mainApp().open();
    		createCustomerIndividual();
            createPolicy();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
            log.info("Verifying 'Cancel Notice' action");
			assertThat(NavigationPage.comboBoxListAction).as("Action 'Cancel Notice' is available").doesNotContainOption("Cancel Notice");            
    	}
    	else {
    		mainApp().open();
    		getCopiedPolicy();
           
            String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

            log.info("TEST: Cancel Notice for Policy #" + policyNumber);
            policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData"));
            assertThat(PolicySummaryPage.labelCancelNotice).isPresent();
            
            log.info("TEST: Delete Cancel Notice for Policy #" + policyNumber);
    		policy.deleteCancelNotice().perform(new SimpleDataProvider());
    		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);
    	}
    }   
}
