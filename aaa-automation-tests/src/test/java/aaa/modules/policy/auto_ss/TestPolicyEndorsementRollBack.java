/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Lina Li
 * @name Test Roll Back Endorsement for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create AutoSS Policy
 * 3. Create Midterm Endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * 7. Roll Back Endorsement
 * 8. Verify Ending Premium was roll back
 * @details
 */
public class TestPolicyEndorsementRollBack extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyEndorsementRollBack() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("MidTerm Endorsement for Policy #" + policyNumber);
        
        TestData endorsement_td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
        policy.createEndorsement(endorsement_td.adjust(tdPolicy.getTestData("Endorsement", "TestData_Plus1Month")));

        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));

        log.info("TEST: Roll Back Endorsement for Policy #" + policyNumber);
        policy.rollBackEndorsement().perform(tdPolicy.getTestData("EndorsementRollBack", "TestData"));
        CustomAssert.assertTrue(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
    }
}
