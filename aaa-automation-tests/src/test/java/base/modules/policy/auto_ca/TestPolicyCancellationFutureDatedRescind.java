/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ca;

import org.testng.annotations.Test;

import aaa.common.pages.Page;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.auto_ca.actiontabs.RescindCancellationActionTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test Rescind Cancellation Auto Policy in Future Date
 * @scenario 1. Create Customer 2. Create Auto Policy 3. Cancellation Auto
 *           Policy with Rescind Cancellation Date + 3 days 4. Verify Policy
 *           status is "Cancellation Pending" 5 Start Rescind cancellation
 *           policy Fill required field, click ok. Decline confirmation of
 *           Rescind Cancellation action Click Cancel in Rescind Cancellation
 *           workspace 6. Rescind Cancellation policy 7. Verify Policy status is
 *           "Policy Active" Verify BAM Message present Verify Rescind
 *           transaction present in transaction History
 * @details https://jira.exigeninsurance.com/browse/EISDEV-147611 Blueprint ID:
 *          UC2959518
 */
public class TestPolicyCancellationFutureDatedRescind extends AutoCaSelectBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines")
	public void testPolicyCancellationFutureDatedRescind() {
		mainApp().open();

		createCustomerIndividual();

		createPolicy();

		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData_Plus3Days"));

		log.info("Cancellation Pending for Policy #" + policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		policy.rescindCancellation().start();
		RescindCancellationActionTab.buttonOk.click();
		Page.dialogConfirmation.buttonNo.click();

		RescindCancellationActionTab.buttonCancel.click();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

		log.info("TEST: Rescind Cancellation for Policy #" + policyNumber);
		policy.rescindCancellation().perform();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "Process Rescind Cancellation effective");
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionContains(1, "for Policy " + policyNumber);

		PolicySummaryPage.buttonTransactionHistory.click();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TYPE).verify.value("Reinstatement");
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).verify.value("Rescind Cancellation");
	}
}
