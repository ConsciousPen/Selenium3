/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.actiontabs.ReinstatementActionTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Reinstatement Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto Policy
 * 3. Cancel Policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * 5. Start reinstatement and cancel it.
 * Verify Policy status is stil 'Policy Cancelled'
 * 6. Reinstate Policy
 * Verify Policy status is 'Policy Active'
 * Verify BOM message appeared
 * Verify Reinstate transaction exist in history
 * 7. Cancel Policy
 * 8. Reinstate policy with lapse
 * Verify Policy status is 'Policy Active'
 * Verify BOM message appeared
 * Verify Reinstate with Lapse transaction exist in history
 * @details
 * https://jira.exigeninsurance.com/browse/EISDEV-146523
 */
public class TestPolicyReinstatement extends AutoCaSelectBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyReinstatement() {
    	mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Cancelling Policy #" + policyNumber);
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        log.info("TEST: Cancel reinstatement Policy #" + policyNumber);
        policy.reinstate().start();
        ReinstatementActionTab.buttonCancel.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        log.info("TEST: Reinstate Policy #" + policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Process Reinstatement effective");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "for Policy " + policyNumber);

        PolicySummaryPage.buttonTransactionHistory.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE).verify.value("Reinstatement");
        Tab.buttonTopCancel.click();

        log.info("Cancelling Policy #" + policyNumber);
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        log.info("TEST: Reinstate Policy With Lapse #" + policyNumber);
        policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus14Days"));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        PolicySummaryPage.labelTermIncludesLapsePeriod.verify.present();

        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Process Reinstatement effective");
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "for Policy " + policyNumber);

        PolicySummaryPage.buttonTransactionHistory.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE).verify.value("Reinstatement with Lapse");
    }
}
