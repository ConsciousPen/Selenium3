/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Viachaslau Markouski
 * @name Test Midterm Endorsement for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * @details
 */
public class TestPolicyEndorsementMidTerm extends HomeSSBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyEndorsementMidTerm() {
		mainApp().open();
		
		createCustomerIndividual();
		createPolicy(getStateTestData(tdPolicy, "DataGather", "TestData")
				.adjust("GeneralTab|Effective date", "/today-2d:MM/dd/yyyy")
				.adjust("GeneralTab|Property insurance base date with CSAA IG", "/today-2d:MM/dd/yyyy"));

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("TEST: MidTerm Endorsement for Policy #" + policyNumber);
		policy.createEndorsement(tdPolicy.getTestData("Endorsement", "TestData")
				.adjust(tdSpecific.getTestData("TestData").resolveLinks()));

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
	}
}
