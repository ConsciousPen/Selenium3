package aaa.admin.metadata.reports;

import aaa.toolkit.webdriver.customcontrols.AdvancedSelectorOR;
import com.exigen.ipb.etcsa.controls.AdvancedSelector;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class OperationalReportsMetaData {

	public static final class OperationalReportsTab extends MetaData {
		public static final AssetDescriptor<RadioGroup> CATEGORY = declare("Category", RadioGroup.class);
		public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
		public static final AssetDescriptor<ComboBox> NAME = declare("Name", ComboBox.class);
		public static final AssetDescriptor<ComboBox> LANGUAGE = declare("Name", ComboBox.class);
		public static final AssetDescriptor<TextBox> ACTIVITY_DATE_FROM = declare("Activity Date From", TextBox.class);
		public static final AssetDescriptor<TextBox> AS_OF_ACCOUNTING_DATE = declare("As of Accounting Date", TextBox.class);
		public static final AssetDescriptor<TextBox> ACCOUNTING_DATE_FROM = declare("Accounting Date From", TextBox.class);
		public static final AssetDescriptor<TextBox> ACCOUNTING_DATE_TO = declare("Accounting Date To", TextBox.class);
		public static final AssetDescriptor<TextBox> ACTIVITY_DATE_TO = declare("Activity Date To", TextBox.class);
		public static final AssetDescriptor<AdvancedSelector> CHANNEL = declare("Channel", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> LOCATION_TYPE = declare("Location Type", AdvancedSelector.class);
		public static final AssetDescriptor<AdvancedSelector> LOCATION = declare("Location", AdvancedSelector.class);
		public static final AssetDescriptor<TextBox> POLICY_NUMBER = declare("Policy Number", TextBox.class);
		public static final AssetDescriptor<RadioGroup> DELIVERY_TYPE = declare("Delivery type", RadioGroup.class);
		public static final AssetDescriptor<CheckBox> MULTIPLE_OUTPUT_FORMATS = declare("Multiple Output Formats", CheckBox.class);
		public static final AssetDescriptor<RadioGroup> REPORT_FORMAT = declare("Report Format", RadioGroup.class);
		public static final AssetDescriptor<AdvancedSelector> EMAIL_ADDRESS = declare("E-mail address", AdvancedSelector.class, By.id("jobsForm:select_email"));
		public static final AssetDescriptor<TextBox> ADDITIONAL_EMAIL = declare("Additional E-mail address", TextBox.class);
		public static final AssetDescriptor<CheckBox> SCHEDULE = declare("Do you want to schedule?", CheckBox.class, By.id("jobsForm:showSchedulingPanel"));
		public static final AssetDescriptor<TextBox> REPORT_NAME = declare("Report Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> SCHEDULE_TYPE = declare("Schedule Type", ComboBox.class);
		public static final AssetDescriptor<TextBox> SCHEDULE_INTERVAL = declare("Schedule Interval", TextBox.class, By.id("jobsForm:interval"));
		public static final AssetDescriptor<ComboBox> SCHEDULE_INTERVAL_TYPE = declare("Schedule Interval Type", ComboBox.class, By.id("jobsForm:repeatingType2"));
		public static final AssetDescriptor<AdvancedSelectorOR> BUSINESS_UNIT = declare("Business Unit", AdvancedSelectorOR.class);
		public static final AssetDescriptor<Button> REPORT = declare("Report", Button.class, By.id("jobsForm:generateReport"));
	}
}
