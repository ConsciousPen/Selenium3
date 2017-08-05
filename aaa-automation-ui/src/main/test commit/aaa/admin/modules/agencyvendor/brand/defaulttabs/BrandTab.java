/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.agencyvendor.brand.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.agencyvendor.BrandMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import toolkit.webdriver.controls.Button;

public class BrandTab extends DefaultTab {
    public static Button buttonUpdate = new Button(By.xpath("//input[(@value = 'Update' or @value = 'UPDATEE') and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    public BrandTab() {
        super(BrandMetaData.BrandTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
