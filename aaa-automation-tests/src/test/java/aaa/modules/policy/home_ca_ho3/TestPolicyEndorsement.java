/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

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

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyEndorsement() {
		mainApp().open();

		getCopiedPolicy();

		log.info("TEST: Endorsement for CAH Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());

		TestData td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData").adjust(tdPolicy.getTestData("Endorsement", "TestData"));

		new HomeCaPolicyActions.Endorse().perform(td);

		policy.getDefaultView().fillUpTo(td, ApplicantTab.class, true);

		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());

		policy.getDefaultView().fillFromTo(td, ReportsTab.class, BindTab.class, true);
		new BindTab().submitTab();

		CustomAssert.enableSoftMode();

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		PolicySummaryPage.tableInsuredInformation.verify.rowsCount(2);

		CustomAssert.assertAll();

	}
}
