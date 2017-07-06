/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.metadata;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.ClickComboBox;
import com.exigen.ipb.etcsa.controls.RichTextBox;

import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class MyWorkMetaData {
    public static final class MyWorkTab extends MetaData {}

    public static final class AssignTaskToActionTab extends MetaData {
        public static final AttributeDescriptor STAY_IN_ORIGINAL_QUEUE = declare("Stay in Original Queue", CheckBox.class);
        public static final AttributeDescriptor FIRST_NAME = declare("First Name", TextBox.class);
        public static final AttributeDescriptor LAST_NAME = declare("Last Name", TextBox.class);
        public static final AttributeDescriptor WORK_STATUS = declare("Work Status", ComboBox.class);
        public static final AttributeDescriptor AGENCY = declare("Agency", ComboBox.class);
        public static final AttributeDescriptor QUEUE = declare("Queue", ComboBox.class);
        public static final AttributeDescriptor NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskAssignForm:userNote']//iframe"));
    }

    public static final class CompleteTaskActionTab extends MetaData {
        public static final AttributeDescriptor COMPLETION_REASON = declare("Completion Reason", ComboBox.class);
        public static final AttributeDescriptor NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskCompleteForm:userNote']//iframe"));
    }

    public static final class CreateTaskActionTab extends MetaData {
        public static final AttributeDescriptor TYPE = declare("Type", ClickComboBox.class, Waiters.AJAX.then(Waiters.SLEEP(2000)));
        public static final AttributeDescriptor TASK_NAME = declare("Task Name", ClickComboBox.class, Waiters.AJAX.then(Waiters.SLEEP(2000)));
        public static final AttributeDescriptor REFERENCE_ID = declare("Reference ID", TextBox.class, Waiters.AJAX);
        public static final AttributeDescriptor PRIORITY = declare("Priority", ClickComboBox.class);
        public static final AttributeDescriptor CUSTOMER = declare("Customer", StaticElement.class);
        public static final AttributeDescriptor WARNING_DATE_TIME = declare("Warning Date/Time", TextBox.class);
        public static final AttributeDescriptor DUE_DATE_TIME = declare("Due Date/Time", TextBox.class);
        public static final AttributeDescriptor DESCRIPTION = declare("Task Description", RichTextBox.class, By.xpath("//div[@id='taskCreateForm:taskDescription']//iframe"));
        public static final AttributeDescriptor NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskCreateForm:userNote']//iframe"));
    }

    public static final class UpdateTaskActionTab extends MetaData {
        public static final AttributeDescriptor PRIORITY = declare("Priority", ClickComboBox.class, Waiters.AJAX);
        public static final AttributeDescriptor WARNING_DATE_TIME = declare("Warning Date/Time", TextBox.class);
        public static final AttributeDescriptor DUE_DATE_TIME = declare("Due Date/Time", TextBox.class);
        public static final AttributeDescriptor DESCRIPTION = declare("Task Description", RichTextBox.class, By.xpath("//div[@id='taskUpdateForm:taskDescription']//iframe"));
        public static final AttributeDescriptor NOTE = declare("Task Note", RichTextBox.class, By.xpath("//div[@id='taskUpdateForm:userNote']//iframe"));
    }

    public static final class FilterTaskActionTab extends MetaData {
        public static final AttributeDescriptor REFERENCE_ID = declare("Reference ID:", TextBox.class, By.id("filterContainerForm:refNoFilter"));
        public static final AttributeDescriptor TASK_ID = declare("Task ID:", TextBox.class, By.id("filterContainerForm:taskId"));
        public static final AttributeDescriptor TYPE = declare("Type:", ClickComboBox.class);
        public static final AttributeDescriptor STATUS = declare("Status:", ClickComboBox.class);
    }
}
