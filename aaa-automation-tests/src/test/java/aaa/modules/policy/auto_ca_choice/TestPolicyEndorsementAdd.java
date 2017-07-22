/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_choice;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

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

    @Test
    @TestInfo(component = "Policy.AutoCA")
    public void testPolicyEndorsementAdd() {
        mainApp().open();
        
        createCustomerIndividual();
        createPolicy();

        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("TEST: Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        TestData tdEndorsement = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
        getPolicyType().get().createEndorsement(tdEndorsement.adjust(tdPolicy.getTestData("Endorsement", "TestData")));

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
