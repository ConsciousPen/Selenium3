package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class RefundTransferTest extends BackwardCompatibilityBaseTest {
	private BillingAccount billingAccount = new BillingAccount();

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_037_RefundTransfer(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_037_RefundTransfer", SELECT_POLICY_QUERY_TYPE).get(0);

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
	@StateList(states = CA)
	public void BCT_ONL_038_RefundTransfer(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_038_RefundTransfer", SELECT_POLICY_QUERY_TYPE).get(0);

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
