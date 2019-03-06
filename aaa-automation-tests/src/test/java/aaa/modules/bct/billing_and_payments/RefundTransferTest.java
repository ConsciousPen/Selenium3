package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class RefundTransferTest extends BackwardCompatibilityBaseTest {
	/**
	 * @author Deloite
	 * @name Process refund manually
	 * @scenario
	 * @param state
	 * Preconditions:
	 * 1. Retrieve an ACTIVE policy
	 * 2. User from the group C32 is logged in.
	 * Steps:
	 * 1.User retrieves an ACTIVE policy
	 * 2. Billing account and policies with Total Paid amount greater than $500.00 (due to payments by Cash/Check/Credit Card/EFT) has been selected.
	 * 3. User has initiated refund entry manually via Refund action, selected Payment Method = Check, entered amount <=$500.00, entered all other required details and created Refund.
	 * Check:
	 * 1. An ACTIVE policy is retrieved
	 * 2.User makes a payment greater than $500
	 * 3.User initiates the refund
	 * 5. The Refund transaction is displayed in the Payments & Other Transactions section as follows:
	 * - Transaction Date = current date
	 * - Type = Refund, enabled link
	 * - Subtype/reason = reason entered on the payment page
	 * - Amount = amount of refund
	 * - Status = Approved
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_037_RefundTransfer(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		verifyRefundTransfer(policyNumber);
	}
	/**
	 * @author Deloite
	 * @name Manual Refund - billing consolidated view page - >$500 - Approval
	 * @scenario
	 * @param state
	 * Preconditions:
	 * 1. Retrieve an ACTIVE policy
	 * 2. User from the group C32 is logged in.
	 * Steps:
	 * 1.User retrieves an ACTIVE policy
	 * 2. Billing account and policies with Total Paid amount greater than $500.00 (due to payments by Cash/Check/Credit Card/EFT) has been selected.
	 * 3. User has initiated refund entry manually via Refund action, selected Payment Method = Check, entered amount <=$500.00, entered all other required details and created Refund.
	 * 4. User approves the manual refundvia Approve link displayed in Action column next to pending refund transaction.
	 * Check:
	 * 1. ACTIVE policy is retrieved
	 * 2. The refund amountis not reversed, therefore, Total Due and Total Paid amounts remain not changed.
	 * 3. The refund transactions is no longer in Pending Transactions section, it has moved to the Payments & Other Transactions section.
	 * 4. Task for refund approval is not completed automatically.
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_038_RefundTransfer(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		verifyRefundTransfer(policyNumber);
	}

	private void verifyRefundTransfer(String policyNumber) {

		SearchPage.openBilling(policyNumber);
		Dollar refundAmount = new Dollar(500);
		Dollar initialTotalPaid = BillingSummaryPage.getTotalPaid();

		billingAccount.refund().perform(testDataManager.billingAccount.getTestData("Refund", "TestData_Check"), refundAmount);
		billingAccount.approveRefund().perform(refundAmount);

		new BillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setAmount(refundAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent();
		BillingSummaryPage.getTotalPaid().verify.equals(initialTotalPaid.subtract(refundAmount));
	}
}
