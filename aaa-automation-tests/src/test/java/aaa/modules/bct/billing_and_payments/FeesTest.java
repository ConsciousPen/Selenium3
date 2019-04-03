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

public class FeesTest extends BackwardCompatibilityBaseTest {
	/**
	 * @author Deloite
	 * @name Addition of fees
	 * @scenario
	 * @param state
	 * 1. User navigates to the Billing Tab
	 * 2. User clicks on “Other transactions”
	 * 3. System displays the following fields
	 * a. Transaction Type
	 * b. Transaction Sub-Type
	 * c. Amount
	 * 4. User selects “Fee” from the Transaction Type field
	 * 5. User selects the appropriate Transaction Sub-Type
	 * 6. User enters the Amount GREATER than $0
	 * 7. User clicks ‘OK’
	 * Check:
	 * 1. Following transaction is created and displayed in the Payments & Other Transactions section
	 *     Transaction 1
	 *     Type = Fee
	 *     Subtype = 'Subtype' of the transaction selected
	 *     Amount= Value entered by the user
	 *     Status = Applied
	 * 2. Total Amount due is increased by the
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_061_ManageFees(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_061_ManageFees", SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);

		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();
		Dollar initialTotalDue = BillingSummaryPage.getTotalDue();
		Dollar feeAmount = new Dollar(10);

		billingAccount.otherTransactions().perform(getTestSpecificTD("OtherTransaction_061"), feeAmount);

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT_FEE)
				.setAmount(feeAmount).setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();

		BillingSummaryPage.getMinimumDue().verify.equals(initialMinDue);
		BillingSummaryPage.getTotalDue().verify.equals(initialTotalDue.add(feeAmount));
	}
}
