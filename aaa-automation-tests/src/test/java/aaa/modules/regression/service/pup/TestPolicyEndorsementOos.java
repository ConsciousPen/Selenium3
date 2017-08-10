/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Yonggang Sun
 * @name Test OOS Endorsement for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Create Midterm Endorsement
 * 4. Create OOS Endorsement
 * 5. Verify 'Pended Endorsement' button is disabled
 * 6. Verify Policy status is 'Pending out of sequence completion'
 * 7. Verify Policy Premium is changed
 * @details
 */
public class TestPolicyEndorsementOos extends PersonalUmbrellaBaseTest {
	
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyEndorsementOos() {
        mainApp().open();
        getCopiedPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("MidTerm Endorsement for Policy #" + policyNumber);
        TestData endorsement_td = getPolicyTD("TestPolicyEndorsement", "TestData");
        policy.createEndorsement(endorsement_td.adjust(getPolicyTD("Endorsement", "TestData_Plus1Month")));
        Dollar policyPremium2 = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("OOS Endorsement for Policy #" + policyNumber);
        TestData endorsement_td1 = getPolicyTD("TestPolicyEndorsement", "TestData");
        policy.createEndorsement(endorsement_td1.adjust(getPolicyTD("Endorsement", "TestData")));

        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        CustomAssert.assertFalse(policyPremium.equals(policyPremium2));
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
    }
}
