package aaa.modules.financials;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class FinancialsBaseTest extends FinancialsTestDataFactory {

	protected static final String METHOD_CASH = "TestData_Cash";
	protected static final String METHOD_CHECK = "TestData_Check";

	private static final Object lock = new Object();

	protected String createFinancialPolicy() {
		return createFinancialPolicy(getPolicyTD());
	}

	protected String createFinancialPolicy(TestData td) {
		String policyNum = createPolicy(td);
		ALL_POLICIES.add(policyNum);
		return policyNum;
	}

	protected Dollar payTotalAmountDue(){
		// Open Billing account and Pay min due for the renewal
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Dollar due = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);
		return due;
	}
	protected Dollar payMinAmountDue(String paymentMethod) {
		// Open Billing account and Pay min due for the renewal
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Dollar due = new Dollar(BillingSummaryPage.getMinimumDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", paymentMethod), due);
		return due;
	}

	protected void cancelPolicy(String policyNumber) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		cancelPolicy(TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void cancelPolicy(LocalDateTime cxDate) {
		policy.cancel().perform(getCancellationTD(cxDate));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	protected void performReinstatement(String policyNumber) {
		if (!PolicySummaryPage.labelPolicyStatus.isPresent()) {
			SearchPage.openPolicy(policyNumber);
		}
		policy.reinstate().perform(getReinstatementTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void performReinstatementWithLapse(LocalDateTime effDate, String policyNumber) {
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
		JobUtils.executeJob(BatchJob.changeCancellationPendingPoliciesStatusJob);
		TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(20));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getReinstatementTD());
		if (Page.dialogConfirmation.buttonYes.isPresent()) {
			Page.dialogConfirmation.buttonYes.click();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void performAPEndorsement(String policyNumber) {
		performAPEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void performAPEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getAddPremiumTD());
		SearchPage.openPolicy(policyNumber);
	}

	protected Dollar performRPEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getReducePremiumTD());
		Dollar reducedPrem = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ENDORSEMENT);
		SearchPage.openPolicy(policyNumber);
		return reducedPrem;
	}

	protected void performNonPremBearingEndorsement(String policyNumber) {
		performNonPremBearingEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void performNonPremBearingEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getNPBEndorsementTD());
		SearchPage.openPolicy(policyNumber);
	}

	protected Dollar rollBackEndorsement(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		policy.rollBackEndorsement().perform(getPolicyTD("EndorsementRollBack", "TestData"));
		Dollar rollBackAmount = getBillingAmountByType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ROLL_BACK_ENDORSEMENT);
		SearchPage.openPolicy(policyNumber);
		return rollBackAmount;
	}

	protected Dollar getBillingAmountByType(String type, String subtype) {
		if (!BillingSummaryPage.tablePaymentsOtherTransactions.isPresent()) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, type);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, subtype);
		return new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()).abs();
	}

	protected void waiveFeeByDateAndType(LocalDateTime txDate, String feeType) {
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, txDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.FEE);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, feeType);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(query)
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(BillingConstants.PaymentsAndOtherTransactionAction.WAIVE).click();
		BillingSummaryPage.dialogConfirmation.confirm();
	}

	protected void advanceTimeAndOpenPolicy(LocalDateTime date, String policyNumber) {
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(date);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
	}

	protected void runLedgerStatusUpdateJob() {
		JobUtils.executeJob(BatchJob.ledgerStatusUpdateJob);
	}

}
