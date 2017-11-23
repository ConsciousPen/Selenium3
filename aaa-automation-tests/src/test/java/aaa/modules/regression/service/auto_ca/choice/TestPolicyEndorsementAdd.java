/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author
 * @name Test Flat Endorsement for Auto Policy
 * @scenario
 * @details
 */
public class TestPolicyEndorsementAdd extends AutoCaChoiceBaseTest {

	@Parameters({"state"})
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

		CustomAssert.enableSoftMode();

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		PolicySummaryPage.tablePolicyDrivers.verify.rowsCount(2);
		PolicySummaryPage.tablePolicyVehicles.verify.rowsCount(2);
		PolicySummaryPage.tableInsuredInformation.verify.rowsCount(2);

		CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

}
