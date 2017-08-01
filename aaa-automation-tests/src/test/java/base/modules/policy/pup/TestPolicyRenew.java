/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Deivydas Piliukaitis
 * @name Test Renew Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Manual Renew Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Click Renewals Policy
 * 6. Verify Policy status is 'Policy Pending'
 * @details
 */
public class TestPolicyRenew extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRenew() {
        mainApp().open();

        createCustomerIndividual();

        TestData tdPolicy = testDataManager.policy.get(getPolicyType());
        createPolicy(getPolicyTD("DataGather", "TestData")
                .adjust(getPolicyTD("CopyFromQuote", "TestData"))
                .adjust(getTestSpecificTD("TestData"))
                .adjust(getPolicyTD("Issue", "TestData").resolveLinks()));

        log.info("TEST: Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.createRenewal(getPolicyTD("Issue", "TestData_ExistentBillingAccount"));

        PolicySummaryPage.linkPolicy.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonRenewals.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
    }
}
