/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.metadata;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.RichTextBox;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class NotesAndAlertsMetaData {

    public static final class NotesAndAlertsTab extends MetaData {
        public static final AssetDescriptor<RadioGroup> NOTE_ALERT = declare("Note/Alert", RadioGroup.class,
                By.xpath("//table[@id='createNote_form:createNote_noteAlert']"));
        public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> CATEGORY = declare("Category", ComboBox.class);
        public static final AssetDescriptor<RadioGroup> CONFID = declare("Confid.", RadioGroup.class);
        public static final AssetDescriptor<TextBox> TITLE = declare("Title", TextBox.class);
        public static final AssetDescriptor<RichTextBox> NOTE = declare("Note", RichTextBox.class,
                By.xpath("//div[@id='createNote_form:createNote_note']//iframe"));
        public static final AssetDescriptor<RichTextBox> ALERT = declare("Alert", RichTextBox.class,
                By.xpath("//div[@id='createNote_form:createNote_note']//iframe"));
        public static final AssetDescriptor<RichTextBox> NOTE_UPDATE = declare("NoteUpdate", RichTextBox.class,
                By.xpath("//div[@id='updateNote_form:updateNote_note']//iframe"));
        public static final AssetDescriptor<RichTextBox> ALERT_UPDATE = declare("AlertUpdate", RichTextBox.class,
                By.xpath("//div[@id='updateAlert_form:updateAlert_note']//iframe"));

        public static Button buttonUpdate = new Button(By.xpath("//input[(@value = 'Update' or @value = 'UPDATEE') and not(@class = 'hidden') and not(contains(@style,'none'))]"));

        public static StaticElement labelAlert = new StaticElement(By.id("alertArchiveForm:alerts:0:arhiveAlert_id"));
    }
}
