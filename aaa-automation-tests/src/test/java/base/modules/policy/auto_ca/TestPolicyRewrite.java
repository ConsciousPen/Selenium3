/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ca;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.actiontabs.RewriteActionTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Rewrite for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto Policy
 * 3. Cancel Policy
 * 4. Rewrite Policy with new Number
 * 4a. Rewrite Policy with same Number
 * 4b. Cancel Rewrite action
 * 5. Verify Quote status is 'Data Gathering'
 * 6. Verify Quote number different the cancelled policy number
 * 6a.Verify Quote number is the same as cancelled policy number
 * @details
 */

public class TestPolicyRewrite extends AutoCaSelectBaseTest {

	private String policyNumber, effectiveDate;

    @BeforeClass
    public void createCancelledPolicy() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        effectiveDate = getPolicyTD("Rewrite", "TestDataNewNumber").getValue(
        		AutoCaMetaData.RewriteActionTab.class.getSimpleName(),
        		AutoCaMetaData.RewriteActionTab.EFFECTIVE_DATE.getLabel());

        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

        log.info("Cancelled Policy #" + policyNumber);
    }

    @Test(groups = "7.2_AUTOP,HOME_UC_RewriteCancelledPersonalPolicy")
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRewriteToNewNumber() {
        log.info("TEST: Rewrite Policy #" + policyNumber);
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataNewNumber"));

        String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Rewritten Policy #" + rewrittenPolicyNumber);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
        CustomAssert.assertFalse(String.format("Rewritten Policy Number #%s is the same as Initial Policy Number #%s",
                policyNumber, rewrittenPolicyNumber), policyNumber.equals(rewrittenPolicyNumber));

        log.info("TEST: Issue Rewritten Policy #" + rewrittenPolicyNumber);

        policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        log.info("TEST: Check Activities and User Notes for original Policy #" + policyNumber);
        MainPage.QuickSearch.search(policyNumber);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Rewrite Policy %s to Quote %s effective %s", policyNumber,
                rewrittenPolicyNumber, effectiveDate));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Finished");
    }

    @Test(groups = "7.2_AUTOP,HOME_UC_RewriteCancelledPersonalPolicy")
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRewriteCancellations() {
        log.info("TEST: Fill Rewrite form and reject confirmation for Policy #" + policyNumber);

        policy.rewrite().start().getView().fill(getPolicyTD("Rewrite", "TestDataNewNumber"));
        RewriteActionTab.buttonOk.click();
        Page.dialogConfirmation.reject();

        NavigationPage.Verify.viewTabSelected("Rewrite");

        log.info("TEST: Cancel Rewrite for Policy #" + policyNumber);

        RewriteActionTab.buttonCancel.click();

        CustomAssert.enableSoftMode();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, String.format(
                "Rewritten Quote %s effective %s from Canceled %s", policyNumber,
                effectiveDate, policyNumber));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(1, "Canceled");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();

        log.info("TEST: Rewrite and check that Quote Data Gather mode is opened for Policy #" + policyNumber);
        policy.rewrite().start();
        policy.rewrite().getView().fill(getPolicyTD("Rewrite", "TestDataNewNumber"));

        RewriteActionTab.buttonOk.click();
        Page.dialogConfirmation.confirm();

        NavigationPage.Verify.viewTabSelected("General");
        RewriteActionTab.buttonSaveAndExit.click();
    }

    @Test(groups = "7.2_AUTOP,HOME_UC_RewriteCancelledPersonalPolicy",
            dependsOnMethods = {"testPolicyRewriteToNewNumber", "testPolicyRewriteCancellations"},
            alwaysRun = true)
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRewriteToSameNumber() {
        log.info("TEST: Rewrite Policy #" + policyNumber);
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameNumber"));

        String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Rewritten Policy #" + rewrittenPolicyNumber);

        CustomAssert.enableSoftMode();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);
        PolicySummaryPage.labelPolicyNumber.verify.value(policyNumber);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(2, String.format(
                "Rewritten Quote %s effective %s from Canceled %s", rewrittenPolicyNumber,
                effectiveDate, rewrittenPolicyNumber));
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.status(2, "Finished");

        CustomAssert.assertAll();
        CustomAssert.disableSoftMode();

        log.info("TEST: Check that original Policy is absent");

        SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, policyNumber);

        SearchPage.verifyWarningsExist("Policy not found");

        log.info("TEST: Issue Rewritten Policy #" + rewrittenPolicyNumber);

        SearchPage.search(SearchFor.QUOTE, SearchBy.POLICY_QUOTE, policyNumber);

        policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    @BeforeMethod
    public void findPolicy() {
        if (!NavigationPage.comboBoxListAction.isPresent()) {
            mainApp().reopen();
            MainPage.QuickSearch.search(policyNumber);
        }
    }
}
