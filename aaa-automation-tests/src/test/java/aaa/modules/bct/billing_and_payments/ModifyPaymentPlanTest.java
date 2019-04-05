package aaa.modules.bct.billing_and_payments;

import static aaa.common.enums.Constants.States.CA;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class ModifyPaymentPlanTest extends BackwardCompatibilityBaseTest {
	/**
	 * @author Deloite
	 * @name Modify Payment Plan
	 * @scenario
	 * @param state
	 * 1.On the billing screen User changes payment plan (e.g : from lower number of installments to a higher number of installments) by clicking on the payment plan given on the billing page as hyperlink and updating the payment plan to Monthly pay plan policy).
	 * 2.System immediately determines the 'Total Amount Due' and the ‘Number of future Installments’ according to the new payment plan as User clicks "OK"  - No error is observed.
	 * 3.System displays the Billing Screen with the updated Payment plan in the Billing Account Policies and the new Installment Schedule.
	 * 4.Initiate a mid term cancellation(System determines Cancellation effective date is GREATER THAN the Policy effective date.) and validate that :-
	 *     Seismic fee is not reversed for the policy (no entry is displayed for the policy on Billing page with the following details( in Payments and Other Transactions section) : -
	 *     Type = "Fee"
	 *     Subtype = "Seismic Safety Fee"
	 *
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_086_ModifyPaymentPlan(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verify(1);
//		int initialInstallmentsCount = BillingSummaryPage.tableInstallmentSchedule.getRowsCount();

		//TODO Policy with anual PP can be returned by query, change query or set PP that differs from initial
		billingAccount.changePaymentPlan().perform("starts=Annual");

		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN)).valueMatches("Annual( \\(Renewal\\))?");

//		CustomAssert.assertTrue("Installments count reduced", BillingSummaryPage.tableInstallmentSchedule.getRowsCount() < initialInstallmentsCount);
	}
	/**
	 * @author Deloite
	 * @name Modify Payment Plan
	 * @scenario
	 * @param state
	 * 1.On the billing screen User changes payment plan (e.g : from lower number of installments to a higher number of installments) by clicking on the payment plan given on the billing page as hyperlink and updating the payment plan to Monthly pay plan policy).
	 * 2.System immediately determines the 'Total Amount Due' and the ‘Number of future Installments’ according to the new payment plan as User clicks "OK"  - No error is observed.
	 * 3.System displays the Billing Screen with the updated Payment plan in the Billing Account Policies and the new Installment Schedule.
	 * 4.Initiate a mid term cancellation(System determines Cancellation effective date is GREATER THAN the Policy effective date.) and validate that :-
	 *     Seismic fee is not reversed for the policy (no entry is displayed for the policy on Billing page with the following details( in Payments and Other Transactions section) : -
	 *     Type = "Fee"
	 *     Subtype = "Seismic Safety Fee"
	 *
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_185_Refund_Validation(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		SearchPage.openBilling(policyNumber);

		billingAccount.changePaymentPlan().perform(BillingConstants.PaymentPlan.STANDARD_MONTHLY_RENEWAL);

		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN))
				.hasValue(BillingConstants.PaymentPlan.STANDARD_MONTHLY_RENEWAL);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = CA)
	public void BCT_ONL_186_Refund_Validation(@Optional("") String state) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);
		//TODO Test moved from Deloite's code as is, probably some additional steps should be added
		SearchPage.openBilling(policyNumber);

		billingAccount.changePaymentPlan().perform(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);

		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAYMENT_PLAN))
				.hasValue(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
	}
}
