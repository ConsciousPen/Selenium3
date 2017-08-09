/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Jelena Dembovska
 * @name Test Endorsement for Auto Policy
 * @scenario
 * 1. Open existing policy and initiate flat endorsement
 * 2. Add additional insured, driver and vehicle
 * 3. Bind endorsement
 * @details
 */
public class TestPolicyEndorsementAdd extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyEndorsementAdd() {
        mainApp().open();
        
        createCustomerIndividual();
        createPolicy();

        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("TEST: Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        TestData endorsementTd = getTestSpecificTD("TestData");
        policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));

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
