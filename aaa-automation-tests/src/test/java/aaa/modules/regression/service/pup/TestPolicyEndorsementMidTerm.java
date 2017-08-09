/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import aaa.modules.regression.sales.pup.TestPolicyBackdated;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;

/**
 * @author Lina Li
 * @name Test Midterm Endorsement for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create UPersonal Umbrella Policy 
 * 3. Create endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * @details
 */
public class TestPolicyEndorsementMidTerm extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyEndorsementMidTerm() {
    	new TestPolicyBackdated().testPolicyBackdated();
        
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("TEST: MidTerm Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        
        TestData endorsementTd = getTestSpecificTD("TestData");
	    policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
	     

        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
    }
}
