package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class RefundTransferTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_037_RefundTransfer(String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_037_RefundTransfer", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar refundAmount = new Dollar(500);
		Dollar initialTotalPaid = BillingSummaryPage.getTotalPaid();

		billingAccount.refund().perform(testDataManager.billingAccount.getTestData("Refund", "TestData_Check"), refundAmount);

		billingAccount.approveRefund().perform(refundAmount);

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setAmount(refundAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent();
		BillingSummaryPage.getTotalPaid().verify.equals(initialTotalPaid.subtract(refundAmount));
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_038_RefundTransfer(String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_038_RefundTransfer", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar refundAmount = new Dollar(500);
		Dollar initialTotalPaid = BillingSummaryPage.getTotalPaid();

		billingAccount.refund().perform(testDataManager.billingAccount.getTestData("Refund", "TestData_Check"), refundAmount);

		billingAccount.approveRefund().perform(refundAmount);

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setAmount(refundAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent();
		BillingSummaryPage.getTotalPaid().verify.equals(initialTotalPaid.subtract(refundAmount));
	}
}
