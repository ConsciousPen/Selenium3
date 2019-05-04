package aaa.modules.regression.finance.billing.home_ca.ho3;

import static aaa.main.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static aaa.main.enums.BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static aaa.main.enums.BillingConstants.PaymentPlan.ANNUAL;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Cell;

public class TestFinancePolicyEscheatmentCheckReversals extends FinanceOperations {

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check reverse rules of a systematically created escheatment transaction(s).
	 * Preconditions:
	 * 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 5. Turn time for more than a year of Refund
	 * 6. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * 7. Navigate to BA
	 * TC Steps:
	 * 1. Check new Escheatment transaction exist
	 * 2. Click Reverse action
	 * 3. Click Cancel button - check no new transaction in Payments & Other Transactions table
	 * 4. Click Reverse action
	 * 5. Click Ok button - check:
	 * New transaction created with negative amount
	 * New BAM message created
	 * Balance due and Prepaid amounts recalculated
	 * In original Escheatment transaction status changed from Applied to Reversed
	 * Reversed action not exist
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-18992")
	public void pas18992_testFinancePolicyEscheatmentCheckReversals(@Optional("CA") String state) {
		String policyNumber = createEscheatmentTransaction();

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		Cell escheatmentActions = BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains("Subtype/Reason", "Escheatment").getCell("Action");
		assertThat(escheatmentActions.getValue()).contains("Reverse");

		escheatmentActions.controls.links.get("Reverse").click();
		Page.dialogConfirmation.reject();
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
				.getCell("Amount")).hasValue("$25.00");

		escheatmentActions.controls.links.get("Reverse").click();
		Page.dialogConfirmation.confirm();

		Dollar totalPaid = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(PAYMENT_PLAN, ANNUAL)
				.getCell("Total Paid").getValue());
		Dollar prepaid = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(PAYMENT_PLAN, ANNUAL)
				.getCell("Prepaid").getValue());
		Dollar totalDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(PAYMENT_PLAN, ANNUAL)
				.getCell("Total Due").getValue());
		Dollar billableAmount = new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRowContains(PAYMENT_PLAN, ANNUAL)
				.getCell("Billable Amount").getValue());

		assertSoftly(softly -> {
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
					.getCell(AMOUNT)).hasValue("($25.00)");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2)
					.getCell(STATUS)).hasValue("Reversed");
			softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2)
					.getCell(ACTION).getValue()).doesNotContain("Reverse");
			softly.assertThat(totalDue.toString()).isEqualTo("($25.00)");
			softly.assertThat(prepaid.toString()).isEqualTo("($25.00)");
			softly.assertThat(totalPaid).isEqualTo(billableAmount.subtract(totalDue));

			NotesAndAlertsSummaryPage.activitiesAndUserNotes.expand();
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1)
					.getCell(DESCRIPTION)).valueContains("Reversal");
		});
	}
}
