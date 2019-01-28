/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.dialog.DialogMultiSelector;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class BillingAccountMetaData {
	public static final class GenerateFutureStatementActionTab extends MetaData {}

	public static final class AcceptPaymentActionTab extends MetaData {
		public static final AssetDescriptor<AddPaymentMethodsMultiAssetList> PAYMENT_METHODS =
				declare("PaymentMethods", AddPaymentMethodsMultiAssetList.class, BillingAccountMetaData.AddPaymentMethodTab.class);
		public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
		public static final AssetDescriptor<ComboBox> INVOICE = declare("Invoice", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
		public static final AssetDescriptor<StaticElement> TRANSACTION_ID = declare("Transaction ID", StaticElement.class);
		public static final AssetDescriptor<TextBox> CHECK_NUMBER = declare("Check Number", TextBox.class);
		public static final AssetDescriptor<TextBox> CHECK_DATE = declare("Check Date", TextBox.class);
		public static final AssetDescriptor<TextBox> PAYEE_NAME = declare("Payee Name", TextBox.class);
		public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
		public static final AssetDescriptor<TextBox> ALLOCATIONS = declare("Allocations", TextBox.class);
		public static final AssetDescriptor<RadioGroup> SUSPEND_REMAINING = declare("Suspend Remaining?", RadioGroup.class);
		public static final AssetDescriptor<RadioButton> ALLOCATE_EXISTING_SUSPENSE = declare("Allocate existing Suspense", RadioButton.class, By.id("paymentForm:createNewPayment_radio:1"));
		public static final AssetDescriptor<RadioButton> UPDATE_LIVES_VOLUME_NO = declare("Update Lives/Volume? NO", RadioButton.class, By.id("paymentForm:updateLivesVolume_radio:1"));
		public static final AssetDescriptor<StaticElement> PAYMENT_METHOD_MESSAGE_TABLE = declare("Payment Method message", StaticElement.class, By.id("paymentForm:refundAmountMessage"));
		public static final AssetDescriptor<StaticElement> PAYMENT_SUBMIT_REFUND = declare("Payment Submit Refund", StaticElement.class, By.id("paymentForm:saveButton_footer"));
		public static final AssetDescriptor<StaticElement> MESSAGE_WHEN_ONLY_PAYMENT_METHOD_CHECK =
				declare("Payment Methods is only check", StaticElement.class, By.id("paymentForm:refundErrorReasonMessage"));
		public static final AssetDescriptor<TextBox> AMOUNT_PREVIOS_TERM = declare("Previous term amount", TextBox.class, By.id("paymentForm:amount_0"));
		public static final AssetDescriptor<TextBox> AMOUNT_RENEWAL_TERM = declare("Renewal term amount", TextBox.class, By.id("paymentForm:amount_1"));

		//Fee specific fields
		public static final AssetDescriptor<ComboBox> TRANSACTION_TYPE = declare("Transaction Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> TRANSACTION_SUBTYPE = declare("Transaction Subtype", ComboBox.class);
		public static final AssetDescriptor<StaticElement> PAYMENT_AMOUNT_ERROR_MESSAGE = declare("Enter too much amount message", StaticElement.class, By.id("paymentForm:paymentAmount_error"));
	}

	public static final class UpdateBillingAccountActionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> AGENCY = declare("Agency", StaticElement.class);
		public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_NAME = declare("Billing Account Name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> BILL_TYPE = declare("Bill Type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_DUE_DAY = declare("Billing Account Due Day", TextBox.class);
		public static final AssetDescriptor<StaticElement> CURRENCY = declare("Currency", StaticElement.class);
		public static final AssetDescriptor<ComboBox> BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> BILLING_CONTACT_PREFIX = declare("Billing Contact Prefix", ComboBox.class);
		public static final AssetDescriptor<TextBox> BILLING_CONTACT_FIRST_NAME = declare("Billing Contact First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> BILLING_CONTACT_MIDDLE_NAME = declare("Billing Contact Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> BILLING_CONTACT_LAST_NAME = declare("Billing Contact Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class, Waiters.AJAX);

		public static final AssetDescriptor<AddPaymentMethodsMultiAssetList> PAYMENT_METHODS =
				declare("PaymentMethods", AddPaymentMethodsMultiAssetList.class, BillingAccountMetaData.AddPaymentMethodTab.class);
		public static final AssetDescriptor<CheckBox> ACTIVATE_AUTOPAY = declare("Activate Autopay", CheckBox.class, Waiters.AJAX(120000));
		public static final AssetDescriptor<ComboBox> AUTOPAY_SELECTION = declare("Autopay Selection", ComboBox.class);
		public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_INDICATOR = declare("Signature on File Indicator", RadioGroup.class);
	}

	public static final class InvoicingCalendarTab extends MetaData {
		public static final AssetDescriptor<ComboBox> BILLING_CALENDAR = declare("Billing Calendar", ComboBox.class);
		public static final AssetDescriptor<TextBox> CALENDAR_NAME = declare("Calendar Name", TextBox.class);
		public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
		public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
		public static final AssetDescriptor<ComboBox> INVOICING_FREQUENCY = declare("Invoicing Frequency", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INVOICING_RULE = declare("Invoicing Rule", ComboBox.class);
		public static final AssetDescriptor<ComboBox> BILLING_PERIOD_OFFSET = declare("Billing Period Offset", ComboBox.class);
		public static final AssetDescriptor<ComboBox> INVOICE_DUE_DAY = declare("Invoice Due Day", ComboBox.class);
		public static final AssetDescriptor<TextBox> GENERATION_DATE_RULE = declare("Generation Date Rule", TextBox.class);
	}

	public static final class DeclinePaymentActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> DECLINE_REASON = declare("Decline Reason", ComboBox.class);
		public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
	}

	public static final class OtherTransactionsActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> TRANSACTION_TYPE = declare("Transaction Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> TRANSACTION_SUBTYPE = declare("Transaction Subtype", ComboBox.class);
		public static final AssetDescriptor<TextBox> ADDITIONAL_INFO = declare("Additional Info", TextBox.class);
		public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
	}

	public static final class RefundActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
		public static final AssetDescriptor<TextBox> CHECK_NUMBER = declare("Check Number", TextBox.class);
		public static final AssetDescriptor<TextBox> CHECK_DATE = declare("Check Date", TextBox.class);
		public static final AssetDescriptor<TextBox> PAYEE_NAME = declare("Payee Name", TextBox.class);
		public static final AssetDescriptor<TextBox> REFERENCE = declare("Reference #", TextBox.class);
		public static final AssetDescriptor<TextBox> AMOUNT = declare("Amount", TextBox.class);
		public static final AssetDescriptor<TextBox> TOTAL_AMOUNT = declare("Total Amount", TextBox.class);
	}

	public static final class TransferPaymentActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> TRANSFER_REASON = declare("Transfer Reason", ComboBox.class);
		public static final AssetDescriptor<DialogMultiSelector> ALLOCATION = declare("Allocation", DialogMultiSelector.class, AllocationMultiSelector.class);
		public static final AssetDescriptor<TextBox> ALLOCATED_AMOUNT = declare("Allocated Amount", TextBox.class);

		public static final class AllocationMultiSelector extends MetaData {
			public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
			public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy #", TextBox.class);
			public static final AssetDescriptor<ComboBox> BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
			public static final AssetDescriptor<TextBox> MORTGAGEE = declare("Mortgagee", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENCY = declare("Agency", TextBox.class);
			public static final AssetDescriptor<TextBox> AGENCY_CODE = declare("Agency Code", TextBox.class);

			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.SLEEP(30000), false,
					By.id("transferPaymentForm:searchAllocationLnkText"));
		}
	}

	public static final class TransferPaymentBenefitsActionTab extends MetaData {
		public static final AssetDescriptor<DialogSingleSelector> BILLING_ACCOUNT = declare("Billing Account", DialogSingleSelector.class, BillingAccountSingleSelector.class, false,
				By.id("searchBillingAccountsForm:billingAccountSearchPopup"));
		public static final AssetDescriptor<TextBox> TRANSFER_AMOUNT = declare("Transfer Amount", TextBox.class);

		public static final class BillingAccountSingleSelector extends MetaData {
			public static final AssetDescriptor<TextBox> BUSINESS_NAME = declare("Business Name", TextBox.class);
			public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
			public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy #", TextBox.class);
			public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
			public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
			public static final AssetDescriptor<TextBox> CASE_NUMBER = declare("Case #", TextBox.class);

			public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.SLEEP(30000), false,
					By.id("paymentForm:showBillingAccountSearchPopupLnk"));
		}
	}

	public static final class MovePoliciesActionTab extends MetaData {
		public static final AssetDescriptor<CheckBox> POLICIES = declare("Policy", CheckBox.class);
		public static final AssetDescriptor<CheckBox> CREATE_NEW_ACCOUNT = declare("Create New Account", CheckBox.class);
		public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_NAME = declare("Billing Account Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> BILL_TYPE = declare("Bill Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> BILLING_ACCOUNT_DUE_DAY = declare("Billing Account Due Day", TextBox.class);
		public static final AssetDescriptor<ComboBox> BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> BILLING_CONTACT_PREFIX = declare("Billing Contact Prefix", ComboBox.class);
		public static final AssetDescriptor<TextBox> BILLING_CONTACT_FIRST_NAME = declare("Billing Contact First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> BILLING_CONTACT_MIDDLE_NAME = declare("Billing Contact Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> BILLING_CONTACT_LAST_NAME = declare("Billing Contact Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AssetDescriptor<TextBox> PHONE = declare("Phone #", TextBox.class);
		public static final AssetDescriptor<ComboBox> INVOICING_CALENDAR = declare("Invoicing Calendar", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
	}

	public static final class WaiveFeeActionTab extends MetaData {

	}

	public static final class AddHoldActionTab extends MetaData {
		public static final AssetDescriptor<FillableTable> POLICIES_LIST = declare("Policies List", FillableTable.class, PoliciesListRow.class, By.id("holdPoliciesForm:policyResults"));
		public static final AssetDescriptor<TextBox> HOLD_NAME = declare("Hold Name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> HOLD_DESCRIPTION = declare("Hold Description", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REASON = declare("Reason", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDITIONAL_INFO = declare("Additional Info", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ListBox> HOLD_TYPE = declare("Hold Type", ListBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> HOLD_EFFECTIVE_DATE = declare("Effective Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> HOLD_EXPIRATION_DATE = declare("Expiration Date", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<FillableTable> HOLDS_TABLE = declare("Billing Holds and Moratoriums", FillableTable.class, HoldsTableRow.class, By.id("holdPoliciesForm:holdsTable"));

		public static final class PoliciesListRow extends MetaData {
			public static final AssetDescriptor<CheckBox> COLUMN_1 = declare("column=1", CheckBox.class);
			public static final AssetDescriptor<StaticElement> POLICY_NUM = declare("Policy #", StaticElement.class);
			public static final AssetDescriptor<StaticElement> TYPE = declare("Type", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EFF_DATE = declare("Eff. Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> PAYMENT_PLAN = declare("Payment Plan", StaticElement.class);
			public static final AssetDescriptor<StaticElement> POLICY_STATUS = declare("Policy Status", StaticElement.class);
			public static final AssetDescriptor<StaticElement> BILLING_STATUS = declare("Billing Status", StaticElement.class);
			public static final AssetDescriptor<StaticElement> TOTAL_DUE = declare("Total Due", StaticElement.class);
			public static final AssetDescriptor<StaticElement> TOTAL_PAID = declare("Total Paid", StaticElement.class);
		}

		public static final class HoldsTableRow extends MetaData {
			public static final AssetDescriptor<StaticElement> HOLD_NAME = declare("Hold Name", StaticElement.class);
			public static final AssetDescriptor<StaticElement> HOLD_DESCRIPTION = declare("Hold Description", StaticElement.class);
			public static final AssetDescriptor<StaticElement> REASON = declare("Reason", StaticElement.class);
			public static final AssetDescriptor<StaticElement> HOLD_TYPE = declare("Hold Type", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EFFECTIVE_DATE = declare("Effective Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
			public static final AssetDescriptor<StaticElement> HOLD_CATEGORY = declare("Hold Category", StaticElement.class);
			public static final AssetDescriptor<Link> ACTIONS = declare("Actions", Link.class);
			public static final AssetDescriptor<Button> CONFIRM_REMOVE_HOLD = declare("Confirm remove hold", Button.class, Waiters.AJAX, false, By.id("confirmationForm:okBtn"));
		}
	}

	public static final class AddPaymentMethodTab extends MetaData {
		public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AssetDescriptor<TextBox> ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AssetDescriptor<TextBox> CITY = declare("City", TextBox.class);
		public static final AssetDescriptor<ComboBox> STATE_PROVINCE = declare("State / Province", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> NUMBER = declare("Number", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> CARD_HOLDER_NAME = declare("Card Holder Name", TextBox.class);
		public static final AssetDescriptor<DoubleComboBox> EXPIRATION_MONTH_YEAR = declare("Card Expiration Month/Year", DoubleComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> TRANSIT = declare("Transit #", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> BANK_NAME = declare("Bank Name", TextBox.class);
		public static final AssetDescriptor<TextBox> ACCOUNT = declare("Account #", TextBox.class);
		public static final AssetDescriptor<ComboBox> ACCOUNT_TYPE = declare("Account Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> PREFIX = declare("Prefix", ComboBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
	}

	public static final class AdvancedAllocationsActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> TOTAL_AMOUNT = declare("Total Amount", TextBox.class);
		public static final AssetDescriptor<TextBox> PRODUCT_SUB_TOTAL = declare("Total Amount", TextBox.class);
		public static final AssetDescriptor<TextBox> OTHER = declare("Other", TextBox.class);
		public static final AssetDescriptor<TextBox> NET_PREMIUM = declare("Net Premium", TextBox.class);
		public static final AssetDescriptor<TextBox> POLICY_FEE = declare("Policy Fee", TextBox.class);
	}

	public static final class ChangePaymentPlanActionTab extends MetaData {
		public static final AssetDescriptor<StaticElement> PRODUCT_NUM = declare("Product #", StaticElement.class);
		public static final AssetDescriptor<ComboBox> PAYMENT_PLAN = declare("Payment Plan:", ComboBox.class);
	}
}
