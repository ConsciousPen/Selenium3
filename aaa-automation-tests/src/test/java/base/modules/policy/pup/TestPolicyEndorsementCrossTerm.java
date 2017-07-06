/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Endorsement Cross Term for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Manual Renew
 * 4. Create cross Endorsement
 * 5. Verify 'Pended Endorsement' link is disabled
 * 6. Verify Policy status is 'Pending out of sequence completion'
 * 7. Verify 'Renewals' button is disabled
 * 8. Verify policy premium is changed
 * @details
 */
public class TestPolicyEndorsementCrossTerm extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyEndorsementCrossTerm() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("Manual Renew for Policy #" + policyNumber);
        policy.createRenewal(tdPolicy.getTestData("Issue", "TestData_ExistentBillingAccount"));

        PolicySummaryPage.linkPolicy.click();

        log.info("TEST: Cross Endorsement for Policy #" + policyNumber);
        policy.createEndorsement(tdPolicy.getTestData("Endorsement", "TestData_Plus1Month")
                .adjust(tdSpecific.getTestData("TestData").resolveLinks())
                .adjust(tdPolicy.getTestData("Issue", "TestData_ExistentBillingAccount").resolveLinks()));

        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));

        PolicySummaryPage.buttonRenewals.verify.enabled(false);
    }
}
