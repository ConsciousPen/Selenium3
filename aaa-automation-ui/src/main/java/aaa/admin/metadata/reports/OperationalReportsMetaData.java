package aaa.admin.metadata.reports;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.AdvancedSelector;

import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class OperationalReportsMetaData {

    public static final class OperationalReportsTab extends MetaData {
        public static final AttributeDescriptor CATEGORY = declare("Category", RadioGroup.class);
        public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
        public static final AttributeDescriptor NAME = declare("Name", ComboBox.class);
        public static final AttributeDescriptor LANGUAGE = declare("Name", ComboBox.class);
        public static final AttributeDescriptor ACTIVITY_DATE_FROM = declare("Activity Date From", TextBox.class);
        public static final AttributeDescriptor ACTIVITY_DATE_TO = declare("Activity Date To", TextBox.class);
        public static final AttributeDescriptor CHANNEL = declare("Channel", AdvancedSelector.class);
        public static final AttributeDescriptor LOCATION_TYPE = declare("Location Type", AdvancedSelector.class);
        public static final AttributeDescriptor LOCATION = declare("Location", AdvancedSelector.class);
        public static final AttributeDescriptor POLICY_NUMBER = declare("Policy", TextBox.class);
        public static final AttributeDescriptor DELIVERY_TYPE = declare("Delivery type", RadioGroup.class);
        public static final AttributeDescriptor MULTIPLE_OUTPUT_FORMATS = declare("Multiple Output Formats",
                CheckBox.class);
        public static final AttributeDescriptor REPORT_FORMAT = declare("Report Format", RadioGroup.class);
        public static final AttributeDescriptor EMAIL_ADDRESS = declare("E-mail address", AdvancedSelector.class, By.id("jobsForm:select_email"));
        public static final AttributeDescriptor ADDITIONAL_EMAIL = declare("Additional E-mail address", TextBox.class);
        public static final AttributeDescriptor SCHEDULE = declare("Do you want to schedule?", CheckBox.class, By.id("jobsForm:showSchedulingPanel"));
        public static final AttributeDescriptor REPORT_NAME = declare("Report Name", TextBox.class);
        public static final AttributeDescriptor SCHEDULE_TYPE = declare("Schedule Type", ComboBox.class);
        public static final AttributeDescriptor SCHEDULE_INTERVAL = declare("Schedule Interval", TextBox.class, By.id("jobsForm:interval"));
        public static final AttributeDescriptor SCHEDULE_INTERVAL_TYPE = declare("Schedule Interval Type", ComboBox.class, By.id("jobsForm:repeatingType2"));
    }

}
