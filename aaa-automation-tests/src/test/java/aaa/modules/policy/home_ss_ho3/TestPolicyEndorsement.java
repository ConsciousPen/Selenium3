/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Olga Reva
 * @name Test Policy Endorsement
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Create new HSS policy.
 * 3. Initiate Endorsement action. 
 * 4. Fill Endorsement tab, confirm endorsement. 
 * 5. Navigate to Applicant tab, add 2nd Insured and Mailing information. 
 * 6. Navigate to Reports tab and re-order reports.
 * 7. Navigate to Property Info tab and fill sections Detached Structures and Recreational Equipment.
 * 8. Recalculate premium. 
 * 9. Bind endorsement. 
 * 10. On Policy Summary Page verify endorsement is completed: 
 * 	Second Insured is displaying; 
 * 	Pended Endorsement button is disabled;
 * 	Premium is not equal to initial policy;
 * 	Policy status is Active.
 * @details
 */

public class TestPolicyEndorsement extends HomeSSHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeSS")
	public void testPolicyEndorsement(){
		mainApp().open();

		//getCopiedPolicy();
		createCustomerIndividual();
		createPolicy();
		
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: Endorsement for HSS Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
		
		TestData td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData").adjust(tdPolicy.getTestData("Endorsement", "TestData"));
		
		policy.endorse().perform(td);
		
		policy.getDefaultView().fillUpTo(td, ApplicantTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		
		policy.getDefaultView().fillFromTo(td, ReportsTab.class, BindTab.class);
		new BindTab().submitTab();
		
        CustomAssert.enableSoftMode();
        
        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
        PolicySummaryPage.tableInsuredInformation.verify.rowsCount(2);
        
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
        
        CustomAssert.assertAll();
	}
}
