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
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public class PurchaseMetaData {

	public static final class PurchaseTab extends MetaData {
		public static final AttributeDescriptor ACTIVATE_AUTOPAY = declare("Activate Autopay", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ACTIVATE_SELECTION = declare("Autopay Selection", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor APPLY_OVERPAID_AMOUNT_TO = declare("Apply Overpaid Amount to:", RadioGroup.class, Waiters.AJAX);

		public static final AttributeDescriptor CHANGE_MINIMUM_DOWNPAYMENT = declare("Change Minimum Down Payment", CheckBox.class, Waiters.AJAX);
		public static final AttributeDescriptor MINIMUM_REQUIRED_DOWNPAYMENT = declare("Minimum Required Down Payment", TextBox.class, Waiters.AJAX);
		public static final AttributeDescriptor REASON_FOR_CHANGING = declare("Reason for Changing", ComboBox.class, Waiters.AJAX);
		public static final AttributeDescriptor ADDITIONAL_INFORMATION = declare("Additional Information", TextBox.class, Waiters.AJAX);

		public static final AttributeDescriptor PAYMENT_METHODS = declare("PaymentMethods", AddPaymentMethodsMultiAssetList.class, BillingAccountMetaData.AddPaymentMethodTab.class);
		public static final AttributeDescriptor PAYMENT_ALLOCATION = declare("PaymentAllocation", PaymentMethodAllocationControl.class, MetaData.class, By.id("purchaseForm:PaymentDetailsTable"));

		public static final class ComunityServiceSurveyPromt extends MetaData {
			public static final AttributeDescriptor NAMED_INSURED = declare("Named Insured", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor GENDER = declare("Gender", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor RACE_OF_ORIGIN = declare("Race or National Origin", TextBox.class, Waiters.NONE);
			public static final AttributeDescriptor OK = declare("Ok", Button.class, Waiters.AJAX, false, By.id("purchaseForm:okBtn"));

		}
	}

}
