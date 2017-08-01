/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.actiontabs.CopyQuoteActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Viachaslau Markouski
 * @name Test Copy Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated Home (Preconfigured) Quote
 * 3. Copy Quote
 * 4. Verify copied quote number is different as initial
 * 5. Verify quote status is 'Data Gathering'
 * @details
 */
public class TestQuoteCopy extends HomeSSHO3BaseTest {

    @Test(groups = "6.2.2_Benefits-Base_CreateMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteCopy() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        GeneralTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Copy From Quote #" + policyNumber);
        policy.copyQuote().perform(getPolicyTD("CopyFromQuote", "TestData"));

        String policyNumberCopied = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Copied Quote #" + policyNumberCopied);

        CustomAssert.assertFalse(
                String.format("Copied quote number %s is the same as initial %s", policyNumberCopied, policyNumber),
                policyNumber.equals(policyNumberCopied));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
    }

    @Test(groups = "6.2.2_Benefits-Base_CreateMasterQuote")
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteCopyCancellations() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        GeneralTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Copy and reject confirmation for Quote #" + policyNumber);
        policy.copyQuote().start().getView().fill(getPolicyTD("CopyFromQuote", "TestData"));

        CopyQuoteActionTab.buttonOk.click();
        Page.dialogConfirmation.reject();

        NavigationPage.Verify.viewTabSelected("Copy from Quote");

        log.info("TEST: Cancel Copy for Quote #" + policyNumber);
        CopyQuoteActionTab.buttonCancel.click();

        String policyNumberActual = PolicySummaryPage.labelPolicyNumber.getValue();

        CustomAssert.assertTrue(String.format("Quote (%s) copied, expected - not", policyNumber),
                policyNumber.equals(policyNumberActual));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
    }
}
