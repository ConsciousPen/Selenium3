package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

import java.util.List;

public class AcceptPaymentTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_030_ProcessAcceptPayment(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_030_ProcessAcceptPayment", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		SearchPage.openBilling(policyNumber);
		Dollar initialMinDue = BillingSummaryPage.getMinimumDue();

		billingAccount.acceptPayment().start();
		AcceptPaymentActionTab paymentTab = new AcceptPaymentActionTab();
		ComboBox paymentMethod = paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD);
		List<String> values = paymentMethod.getAllValues();
		values.remove("");
		CustomAssert.assertTrue("There is Credit Card payment method present", values.size()>2);
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

