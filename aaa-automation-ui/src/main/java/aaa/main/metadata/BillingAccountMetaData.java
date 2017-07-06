/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import org.openqa.selenium.By;
import com.exigen.ipb.etcsa.controls.dialog.DialogMultiSelector;
import com.exigen.ipb.etcsa.controls.dialog.DialogSingleSelector;
import com.exigen.ipb.etcsa.controls.dialog.type.AbstractDialog;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.PurchaseMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.DoubleComboBox;
import toolkit.webdriver.controls.RadioButton;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class BillingAccountMetaData {
	public static final class GenerateFutureStatementActionTab extends MetaData {
	}

	public static final class AcceptPaymentActionTab extends MetaData {
		public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
		public static final AttributeDescriptor INVOICE = declare("Invoice", ComboBox.class);
		public static final AttributeDescriptor PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
		public static final AttributeDescriptor CHECK_NUMBER = declare("Check Number", TextBox.class);
		public static final AttributeDescriptor CHECK_DATE = declare("Check Date", TextBox.class);
		public static final AttributeDescriptor PAYEE_NAME = declare("Payee Name", TextBox.class);
		public static final AttributeDescriptor REFERENCE = declare("Reference #", TextBox.class);
		public static final AttributeDescriptor ALLOCATIONS = declare("Allocations", TextBox.class);
		public static final AttributeDescriptor SUSPEND_REMAINING = declare("Suspend Remaining?", RadioGroup.class);
		public static final AttributeDescriptor ALLOCATE_EXISTING_SUSPENSE = declare("Allocate existing Suspense", RadioButton.class, By.id("paymentForm:createNewPayment_radio:1"));
		public static final AttributeDescriptor UPDATE_LIVES_VOLUME_NO = declare("Update Lives/Volume? NO", RadioButton.class, By.id("paymentForm:updateLivesVolume_radio:1"));
	}

	public static final class BillingAccountTab extends MetaData {
		public static final AttributeDescriptor SELECT_ACTION = declare("Select Action", RadioGroup.class);
		public static final AttributeDescriptor CREATE_NEW_ACCOUNT = declare("Create New Account", CheckBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_NAME = declare("Billing Account Name", TextBox.class);
		public static final AttributeDescriptor BILL_TYPE = declare("Bill Type", ComboBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_DUE_DAY = declare("Billing Account Due Day", TextBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_PREFIX = declare("Billing Contact Prefix", ComboBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_FIRST_NAME = declare("Billing Contact First Name", TextBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_MIDDLE_NAME = declare("Billing Contact Middle Name", TextBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_LAST_NAME = declare("Billing Contact Last Name", TextBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
		public static final AttributeDescriptor ADD_INVOICING_CALENDAR = declare("Add Invoicing Calendar", AssetList.class, InvoicingCalendarTab.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AttributeDescriptor SELECT_INVOICING_CALENDAR = declare("Select Invoicing Calendar", AssetList.class, InvoicingCalendarComboBox.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public static final class InvoicingCalendarTab extends MetaData {
			public static final AttributeDescriptor BILLING_CALENDAR = declare("Billing Calendar", ComboBox.class);
			public static final AttributeDescriptor CALENDAR_NAME = declare("Calendar Name", TextBox.class);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AttributeDescriptor INVOICING_FREQUENCY = declare("Invoicing Frequency", ComboBox.class);
			public static final AttributeDescriptor INVOICING_RULE = declare("Invoicing Rule", ComboBox.class);
			public static final AttributeDescriptor BILLING_PERIOD_OFFSET = declare("Billing Period Offset", ComboBox.class);
			public static final AttributeDescriptor INVOICE_DUE_DAY = declare("Invoice Due Day", ComboBox.class);
			public static final AttributeDescriptor GENERATION_DATE_RULE = declare("Generation Date Rule", TextBox.class);
		}

		public static final class InvoicingCalendarComboBox extends MetaData {
			public static final AttributeDescriptor INVOICING_CALENDAR = declare("Invoicing Calendar", ComboBox.class);
		}
	}

	public static final class UpdateBillingAccountActionTab extends MetaData {
		public static final AttributeDescriptor PAYMENT_METHODS = PurchaseMetaData.PurchaseTab.PAYMENT_METHODS;
		public static final AttributeDescriptor BILLING_ACCOUNT_NAME = declare("Billing Account Name", TextBox.class);
		public static final AttributeDescriptor BILL_TYPE = declare("Bill Type", ComboBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_DUE_DAY = declare("Billing Account Due Day", TextBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_PREFIX = declare("Billing Contact Prefix", ComboBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_FIRST_NAME = declare("Billing Contact First Name", TextBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_MIDDLE_NAME = declare("Billing Contact Middle Name", TextBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_LAST_NAME = declare("Billing Contact Last Name", TextBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
		public static final AttributeDescriptor ADD_INVOICING_CALENDAR = declare("Add Invoicing Calendar", AssetList.class, InvoicingCalendarTab.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
		public static final AttributeDescriptor SELECT_INVOICING_CALENDAR = declare("Select Invoicing Calendar", AssetList.class, InvoicingCalendarComboBox.class, By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));

		public static final class InvoicingCalendarTab extends MetaData {
			public static final AttributeDescriptor BILLING_CALENDAR = declare("Billing Calendar", ComboBox.class);
			public static final AttributeDescriptor CALENDAR_NAME = declare("Calendar Name", TextBox.class);
			public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
			public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
			public static final AttributeDescriptor INVOICING_FREQUENCY = declare("Invoicing Frequency", ComboBox.class);
			public static final AttributeDescriptor INVOICING_RULE = declare("Invoicing Rule", ComboBox.class);
			public static final AttributeDescriptor BILLING_PERIOD_OFFSET = declare("Billing Period Offset", ComboBox.class);
			public static final AttributeDescriptor INVOICE_DUE_DAY = declare("Invoice Due Day", ComboBox.class);
			public static final AttributeDescriptor GENERATION_DATE_RULE = declare("Generation Date Rule", TextBox.class);
		}

		public static final class InvoicingCalendarComboBox extends MetaData {
			public static final AttributeDescriptor INVOICING_CALENDAR = declare("Invoicing Calendar", ComboBox.class);
		}
	}

	public static final class DeclinePaymentActionTab extends MetaData {
		public static final AttributeDescriptor DECLINE_REASON = declare("Decline Reason", ComboBox.class);
		public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class);
	}

	public static final class OtherTransactionsActionTab extends MetaData {
		public static final AttributeDescriptor TRANSACTION_TYPE = declare("Transaction Type", ComboBox.class);
		public static final AttributeDescriptor TRANSACTION_SUBTYPE = declare("Transaction Subtype", ComboBox.class);
		public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
	}

	public static final class RefundActionTab extends MetaData {
		public static final AttributeDescriptor PAYMENT_METHOD = declare("Payment Method", ComboBox.class);
		public static final AttributeDescriptor CHECK_NUMBER = declare("Check Number", TextBox.class);
		public static final AttributeDescriptor CHECK_DATE = declare("Check Date", TextBox.class);
		public static final AttributeDescriptor PAYEE_NAME = declare("Payee Name", TextBox.class);
		public static final AttributeDescriptor REFERENCE = declare("Reference #", TextBox.class);
		public static final AttributeDescriptor AMOUNT = declare("Amount", TextBox.class);
	}

	public static final class TransferPaymentActionTab extends MetaData {
		public static final AttributeDescriptor TRANSFER_REASON = declare("Transfer Reason", ComboBox.class);
		public static final AttributeDescriptor ALLOCATION = declare("Allocation", DialogMultiSelector.class, AllocationMultiSelector.class);
		public static final AttributeDescriptor ALLOCATED_AMOUNT = declare("Allocated Amount", TextBox.class);

		public static final class AllocationMultiSelector extends MetaData {
			public static final AttributeDescriptor BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
			public static final AttributeDescriptor POLICY_NUMBER = declare("Policy #", TextBox.class);
			public static final AttributeDescriptor BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
			public static final AttributeDescriptor MORTGAGEE = declare("Mortgagee", TextBox.class);
			public static final AttributeDescriptor AGENCY = declare("Agency", TextBox.class);
			public static final AttributeDescriptor AGENCY_CODE = declare("Agency Code", TextBox.class);

			public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.SLEEP(30000), false, By.id("transferPaymentForm:searchAllocationLnkText"));
		}
	}

	public static final class TransferPaymentBenefitsActionTab extends MetaData {
		public static final AttributeDescriptor BILLING_ACCOUNT = declare("Billing Account", DialogSingleSelector.class, BillingAccountSingleSelector.class, false, By.id("searchBillingAccountsForm:billingAccountSearchPopup"));
		public static final AttributeDescriptor TRANSFER_AMOUNT = declare("Transfer Amount", TextBox.class);

		public static final class BillingAccountSingleSelector extends MetaData {
			public static final AttributeDescriptor BUSINESS_NAME = declare("Business Name", TextBox.class);
			public static final AttributeDescriptor BILLING_ACCOUNT_NUMBER = declare("Billing Account #", TextBox.class);
			public static final AttributeDescriptor POLICY_NUMBER = declare("Policy #", TextBox.class);
			public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
			public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
			public static final AttributeDescriptor CASE_NUMBER = declare("Case #", TextBox.class);

			public static final AttributeDescriptor BUTTON_OPEN_POPUP = declare(AbstractDialog.DEFAULT_POPUP_OPENER_NAME, Button.class, Waiters.SLEEP(30000), false, By.id("paymentForm:showBillingAccountSearchPopupLnk"));
		}
	}

	public static final class MovePoliciesActionTab extends MetaData {
		public static final AttributeDescriptor POLICIES = declare("Policy", CheckBox.class);
		public static final AttributeDescriptor CREATE_NEW_ACCOUNT = declare("Create New Account", CheckBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_NAME = declare("Billing Account Name", TextBox.class);
		public static final AttributeDescriptor BILL_TYPE = declare("Bill Type", ComboBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_DUE_DAY = declare("Billing Account Due Day", TextBox.class);
		public static final AttributeDescriptor BILLING_ACCOUNT_NAME_TYPE = declare("Billing Account Name Type", ComboBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_PREFIX = declare("Billing Contact Prefix", ComboBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_FIRST_NAME = declare("Billing Contact First Name", TextBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_MIDDLE_NAME = declare("Billing Contact Middle Name", TextBox.class);
		public static final AttributeDescriptor BILLING_CONTACT_LAST_NAME = declare("Billing Contact Last Name", TextBox.class);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class);
		public static final AttributeDescriptor PHONE = declare("Phone #", TextBox.class);
		public static final AttributeDescriptor INVOICING_CALENDAR = declare("Invoicing Calendar", ComboBox.class);
		public static final AttributeDescriptor PAYMENT_PLAN = declare("Payment Plan", ComboBox.class);
	}

	public static final class WaiveFeeActionTab extends MetaData {

	}

	public static final class AddHoldActionTab extends MetaData {
		public static final AttributeDescriptor INVOICE = declare("Invoice", CheckBox.class, By.id("holdForm:holdType:0"));
		public static final AttributeDescriptor CANCEL_NOTICE = declare("Cancel Notice", CheckBox.class, By.id("holdForm:holdType:1"));
		public static final AttributeDescriptor HOLD_EFFECTIVE_DATE = declare("Hold Effective Date", TextBox.class);
		public static final AttributeDescriptor HOLD_EXPIRATION_DATE = declare("Hold Expiration Date", TextBox.class);
	}

	public static final class AddPaymentMethodTab extends MetaData {
		public static final AttributeDescriptor PAYMENT_METHOD = declare("Payment Method", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor COUNTRY = declare("Country", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ZIP_POSTAL_CODE = declare("Zip / Postal Code", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_1 = declare("Address Line 1", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_2 = declare("Address Line 2", TextBox.class);
		public static final AttributeDescriptor ADDRESS_LINE_3 = declare("Address Line 3", TextBox.class);
		public static final AttributeDescriptor CITY = declare("City", TextBox.class);
		public static final AttributeDescriptor STATE_PROVINCE = declare("State / Province", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor NUMBER = declare("Number", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor CARD_HOLDER_NAME = declare("Card Holder Name", TextBox.class);
		public static final AttributeDescriptor EXPIRATION_MONTH_YEAR = declare("Expiration Month/Year", DoubleComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor TRANSIT = declare("Transit #", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor BANK_NAME = declare("Bank Name", TextBox.class);
		public static final AttributeDescriptor ACCOUNT = declare("Account #", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ACCOUNT_TYPE = declare("Account Type", ComboBox.class);
		public static final AttributeDescriptor PREFIX = declare("Prefix", ComboBox.class);
		public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
		public static final AttributeDescriptor MIDDLE_NAME = declare("Middle Name", TextBox.class);
		public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
	}
}
