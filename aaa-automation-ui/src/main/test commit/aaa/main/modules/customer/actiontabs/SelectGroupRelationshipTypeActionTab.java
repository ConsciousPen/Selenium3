/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class SelectGroupRelationshipTypeActionTab extends ActionTab {
    public SelectGroupRelationshipTypeActionTab() {
        super(CustomerMetaData.SelectGroupRelationshipTypeActionTab.class);
        assetList = new AssetList(By.xpath("//*"), metaDataClass);
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.buttonOk.click();
        return this;
    }
}
