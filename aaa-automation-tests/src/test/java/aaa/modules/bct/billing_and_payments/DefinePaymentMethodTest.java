package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.CA;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.pages.SearchPage;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.webdriver.controls.ComboBox;

public class DefinePaymentMethodTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_031_Define_Payment_Method(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery("BCT_ONL_031_PaymentMethod", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();
		AcceptPaymentActionTab paymentTab = new AcceptPaymentActionTab();

		SearchPage.openBilling(policyNumber);
		Dollar minDue = BillingSummaryPage.getMinimumDue();
		billingAccount.acceptPayment().start();

		ComboBox paymentMethod = new AcceptPaymentActionTab().getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD);
		assertThat(paymentMethod).containsAllOptions(BillingConstants.AcceptPaymentMethod.CASH, BillingConstants.AcceptPaymentMethod.CHECK);

		paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue(BillingConstants.AcceptPaymentMethod.CASH);
		paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(minDue.add(100).toString());
		paymentTab.submitTab();

		BillingSummaryPage.getMinimumDue().verify.equals(new Dollar(0));
	}
}
