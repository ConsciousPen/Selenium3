package aaa.modules.bct.billing_and_payments;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.ComboBox;

public class AcceptPaymentTest extends BackwardCompatibilityBaseTest {

	/**
	 * @author Deloite
	 * @name Realtime Payments - Accept Direct Payment
	 * @scenario
	 * 1.User navigates to the billing page
	 * 2.Accept payment via CC
	 * 3.Min due after payment must be zero
	 * Check:
	 * 1. Payment is posted.
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states =  Constants.States.CA)
	public void BCT_ONL_030_ProcessAcceptPayment(@Optional("") String state) {
		AcceptPaymentActionTab paymentTab = new AcceptPaymentActionTab();
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);
		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();

		billingAccount.acceptPayment().start();
		ComboBox paymentMethod = paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD);
		List<String> values = paymentMethod.getAllValues();
		values.remove("");

		CustomAssertions.assertThat(values.size()).as("There is Credit Card payment method present").isGreaterThan(2);

		values.remove(BillingConstants.AcceptPaymentMethod.CASH);
		values.remove(BillingConstants.AcceptPaymentMethod.CHECK);
		paymentMethod.setValue(values.get(0));

		paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(initialMinDue.toString());
		paymentTab.submitTab();

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setAmount(initialMinDue.negate()).verifyPresent();
		BillingSummaryPage.getMinimumDue().verify.equals(new Dollar(0));
	}
}

