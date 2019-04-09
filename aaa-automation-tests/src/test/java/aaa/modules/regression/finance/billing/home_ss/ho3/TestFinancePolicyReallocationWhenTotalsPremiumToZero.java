package aaa.modules.regression.finance.billing.home_ss.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinancePolicyReallocationWhenTotalsPremiumToZero extends FinanceOperations {

	/**
	 * @author Reda Kazlauskiene
	 * Preconditions:
	 * 1. Create Annual Policy with Fee - example: NJ PLIGA FEE;
	 * 2. Make manual payment with the same amount as deposit payment;
	 * 3. Decline deposit payment;
	 * Verify that Mind due and Prepaid amounts are not equal to zero
	 and Total due aamount is equal to zero.
	 * 4. Run aaaBalancingReallocationJob
	 * 5. Verify that Reallocation move funds from NetPremium to Fee.
	 Min due, Prepaid and Total due is equal to zero.
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-25275")
	public void pas25275_pas26255_testFinancePolicyReallocationWhenTotalsPremiumToZero(@Optional("NJ") String state) {
		Dollar totalPayment;

		String policyNumber = openAppAndCreatePolicy();
		SearchPage.openBilling(policyNumber);

		totalPayment = BillingSummaryPage.getTotalDue().subtract(new Dollar(
				BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()));

		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
				.getTestData("AcceptPayment", "TestData_Cash"), totalPayment);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable
				.SUBTYPE_REASON, "Deposit Payment")
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get("Decline").click();

		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isNotEqualTo(new Dollar(0));
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.PREPAID).getValue())).isNotEqualTo(new Dollar(0));
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue())).isEqualTo(new Dollar(0));

		JobUtils.executeJob(Jobs.aaaBalancingReallocationJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(new Dollar(0));
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.PREPAID).getValue())).isEqualTo(new Dollar(0));
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue())).isEqualTo(new Dollar(0));

		assertThat(new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable
				.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATED_PAYMENT).getCell(
						BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue())).isEqualTo(totalPayment);
		assertThat(new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable
				.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REALLOCATE_PAYMENT).getCell(
						BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue())).isEqualTo(totalPayment.negate());
	}
}
