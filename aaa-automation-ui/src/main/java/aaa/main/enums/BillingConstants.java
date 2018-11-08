/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.enums;

public final class BillingConstants {

	private BillingConstants() {
	}

	public static final class PolicyFlag {
		public static final String CANCEL_NOTICE = "Cancel Notice Flag"; //"Cancel Notice";
		public static final String LAPSE_EXIST = "Term includes a lapse period";
		public static final String DO_NOT_RENEW = "Do Not Renew";
		public static final String DEFAULT = "";
	}

	public static final class PaymentPlan {
		public static final String PAY_IN_FULL = "Pay in Full";
		public static final String SEMI_ANNUAL = "Semi-Annual";
		public static final String SEMI_ANNUAL_RENEWAL = "Semi-Annual (Renewal)";
		public static final String QUARTERLY = "Quarterly";
		public static final String QUARTERLY_RENEWAL = "Quarterly (Renewal)";
		public static final String MONTHLY_STANDARD = "Monthly Standard";
		public static final String MONTHLY_LOW_DOWN = "Monthly Low Down";
		public static final String MONTHLY_ZERO_DOWN = "Monthly Zero Down";
		public static final String MORTGAGEE_BILL = "Mortgagee Bill";
		public static final String MORTGAGEE_BILL_RENEWAL = "Mortgagee Bill (Renewal)";
		public static final String ELEVEN_PAY = "Eleven Pay Standard";
		public static final String AUTO_ELEVEN_PAY = "Eleven Pay - Standard";
		public static final String ELEVEN_PAY_RENEWAL = "Eleven Pay Standard (Renewal)";
		public static final String ANNUAL = "Annual";
		public static final String MONTHLY = "Monthly";
		public static final String MONTHLY_RENEWAL = "Monthly (Renewal)";
		public static final String MONTHLY_STANDARD_RENEWAL = "Monthly Standard (Renewal)"; //HO
		public static final String STANDARD_MONTHLY_RENEWAL = "Standard Monthly (Renewal)"; //AU
		public static  final String ELEVEN_PAY_LOW_DOWN = "Eleven Pay Low Down";
	}

	public static final class PolicyTerm {
		public static final String ANNUAL = "Annual";
		public static final String SEMI_ANNUAL = "Semi-annual";
	}

	public static final class BillingAccountPoliciesBillingStatus {
		public static final String ACTIVE = "Active";
		public static final String HOLD = "On Hold";
	}

	public static final class BillingAccountPoliciesPolicyStatus {
		public static final String CUSTOMER_DECLINED = "Customer Declined";
		public static final String POLICY_CANCELLED = "Policy Cancelled";
	}

	public static final class InstallmentScheduleBilledStatus {
		public static final String BILLED = "Billed";
		public static final String UNBILLED = "Unbilled";
	}

	public static final class InstallmentDescription {
		public static final String DEPOSIT = "Deposit";
		public static final String INSTALLMENT = "Installment";
	}

	public static final class BillsAndStatementsType {
		public static final String BILL = "Bill";
		public static final String DISCARDED_BILL = "Discarded Bill";
		public static final String DISCARDED_CANCELLATION_NOTICE = "Discarded Cancellation Notice";
		public static final String CANCELLATION_NOTICE = "Cancellation Notice";
		public static final String CANCELLATION = "Cancellation";
		public static final String OFFER = "Offer";
		public static final String DISCARDED_OFFER = "Discarded Offer";
	}

	public static final class PaymentsAndOtherTransactionType {
		public static final String PAYMENT = "Payment";
		public static final String PREMIUM = "Premium";
		public static final String ADJUSTMENT = "Adjustment";
		public static final String FEE = "Fee";
		public static final String REFUND = "Refund";
	}

	public static final class BillingPendingTransactionsType {
		public static final String REFUND = "Refund";
	}

	public static final class BillingPendingTransactionsSubtype {
		public static final String AUTOMATED_REFUND = "Automated Refund";
	}

	public static final class BillingPendingTransactionsStatus {
		public static final String PENDING = "Pending";
	}

	public static final class PaymentsAndOtherTransactionSubtypeReason {
		public static final String POLICY = "Policy";
		public static final String RENEWAL = "Renewal";
		public static final String POLICY_MOVED = "Policy Moved";
		public static final String MANUAL_PAYMENT = "Manual Payment";
		public static final String BULK_PAYMENT = "Bulk Payment";
		public static final String MANUAL_REFUND = "Manual Refund";
		public static final String AUTOMATED_REFUND = "Automated Refund";
		public static final String INSTALLMENT_FEE = "Installment Fee";
		public static final String ENDORSEMENT = "Endorsement";
		public static final String NOT_SUFFICIENT_FUNDS_FEE = "Not Sufficient Funds Fee";
		public static final String CANCELLATION_NOTICE_FEE = "Cancellation Notice Fee";
		public static final String CANCELLATION_FEE = "Cancellation Fee";
		public static final String CANCELLATION_FEE_WAIVED = "Cancellation Fee Waived";
		public static final String CANCELLATION_NOTICE = "Cancellation Notice";
		public static final String CANCELLATION = "Cancellation";
		public static final String CANCELLATION_NON_PAYMENT_OF_PREMIUM = "Cancellation - Non Payment of Premium";
		public static final String CANCELLATION_INSURED_NON_PAYMENT_OF_PREMIUM = "Cancellation - Insured Non-Payment Of Premium";
		public static final String CANCELLATION_INSURED_REQUEST_DUE_TO_HIGH_PREMIUM = "Cancellation - Insured Request - Due To High Premium";
		public static final String PAYMENT_DECLINED = "Payment Declined";
		public static final String PAYMENT_DISAPPROVED = "Payment Disapproved";
		public static final String WRITE_OFF = "Write-off";
		public static final String SERVICE_FEE = "Service Fee";
		public static final String SERVICE_FEE_WAIVED = "Service Fee Waived";
		public static final String INSTALLMENT_FEE_WAIVED = "Installment Fee Waived";
		public static final String EARNED_PREMIUM_WRITE_OFF = "Earned Premium Write-off";
		public static final String EARNED_PREMIUM_WRITE_OFF_REVERSED = "Earned Premium Write-off Reversed";
		public static final String PAYMENT_TRANSFERRED = "Payment Transferred";
		public static final String REALLOCATED_PAYMENT = "Reallocated Payment";
		public static final String REALLOCATE_PAYMENT = "Reallocate Payment";
		public static final String RENEWAL_POLICY_RENEWAL_PROPOSAL = "Renewal - Policy Renewal Proposal";
		public static final String RENEWAL_POLICY_RENEWAL_PROPOSAL_REVERSAL = "Renewal - Policy Renewal Proposal Reversal";
		public static final String REINSTATEMENT = "Reinstatement";
		public static final String REINSTATEMENT_FEE = "Reinstatement Fee";
		public static final String REINSTATEMENT_FEE_RENEWAL = "Reinstatement Fee - Renewal";
		public static final String REINSTATEMENT_FEE_WITH_LAPSE = "Reinstatement with Lapse";
		public static final String REINSTATEMENT_WITHOUT_LAPSE = "Reinstatement - Without Lapse: payment received within grace period";
		public static final String REINSTATEMENT_WITH_LAPSE = "Reinstatement - Lapse: payment received within the lapse period";
		public static final String REINSTATEMENT_RESCIND_CANCELLATION = "Reinstatement - Rescind Cancellation";
		public static final String CROSS_POLICY_TRANSFER = "Cross Policy Transfer";
		public static final String OFFER_WITHOUT_LAPSE = "Offer without Lapse";
		public static final String SUSPENSE = "Suspense";
		public static final String NSF_FEE__WITH_RESTRICTION = "NSF fee - with restriction";
		public static final String NSF_FEE__WITHOUT_RESTRICTION = "NSF fee - without restriction";
		public static final String DEPOSIT_PAYMENT = "Deposit Payment";
		public static final String PLIGA_FEE = "PLIGA Fee";
		public static final String MVLE_FEE = "MVLE Fee";
		public static final String RECURRING_PAYMENT = "Recurring Payment";
		public static final String REGULUS_LOCKBOX = "Regulus LockBox";
		public static final String REGULUS_ONLINE = "Regulus Online";
		public static final String NON_EFT_INSTALLMENT_FEE = "Non EFT Installment Fee";
		public static final String NON_EFT_INSTALLMENT_FEE_WAIVED = "Non EFT Installment Fee Waived";
		public static final String SEISMIC_SAFETY_FEE = "Seismic Safety Fee";
		public static final String OTHER = "Other";
		public static final String REFUND_PAYMENT_VOIDED = "Refund Payment Voided";
		public static final String ESCHEATMENT = "Escheatment";
		public static final String SMALL_BALANCE_WRITE_OFF = "Small Balance Write-off";
		public static final String ENDORSEMENT_PROPERTY_EXPOSURES = "Endorsement - Property/exposures";
		public static final String ENDORSEMENT_MAINTAIN_VECHICLES = "Endorsement - Maintain Vehicle(s)";
	}

	public static final class PaymentsAndOtherTransactionReason {
		public static final String COUNTERFEIT = "Counterfeit";
		public static final String FEE_NO_RESTRICTION = "Fee + No Restriction";
		public static final String FEE_PLUS_RESTRICTION = "Fee + Restriction";
		public static final String NO_FEE_NO_RESTRICTION = "No Fee + No Restriction";
		public static final String NO_FEE_NO_RESTRICTION_NO_LETTER = "No Fee + No Restriction + No Letter";
		public static final String OVERPAYMENT = "Overpayment";
	}

	public static final class BillingPendingTransactionsReason {
		public static final String OVERPAYMENT = "Overpayment";
	}

	public static final class PaymentsAndOtherTransactionStatus {
		public static final String APPLIED = "Applied";
		public static final String APPROVED = "Approved";
		public static final String CLEARED = "Cleared";
		public static final String TRANSFERRED = "Transferred";
		public static final String DECLINED = "Declined";
		public static final String ISSUED = "Issued";
		public static final String PENDING = "Pending";
		public static final String VOIDED = "Voided";
	}
	public static final class PaymentsAndOtherTransactionAction {
		public static final String DECLINE = "Decline";
		public static final String TRANSFER = "Transfer";
		public static final String WAIVE = "Waive";
	}

	public static final class AcceptPaymentMethod {
		public static final String CASH = "Cash";
		public static final String CHECK = "Check";
		public static final String VISA = "Visa";
	}

	public static final class RefundPaymentMethod {
		public static final String CASH = "Cash";
		public static final String CHECK = "Check";
		public static final String VISA = "Visa";
	}

	public static final class ModalPremiumTransactionType {
		public static final String ADD_PREMIUM_WAIVER = "Add Premium Waiver";
		public static final String ENDORSEMENT = "Endorsement (Premium Bearing / TBD Endorsement Reason 1)";
		public static final String RENEWAL = "Renewal (Annual / - )";
		public static final String REMOVE_PREMIUM_WAIVER = "Remove Premium Waiver";
		public static final String CANCELLATION = "Cancellation ( - / Insured Request - Due To High Premium)";
		public static final String CANCELLATION_NON_PAYMENT = "Cancellation ( - / Non Payment of Premium)";
		public static final String REINSTATEMENT = "Reinstatement ( - / Without Lapse: payment received within grace period)";
		public static final String REINSTATEMENT_OTHER = "Reinstatement ( - / Other)";
	}

	public static final class BillingPendingTransactionsActions {
		public static final String APPROVE = "Approve";
	}

	public static final class BillingStatus {
		public static final String ON_HOLD = "On Hold";
		public static final String ACTIVE = "Active";
	}

	public static final class HoldsAndMoratoriumsActions {
		public static final String EDIT = "Edit";
		public static final String REMOVE = "Remove";
	}

	public static final class ExternalPaymentSystem {
		public static final String REGLKBX = "REGLKBX";
		public static final String REGONLN = "REGONLN";
		public static final String BLLMAT = "BLLMAT";
	}

	// ----TABLES----

	public static final class BillingGeneralInformationTable {
		public static final String ID = "ID";
		public static final String BILLING_ADDRESS = "Billing Address";
		public static final String BILL_TYPE = "Bill Type";
		public static final String DUE_DATE = "Due Day";
		public static final String MINIMUM_DUE = "Minimum Due";
		public static final String PAST_DUE = "Past Due";
		public static final String TOTAL_DUE = "Total Due";
		public static final String TOTAL_PAID = "Total Paid";
		public static final String BILLABLE_AMOUNT = "Billable Amount";
	}

	public static final class BillingBillsAndStatmentsTable {
		public static final String DUE_DATE = "Due Date";
		public static final String TYPE = "Type";
		public static final String STATEMENT_NUM = "Statement #";
		public static final String MINIMUM_DUE = "Minimum Due";
		public static final String PAST_DUE = "Past Due";
		public static final String TOTAL_DUE = "Total Due";
		public static final String ACTIONS = "Actions";
	}

	public static final class BillingPaymentsAndOtherTransactionsTable {
		public static final String TYPE = "Type";
		public static final String ACTION = "Action";
		public static final String AMOUNT = "Amount";
		public static final String SUBTYPE_REASON = "Subtype/Reason";
		public static final String REASON = "Reason";
		public static final String POLICY = "Policy #";
		public static final String STATUS = "Status";
		public static final String TRANSACTION_DATE = "Transaction Date";
		public static final String EFF_DATE = "Effective Date";
		public static final String AGENCY_RE_SWEEP = "Agency Re-Sweep";
	}

	public static final class BillingInstallmentScheduleTable {
		public static final String DESCRIPTION = "Description";
		public static final String SCHEDULE_DUE_AMOUNT = "Schedule Due Amount";
		public static final String INSTALLMENT_DUE_DATE = "Schedule Due Date";
		public static final String BILLED_STATUS = "Billed Status";
		public static final String BILL_GENERATION_DATE = "Bill Generation Date";
		public static final String BILL_DUE_DATE = "Bill Due Date";
		public static final String BILLED_AMOUNT = "Billed Amount";
	}

	public static final class BillingPendingTransactionsTable {
		public static final String TRANSACTION_DATE = "Transaction Date";
		public static final String EFFECTIVE_DATE = "Effective Date";
		public static final String TYPE = "Type";
		public static final String SUBTYPE = "Subtype";
		public static final String REASON = "Reason";
		public static final String AMOUNT = "Amount";
		public static final String STATUS = "Status";
		public static final String ACTION = "Action";
	}

	public static final class BillingAccountPoliciesTable {
		public static final String POLICY_NUM = "Policy #";
		public static final String TYPE = "Type";
		public static final String EFF_DATE = "Eff. Date";
		public static final String PAYMENT_PLAN = "Payment Plan";
		public static final String POLICY_STATUS = "Policy Status";
		public static final String POLICY_FLAG = "Policy Flag";
		public static final String BILLING_STATUS = "Billing Status";
		public static final String MIN_DUE = "Min. Due";
		public static final String PAST_DUE = "Past Due";
		public static final String TOTAL_DUE = "Total Due";
		public static final String TOTAL_PAID = "Total Paid";
		public static final String PREPAID = "Prepaid";
		public static final String BILLABLE_AMOUNT = "Billable Amount";
		public static final String PAID_THROUGH = "Paid Through";
	}

	public static final class BillingPaymentAllocationTable {
		public static final String COVERAGE = "Coverage";
		public static final String REMAINING_DUE = "Remaining Due";
		public static final String AMOUNT_PAID = "Amount Paid";
	}

	public static final class BillingModalPremiumTable {
		public static final String MODAL_PREMIUM_EFFECTIVE_DATE = "Modal Premium Effective Date";
		public static final String AMOUNT = "Amount";
		public static final String TRANSACTION_TYPE_SUBTYPE_REASON = "Transaction Type (Subtype / Reason)";
		public static final String COVERAGE = "Coverage";
	}

	public static final class BillingAccountsTable {
		public static final String BILLING_ACCOUNT = "Billing Account #";
	}

	public static final class BillingBenefitAccountsTable {
		public static final String BILLING_ACCOUNT = "Billing Account #";
	}

	public static final class BillingSuspensePaymentsTable {
		public static final String BILLING_ACCOUNTS = "Billing Accounts";
		public static final String STATUS = "Status";
	}

	public static final class BillingSuspenseForDeclineTable {
		public static final String REFERENCE_NUMBER = "Reference Number";
		public static final String ACTION = "Action";
	}

	public static final class BillingSuspenseSearchResultsTable {
		public static final String ACTION = "Missing [action]";
	}

	public static final class BillingAddOnHoldPoliciesTable {
		public static final String POLICY_NUM = "Policy #";
		public static final String TYPE = "Type";
		public static final String EFF_DATE = "Eff. Date";
		public static final String PAYMENT_PLAN = "Payment Plan";
		public static final String POLICY_STATUS = "Policy Status";
		public static final String BILLING_STATUS = "Billing Status";
		public static final String TOTAL_DUE = "Total Due";
		public static final String TOTAL_PAID = "Total Paid";
	}

	public static final class BillingHoldsAndMoratoriumsTable {
		public static final String ACTIONS = "Actions";
	}
}
