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

public class FeesTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_061_ManageFees(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_061_ManageFees", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

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
