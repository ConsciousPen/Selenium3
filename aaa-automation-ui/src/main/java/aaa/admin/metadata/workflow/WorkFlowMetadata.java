/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.metadata.workflow;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.ClickComboBox;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public final class WorkFlowMetadata {

    public static final class TaskSearchByField extends MetaData {
        public static final AssetDescriptor<ComboBox> ENTITY_TYPE = declare("Entity Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> PROCESS_TYPE = declare("Process Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> TASK_NAME = declare("Task name", TextBox.class);
        public static final AssetDescriptor<CheckBox> ACTIVE_ONLY = declare("Active only", CheckBox.class);
    }

    public static final class CreateManualTaskDefinitionTab extends MetaData {
        public static final AssetDescriptor<TextBox> DEFINITION_KEY = declare("Definition Key", TextBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<ClickComboBox> ENTITY_TYPE = declare("Entity Type", ClickComboBox.class, Waiters.DEFAULT);
        public static final AssetDescriptor<TextBox> TASK_NAME = declare("Task Name", TextBox.class);
        public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
        public static final AssetDescriptor<TextBox> EXPIRATION_DATE = declare("Expiration Date", TextBox.class);
        public static final AssetDescriptor<CheckBox> GENERATE_PRIVILEGE = declare("Generate Privilege", CheckBox.class);
        public static final AssetDescriptor<TextBox> WARNING_DATE_DELAY = declare("Warning Date Delay", TextBox.class);
        public static final AssetDescriptor<TextBox> DUE_DATE_DELAY = declare("Due Date Delay", TextBox.class);
        public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class,
                By.id("manualTaskDefinitionForm:taskDescription_input"));
        public static final AssetDescriptor<ClickComboBox> PREFERRED_QUEUE = declare("Preferred Queue", ClickComboBox.class);
        public static final AssetDescriptor<ClickComboBox> PRIORITY = declare("Priority", ClickComboBox.class);
    }
}
