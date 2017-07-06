/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.workflow;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.controls.ClickComboBox;

import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class WorkFlowMetadata {

    public static final class TaskSearchByField extends MetaData {
        public static final AttributeDescriptor ENTITY_TYPE = declare("Entity Type", ComboBox.class);
        public static final AttributeDescriptor PROCESS_TYPE = declare("Process Type", ComboBox.class);
        public static final AttributeDescriptor TASK_NAME = declare("Task name", TextBox.class);
        public static final AttributeDescriptor ACTIVE_ONLY = declare("Active only", CheckBox.class);
    }

    public static final class CreateManualTaskDefinitionTab extends MetaData {
        public static final AttributeDescriptor DEFINITION_KEY = declare("Definition Key", TextBox.class, Waiters.DEFAULT);
        public static final AttributeDescriptor ENTITY_TYPE = declare("Entity Type", ClickComboBox.class, Waiters.DEFAULT);
        public static final AttributeDescriptor TASK_NAME = declare("Task Name", TextBox.class);
        public static final AttributeDescriptor EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AttributeDescriptor EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AttributeDescriptor GENERATE_PRIVILEGE = declare("Generate Privilege", CheckBox.class);
        public static final AttributeDescriptor WARNING_DATE_DELAY = declare("Warning Date Delay", TextBox.class);
        public static final AttributeDescriptor DUE_DATE_DELAY = declare("Due Date Delay", TextBox.class);
        public static final AttributeDescriptor DESCRIPTION = declare("Description", TextBox.class,
                By.id("manualTaskDefinitionForm:taskDescription_input"));
        public static final AttributeDescriptor PREFERRED_QUEUE = declare("Preferred Queue", ClickComboBox.class);
        public static final AttributeDescriptor PRIORITY = declare("Priority", ClickComboBox.class);
    }
}
