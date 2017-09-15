package aaa.modules.bct.billing_and_payments;

import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.ChangePaymentPlanActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ModifyPaymentPlanTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_086_ModifyPaymentPlan(String state) {
		String policyNumber = getPoliciesByQuery("BCT_ONL_086_ModifyPaymentPlan", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
//		int initialInstallmentsCount = BillingSummaryPage.tableInstallmentSchedule.getRowsCount();

		//TODO Policy with anual PP can be returned by query, change query or set PP that differs from initial
		billingAccount.changePaymentPlan().perform("starts=Annual");

		BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN)
				.verify.valueByRegex("Annual( \\(Renewal\\))?");

//		CustomAssert.assertTrue("Installments count reduced", BillingSummaryPage.tableInstallmentSchedule.getRowsCount() < initialInstallmentsCount);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_185_Refund_Validation(String state) {
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		String policyNumber = getPoliciesByQuery("BCT_ONL_185_Refund_Validation", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billingAccount.changePaymentPlan().perform("Standard Monthly (Renewal)");

		BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN)
				.verify.valueByRegex("Standard Monthly (Renewal)");

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.INSTALLMENT_FEE)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).setAmount(new Dollar(3)).verify(1);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_186_Refund_Validation(String state) {
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		String policyNumber = getPoliciesByQuery("BCT_ONL_186_Refund_Validation", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		billingAccount.changePaymentPlan().start();
		ChangePaymentPlanActionTab tab = new ChangePaymentPlanActionTab();
		tab.getAssetList().getAsset(BillingAccountMetaData.ChangePaymentPlanActionTab.PAYMENT_PLAN).setValue("Standard Monthly (Renewal)");
		tab.buttonOk.click();
		Page.dialogConfirmation.labelMessage.verify.contains("As you requested, we have changed your payment plan from Annual (Renewal) to Standard Monthly (Renewal) and your minimum due has changed. Your policy is set up on automatic payment and the new minimum due will be withdrawn from your account on or after your renewal date. An updated renewal statement will not be available. Do you agree to these changes?");
		Page.dialogConfirmation.confirm();

		BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN)
				.verify.valueByRegex("Standard Monthly (Renewal)");

		new BillingPaymentsAndTransactionsVerifier().setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.INSTALLMENT_FEE)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED).setAmount(new Dollar(3)).verify(1);
	}
}
