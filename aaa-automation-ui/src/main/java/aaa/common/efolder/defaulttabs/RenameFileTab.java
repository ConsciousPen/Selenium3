/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.efolder.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.metadata.EfolderMataData;
import toolkit.webdriver.controls.Button;

public class RenameFileTab extends Tab {
    public static Button buttonOk = new Button(By.id("renameDocumentForm:okBtn"));

    public RenameFileTab() {
        super(EfolderMataData.RenameFileTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
