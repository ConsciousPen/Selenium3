/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.ClickComboBox;
import com.exigen.ipb.eisa.controls.RichTextBox;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class MyWorkMetaData {
	public static final class MyWorkTab extends MetaData {}

	public static final class AssignTaskToActionTab extends MetaData {
		public static final AssetDescriptor<CheckBox> STAY_IN_ORIGINAL_QUEUE = declare("Stay in Original Queue", CheckBox.class);
		public static final AssetDescriptor<TextBox> FIRST_NAME = declare("First Name", TextBox.class);
		public static final AssetDescriptor<TextBox> LAST_NAME = declare("Last Name", TextBox.class);
		public static final AssetDescriptor<ComboBox> WORK_STATUS = declare("Work Status", ComboBox.class);
		public static final AssetDescriptor<ComboBox> AGENCY = declare("Agency", ComboBox.class);
		public static final AssetDescriptor<ComboBox> QUEUE = declare("Queue", ComboBox.class);
		public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskAssignForm:userNote']//iframe"));
	}

	public static final class CompleteTaskActionTab extends MetaData {
		public static final AssetDescriptor<ComboBox> COMPLETION_REASON = declare("Completion Reason", ComboBox.class);
		public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskCompleteForm:userNote']//iframe"));
	}

	public static final class CreateTaskActionTab extends MetaData {
		public static final AssetDescriptor<ClickComboBox> TYPE = declare("Type", ClickComboBox.class, Waiters.AJAX.then(Waiters.SLEEP(2000)));
		public static final AssetDescriptor<ClickComboBox> TASK_NAME = declare("Task Name", ClickComboBox.class, Waiters.AJAX.then(Waiters.SLEEP(2000)));
		public static final AssetDescriptor<TextBox> REFERENCE_ID = declare("Reference ID", TextBox.class, Waiters.AJAX);
		public static final AssetDescriptor<ClickComboBox> PRIORITY = declare("Priority", ClickComboBox.class);
		public static final AssetDescriptor<StaticElement> CUSTOMER = declare("Customer", StaticElement.class);
		public static final AssetDescriptor<TextBox> WARNING_DATE_TIME = declare("Warning Date/Time", TextBox.class);
		public static final AssetDescriptor<TextBox> DUE_DATE_TIME = declare("Due Date/Time", TextBox.class);
		public static final AssetDescriptor<RichTextBox> DESCRIPTION = declare("Task Description", RichTextBox.class, By.xpath("//div[@id='taskCreateForm:taskDescription']//iframe"));
		public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskCreateForm:userNote']//iframe"));
	}

	public static final class UpdateTaskActionTab extends MetaData {
		public static final AssetDescriptor<ClickComboBox> PRIORITY = declare("Priority", ClickComboBox.class, Waiters.AJAX);
		public static final AssetDescriptor<TextBox> WARNING_DATE_TIME = declare("Warning Date/Time", TextBox.class);
		public static final AssetDescriptor<TextBox> DUE_DATE_TIME = declare("Due Date/Time", TextBox.class);
		public static final AssetDescriptor<RichTextBox> DESCRIPTION = declare("Task Description", RichTextBox.class, By.xpath("//div[@id='taskUpdateForm:taskDescription']//iframe"));
		public static final AssetDescriptor<RichTextBox> NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskUpdateForm:userNote']//iframe"));
	}

	public static final class FilterTaskActionTab extends MetaData {
		public static final AssetDescriptor<TextBox> REFERENCE_ID = declare("Reference ID:", TextBox.class, By.id("filterContainerForm:refNoFilter"));
		public static final AssetDescriptor<TextBox> TASK_ID = declare("Task ID:", TextBox.class, By.id("filterContainerForm:taskId"));
		public static final AssetDescriptor<ClickComboBox> TYPE = declare("Type:", ClickComboBox.class);
		public static final AssetDescriptor<ClickComboBox> STATUS = declare("Status:", ClickComboBox.class);
	}
}
