package aaa.main.metadata.policy;

import org.openqa.selenium.By;

import aaa.main.metadata.BillingAccountMetaData;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.PaymentMethodAllocationControl;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class PurchaseMetaData {

	public static final class PurchaseTab extends MetaData {
		public static final AssetDescriptor<AddPaymentMethodsMultiAssetList> PAYMENT_METHODS = declare("PaymentMethods", AddPaymentMethodsMultiAssetList.class, BillingAccountMetaData.AddPaymentMethodTab.class);

		public static final AssetDescriptor<CheckBox> ACTIVATE_AUTOPAY = declare("Activate Autopay", CheckBox.class, Waiters.AJAX, By.xpath(".//input[@id='purchaseForm:billingAutomaticRecurring']"));
		public static final AssetDescriptor<ComboBox> AUTOPAY_SELECTION = declare("Autopay Selection", ComboBox.class, Waiters.AJAX, By.xpath(".//select[@id='purchaseForm:paymentType']"));
		public static final AssetDescriptor<RadioGroup> APPLY_OVERPAID_AMOUNT_TO = declare("Apply Overpaid Amount to:", RadioGroup.class, Waiters.AJAX);

		public static final AssetDescriptor<CheckBox> CHANGE_MINIMUM_DOWNPAYMENT = declare("Change Minimum Down Payment", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> MINIMUM_REQUIRED_DOWNPAYMENT = declare("Minimum Required Down Payment", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REASON_FOR_CHANGING = declare("Reason for Changing", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDITIONAL_INFORMATION = declare("Additional Information", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> PAYMENT_METHOD_CASH = declare("Cash", TextBox.class, Waiters.AJAX, false, By.xpath(".//input[@id='purchaseForm:amount_0']"));
		public static final AssetDescriptor<PaymentMethodAllocationControl> PAYMENT_ALLOCATION = declare("PaymentAllocation", PaymentMethodAllocationControl.class, MetaData.class, By.id("purchaseForm:PaymentDetailsTable"));

		public static final class ComunityServiceSurveyPromt extends MetaData {
			public static final AssetDescriptor<TextBox> NAMED_INSURED = declare("Named Insured", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> GENDER = declare("Gender", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<TextBox> RACE_OF_ORIGIN = declare("Race or National Origin", TextBox.class, Waiters.NONE);
			public static final AssetDescriptor<Button> OK = declare("Ok", Button.class, Waiters.AJAX, false, By.id("purchaseForm:okBtn"));

		}
	}
	
	public static final class PurchasePaymentMethod extends MetaData {
		public static final AssetDescriptor<ComboBox> PAYMENT_METHOD = declare("Payment Method", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> GENERAL_TYPE = declare("Type", ComboBox.class, Waiters.AJAX, By.xpath(".//select[@id='paymentMethodForm:generalPaymentMethod_displayType']"));
		public static final AssetDescriptor<TextBox> NUMBER = declare("Number", TextBox.class, Waiters.AJAX, By.xpath(".//input[@id='paymentMethodForm:generalPaymentMethod_fullNumber']"));
		public static final AssetDescriptor<Button> ADD_CARD_BANK_BTN = declare("AddCard", Button.class, Waiters.AJAX, false, By.id("paymentMethodForm:pciSaveBtn"));
		public static final AssetDescriptor<Button> BACK_BUTTON = declare("Back", Button.class, Waiters.AJAX, false, By.id("primaryButtonsForm:backButton_footer"));
		
		public static final AssetDescriptor<TextBox> TRANSIT_NUMBER = declare("Transit", TextBox.class, Waiters.AJAX, false, By.id("paymentMethodEFTForm:paymentEFT_transitNumber"));
		public static final AssetDescriptor<TextBox> BANK_NAME = declare("Bank Name", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ACCOUNT_NUMBER = declare("Account", TextBox.class, Waiters.AJAX, false, By.id("paymentMethodEFTForm:paymentEFT_accountNumber"));
		
		public static final AssetDescriptor<Button> ADD_ACH_SAVE_BTN = declare("AddACH", Button.class, Waiters.AJAX, false, By.id("paymentMethodEFTForm:eftSaveBtn"));
	}

}
