/*
  Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package aaa.admin.modules.taxesfees.feegroup.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.taxesfees.TaxesFeesMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import toolkit.webdriver.controls.Button;

public class AddFeeGroupTab extends DefaultTab {

    public static Button buttonSave = new Button(By.id("feeGroupForm:okBtnProfile"));

    public AddFeeGroupTab() {
        super(TaxesFeesMetaData.AddFeeGroupTab.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
