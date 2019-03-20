package aaa.main.metadata.policy;

import org.openqa.selenium.By;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.PaymentMethodAllocationControl;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class PurchaseMetaData {

	public static final class PurchaseTab extends MetaData {
		public static final AssetDescriptor<AddPaymentMethodsMultiAssetList> PAYMENT_METHODS = declare("PaymentMethods", AddPaymentMethodsMultiAssetList.class,
				BillingAccountMetaData.AddPaymentMethodTab.class);

		public static final AssetDescriptor<CheckBox> ACTIVATE_AUTOPAY = declare("Activate Autopay", CheckBox.class, Waiters.AJAX, By.xpath(".//input[@id='purchaseForm:billingAutomaticRecurring']"));
		public static final AssetDescriptor<ComboBox> AUTOPAY_SELECTION = declare("Autopay Selection", ComboBox.class, Waiters.AJAX, By.xpath(".//select[@id='purchaseForm:paymentType']"));
		public static final AssetDescriptor<RadioGroup> SIGNATURE_ON_FILE_INDICATOR = declare("Signature on File Indicator", RadioGroup.class);
		public static final AssetDescriptor<RadioGroup> APPLY_OVERPAID_AMOUNT_TO = declare("Apply Overpaid Amount to:", RadioGroup.class, Waiters.AJAX);
		public static final AssetDescriptor<StaticElement> AUTOPAY_MESSAGE_WARNING_BLOCK =
				declare("You must activate AutoPay for the selected payment plan in order to keep eValue discount.", StaticElement.class, By.xpath(".//span[@id='purchaseForm:billingAcccountRecurringPayments_recurringPaymentsComponent']/div"));

		public static final AssetDescriptor<CheckBox> CHANGE_MINIMUM_DOWNPAYMENT = declare("Change Minimum Down Payment", CheckBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> MINIMUM_REQUIRED_DOWNPAYMENT = declare("Minimum Required Down Payment", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> PLIGA_FEE = declare("PLIGA Fee", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ComboBox> REASON_FOR_CHANGING = declare("Reason for Changing", ComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> ADDITIONAL_INFORMATION = declare("Additional Information", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> PAYMENT_METHOD_CASH = declare("Cash", TextBox.class, Waiters.AJAX, false, By.xpath(".//input[@id='purchaseForm:amount_0']"));
		public static final AssetDescriptor<TextBox> PAYMENT_METHOD_CHECK = declare("Check", TextBox.class, Waiters.AJAX, false, By.xpath(".//input[@id='purchaseForm:amount_1']"));
		public static final AssetDescriptor<RadioGroup> VOICE_SIGNATURE = declare("Bind by Voice Signature", RadioGroup.class, Waiters.AJAX, false, By
				.xpath(".//table[@id='purchaseForm:voiceBindingSelection']"));
		public static final AssetDescriptor<PaymentMethodAllocationControl> PAYMENT_ALLOCATION = declare("PaymentAllocation", PaymentMethodAllocationControl.class, MetaData.class, By
				.id("purchaseForm:PaymentDetailsTable"));

		public static final class ComunityServiceSurveyPromt extends MetaData {
			public static final AssetDescriptor<TextBox> NAMED_INSURED = declare("Named Insured", TextBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> GENDER = declare("Gender", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<ComboBox> RACE_OF_ORIGIN = declare("Race or National Origin", ComboBox.class, Waiters.AJAX);
			public static final AssetDescriptor<Button> OK = declare("Ok", Button.class, Waiters.AJAX, false, By.id("purchaseForm:okBtn"));

		}
	}
}
