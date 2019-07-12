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
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.webdriver.controls.ComboBox;

public class DefinePaymentMethodTest extends BackwardCompatibilityBaseTest {
	/**
	 * @author Deloite
	 * @name Payment Method – Cash/Check
	 * @scenario
	 * @param state
	 * 1. System displays the following Payment Method to apply Minimum Required Down payment:
	 *     a.Cash
	 *     b.Check
	 * Check:
	 * User can pay the amount using the listed Payment Method
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_031_Define_Payment_Method(@Optional("") String state) {
		AcceptPaymentActionTab paymentTab = new AcceptPaymentActionTab();
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

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
