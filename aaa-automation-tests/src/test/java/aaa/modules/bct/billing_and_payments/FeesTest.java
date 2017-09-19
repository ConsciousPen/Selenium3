package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class FeesTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_061_ManageFees(@Optional("") String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_061_ManageFees", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		mainApp().open();
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
