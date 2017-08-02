/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.actiontabs.SuspendQuoteActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PrefillTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Add and Remove Suspense for Auto Quote
 * @scenario
 * 1. Create Customer
 * 2. Initiated Auto Base Quote
 * 3. Suspend Quote
 * 4. Verify quote status is 'Suspended'
 * 5. Remove Suspense for Quote
 * 6. Verify quote status is 'Data Gathering'
 * @details
 */
public class TestQuoteSuspenseAddRemove extends AutoCaSelectBaseTest {

    @Test(groups = {"7.2_AUTOP,HOME_UC_SuspendQuote", "7.2_AUTOP,HOME_UC_RemoveQuoteSuspense"})
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteSuspenseAddRemove() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        PrefillTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Suspend Quote #" + policyNumber);
        policy.suspendQuote().perform(getPolicyTD("SuspendQuote", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.SUSPENDED);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Suspend Quote %s", policyNumber));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");

        log.info("TEST: Remove Suspense for Quote #" + policyNumber);
        policy.removeSuspendQuote().perform(new SimpleDataProvider());
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Suspend status removed for Quote %s", policyNumber));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");
    }

    @Test(groups = {"7.2_AUTOP,HOME_UC_SuspendQuote", "7.2_AUTOP,HOME_UC_RemoveQuoteSuspense"})
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteSuspenseAddRemoveCancellations() {
        mainApp().open();

        createCustomerIndividual();

        policy.initiate();
        PrefillTab.buttonSaveAndExit.click();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Initiated Quote #" + policyNumber);

        log.info("TEST: Suspend and reject confirmation for Quote #" + policyNumber);
        policy.suspendQuote().start().getView().fill(getPolicyTD("SuspendQuote", "TestData"));
        SuspendQuoteActionTab.buttonOk.click();
        Page.dialogConfirmation.reject();

        NavigationPage.Verify.viewTabSelected("Suspend Quote");

        log.info("TEST: Cancel Suspend for Quote #" + policyNumber);
        SuspendQuoteActionTab.buttonCancel.click();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Suspend Quote %s", policyNumber));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Canceled");

        log.info("TEST: Suspend Quote #" + policyNumber);
        policy.suspendQuote().perform(getPolicyTD("SuspendQuote", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.SUSPENDED);

        log.info("TEST: Remove Suspense and reject confirmation for Quote #" + policyNumber);
        policy.removeSuspendQuote().start();
        Page.dialogConfirmation.reject();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.SUSPENDED);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Suspend status removed for Quote %s", policyNumber));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Canceled");
    }
}
