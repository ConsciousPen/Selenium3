package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.SearchPage;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.webdriver.controls.ComboBox;

import java.util.Arrays;

public class DefinePaymentMethodTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_031_Define_Payment_Method(@Optional("") String state) {

		String policyNumber = getPoliciesByQuery("BCT_ONL_031_PaymentMethod", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();
		AcceptPaymentActionTab paymentTab = new AcceptPaymentActionTab();

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar minDue = BillingSummaryPage.getMinimumDue();
		Dollar totDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().start();

		ComboBox paymentMethod = new AcceptPaymentActionTab().getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD);
		paymentMethod.verify.optionsContain(Arrays.asList(new String[]{BillingConstants.AcceptPaymentMethod.CASH, BillingConstants.AcceptPaymentMethod.CHECK}));

		paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue(BillingConstants.AcceptPaymentMethod.CASH);
		paymentTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT).setValue(minDue.add(100).toString());
		paymentTab.submitTab();

		BillingSummaryPage.getMinimumDue().verify.equals(new Dollar(0));
	}

}