/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author
 * @name Test Flat Endorsement for Auto Policy
 * @scenario
 * @details
 */
public class TestPolicyEndorsementAdd extends AutoCaChoiceBaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.SMOKE, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE)
	public void testPolicyEndorsementAdd(@Optional("CA") String state) {
		mainApp().open();

		createCustomerIndividual();
		createPolicy();

		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: Endorsement for Policy #{}", PolicySummaryPage.labelPolicyNumber.getValue());

		TestData tdEndorsement = getTestSpecificTD("TestData");
		//BUG PAS-6310 VIN retrieve doesnt work when adding or editing a vehicle in Endorsement for CA auto product
		getPolicyType().get().createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			softly.assertThat(PolicySummaryPage.tablePolicyDrivers).hasRows(2);
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles).hasRows(2);
			softly.assertThat(PolicySummaryPage.tableInsuredInformation).hasRows(2);

			softly.assertThat(policyPremium).isNotEqualTo(PolicySummaryPage.TransactionHistory.getEndingPremium());
		});
	}
}
