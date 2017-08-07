/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.templates;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

/**
 * @author Xiaolan Ge
 * @name Test Roll Back Endorsement for Auto Policy
 * @scenario
 * 1.Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Create Midterm Endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * 7. Roll Back Endorsement
 * 8. Verify Ending Premium was roll back
 * @details
 */
public abstract class PolicyEndorsementRollBack extends PolicyBaseTest {

    public void testPolicyEndorsementRollBack() {
    	mainApp().open();
        getCopiedPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("MidTerm Endorsement for Policy #" + policyNumber);
        
        TestData endorsement_td = getPolicyTD("TestPolicyEndorsementAdd", "TestData");
        policy.createEndorsement(endorsement_td.adjust(getPolicyTD("Endorsement", "TestData_Plus10Day")));

        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));

        log.info("TEST: Roll Back Endorsement for Policy #" + policyNumber);
        policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData"));
        CustomAssert.assertTrue(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
    }
}
