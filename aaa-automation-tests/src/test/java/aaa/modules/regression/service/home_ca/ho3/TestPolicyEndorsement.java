/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.common.enums.Constants.UserGroups;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Olga Reva
 * @name Test CA Home Policy Endorsement
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Find CAH quote or create new if quote does not exist.
 * 3. Perform Endorsement action.
 * 4. Add Named Insured, fill sections: Mailing Address, Recreational Equipment, Detached Structures.
 * 5. Calculate premium.
 * 6. Bind endorsement. 
 * 7. Check that endorsement was added.
 * @details
 */
public class TestPolicyEndorsement extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.SMOKE, Groups.REGRESSION, Groups.BLOCKER })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyEndorsement(@Optional("CA") String state) {
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

			log.info("TEST: Endorsement for CAH Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

			TestData td = getTestSpecificTD("TestData").adjust(getPolicyTD("Endorsement", "TestData"));
			policy.endorse().performAndFill(td);

			CustomSoftAssertions.assertSoftly(softly -> {

				softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled(false);
				softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

				softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);
			});
		}
	}
}
