/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

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

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER})
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
	public void testPolicyEndorsement(@Optional("") String state) {
		mainApp().open();

		//getCopiedPolicy(); // fails by timeout
		//createCustomerIndividual();
		//createPolicy();
		
		if (getUserGroup().equals(UserGroups.F35.get())||getUserGroup().equals(UserGroups.G36.get())) {
        	createCustomerIndividual();
            createPolicy();
        }
        else {
        	getCopiedPolicy();
        }

		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: Endorsement for HSS Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		TestData td = getTestSpecificTD("TestData").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().perform(td);

		policy.getDefaultView().fillUpTo(td, ApplicantTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

		policy.getDefaultView().fillFromTo(td, ReportsTab.class, BindTab.class);
		new BindTab().submitTab();

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);

			softly.assertThat(policyPremium).isNotEqualTo(PolicySummaryPage.TransactionHistory.getEndingPremium());
		});
	}
}
