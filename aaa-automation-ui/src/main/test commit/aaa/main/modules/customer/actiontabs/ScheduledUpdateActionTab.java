/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

// TODO 1) Multiasset list has an empty locator;
public class ScheduledUpdateActionTab extends ActionTab {
    public ScheduledUpdateActionTab() {
        super(CustomerMetaData.ScheduledUpdateActionTab.class);
        assetList = new MultiAssetList(By.xpath("//*"), metaDataClass) {

            @Override
            protected void addSection(int index, int size) {
                // TODO Auto-generated method stub

            }

            @Override
            protected void selectSection(int index) {
                // TODO Auto-generated method stub

            }

        };
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.confirm();
        return this;
    }
}
