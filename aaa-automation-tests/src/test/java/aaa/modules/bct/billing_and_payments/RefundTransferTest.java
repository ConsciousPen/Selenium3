package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class RefundTransferTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_037_RefundTransfer(@Optional("") String state) {
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_037_RefundTransfer", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

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
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_038_RefundTransfer", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

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
