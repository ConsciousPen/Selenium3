/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Ryan Yu
 * <b> Test OOS Endorsement for Home Policy </b>
 * <p> Steps:
 * <p> 1. Create Customer
 * <p> 2. Create Home Policy
 * <p> 3. Create Midterm Endorsement
 * <p> 4. Create OOS Endorsement
 * <p> 5. Verify 'Pended Endorsement' button is disabled
 * <p> 6. Verify Policy status is 'Pending out of sequence completion'
 * <p> 7. Verify Policy Premium is changed
 *
 */
public class TestPolicyEndorsementOos extends HomeCaHO3BaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
	public void testPolicyEndorsementOos(@Optional("CA") String state) {
		mainApp().open();

		getCopiedPolicy();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("MidTerm Endorsement for Policy #" + policyNumber);
		policy.createEndorsement(getPolicyTD("Endorsement", "TestData_Plus3Days").adjust(getTestSpecificTD("TestData").resolveLinks()));

		Dollar policyPremium2 = PolicySummaryPage.TransactionHistory.getEndingPremium();

		log.info("OOS Endorsement for Policy #" + policyNumber);
		policy.createEndorsement(getPolicyTD("Endorsement", "TestData").adjust(getTestSpecificTD("TestData2").resolveLinks()));

		assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
		assertThat(policyPremium).isNotEqualTo(policyPremium2);
		assertThat(policyPremium).isNotEqualTo(PolicySummaryPage.TransactionHistory.getEndingPremium());
	}
}
