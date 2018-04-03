package aaa.modules.bct.billing_and_payments;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;

public class ModifyPaymentPlanTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	public void BCT_ONL_086_ModifyPaymentPlan(@Optional("") String state) {
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_086_ModifyPaymentPlan", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

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
	public void BCT_ONL_185_Refund_Validation(@Optional("") String state) {
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_185_Refund_Validation", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		SearchPage.openBilling(policyNumber);

		billingAccount.changePaymentPlan().perform(BillingConstants.PaymentPlan.STANDARD_MONTHLY_RENEWAL);

		BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN)
				.verify.value(BillingConstants.PaymentPlan.STANDARD_MONTHLY_RENEWAL);
	}

	@Parameters({"state"})
	@Test
	public void BCT_ONL_186_Refund_Validation(@Optional("") String state) {
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		mainApp().open();

		String policyNumber = getPoliciesByQuery("BCT_ONL_186_Refund_Validation", "SelectPolicy").get(0);
		BillingAccount billingAccount = new BillingAccount();

		SearchPage.openBilling(policyNumber);

		billingAccount.changePaymentPlan().perform(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);

		BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN)
				.verify.value(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
	}
}
