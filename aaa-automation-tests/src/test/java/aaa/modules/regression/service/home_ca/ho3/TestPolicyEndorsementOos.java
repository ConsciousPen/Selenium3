/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Ryan Yu
 * @name Test OOS Endorsement for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home Policy
 * 3. Create Midterm Endorsement
 * 4. Create OOS Endorsement
 * 5. Verify 'Pended Endorsement' button is disabled
 * 6. Verify Policy status is 'Pending out of sequence completion'
 * 7. Verify Policy Premium is changed
 * @details
 */
public class TestPolicyEndorsementOos extends HomeCaHO3BaseTest {

	@Test
	@TestInfo(component = "Policy.HomeCA.Endorsement")
	public void testPolicyEndorsementOos() {
		mainApp().open();

		getCopiedPolicy();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("MidTerm Endorsement for Policy #" + policyNumber);
		policy.createEndorsement(getPolicyTD("Endorsement", "TestData_Plus3Days")
				.adjust(getTestSpecificTD("TestData").resolveLinks()));

		Dollar policyPremium2 = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("OOS Endorsement for Policy #" + policyNumber);
		policy.createEndorsement(getPolicyTD("Endorsement", "TestData")
				.adjust(getTestSpecificTD("TestData2").resolveLinks()));

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
		CustomAssert.assertFalse(policyPremium.equals(policyPremium2));
		CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
	}
}
