/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.defaulttabs;

import org.openqa.selenium.By;

import aaa.main.modules.customer.defaulttabs.DivisionsTab;

import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class DivisionsTab extends DefaultTab {

	public static Button buttonRemoveDevision = new Button(By.xpath("//a[text()='Remove This Division']"));
    public static Button buttonAddDivision = new Button(By.id("crmForm:addDivision"));

    public static CheckBox checkBoxAssociateDivisions = new CheckBox(By.id("crmForm:generalInfoLeft_associateDivisions"));

    public DivisionsTab() {
        super(CustomerMetaData.DivisionsTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(DivisionsTab.class.getSimpleName()) && td.getTestData(DivisionsTab.class.getSimpleName()).getKeys().size() > 0) {
            if (!buttonRemoveDevision.isPresent()) {
                buttonAddDivision.click();
            }
            ((AssetList) super.getAssetList()).setValue(td.getTestData(getMetaKey()));
        }

        return this;
    }
}
