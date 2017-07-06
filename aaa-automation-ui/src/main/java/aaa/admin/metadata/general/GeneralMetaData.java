/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.metadata.general;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public final class GeneralMetaData {

    public static final class NumberRangeSearchByField extends MetaData {
        public static final AttributeDescriptor RANGE_TYPE = declare("Range Type", ComboBox.class);
        public static final AttributeDescriptor RANGE_STATUS = declare("Range Status", ComboBox.class);
    }

    public static final class AddNumberRange extends MetaData {
        public static final AttributeDescriptor RANGE_TYPE = declare("Range Type", ComboBox.class);
        public static final AttributeDescriptor START_NUMBER = declare("Start #", TextBox.class);
        public static final AttributeDescriptor END_NUMBER = declare("End #", TextBox.class);
    }

    public static final class NoteSearchByField extends MetaData {
        public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
        public static final AttributeDescriptor CATEGORY = declare("Category", TextBox.class);
        public static final AttributeDescriptor TITLE = declare("Title", TextBox.class);
    }

    public static final class AddNoteCategory extends MetaData {
        public static final AttributeDescriptor TYPE = declare("Type", ComboBox.class);
        public static final AttributeDescriptor STATUS = declare("Status", RadioGroup.class);
        public static final AttributeDescriptor NEW_CATEGORY = declare("New Category", TextBox.class);
        public static final AttributeDescriptor TITLE = declare("Title", TextBox.class);
        public static final AttributeDescriptor QUICK_NOTE = declare("Quick Note", TextBox.class,
                By.name("quickNoteForm:quickNoteInfoPanel_userText_input"));
    }

}
