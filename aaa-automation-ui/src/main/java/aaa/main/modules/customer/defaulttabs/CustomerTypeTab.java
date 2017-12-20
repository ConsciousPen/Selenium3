/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.defaulttabs;

import org.openqa.selenium.By;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.AssetList;

// TODO 1) move vonfiguration and init of assetlist to constructor. 2) assetlist.fill checks by itself containing of metadata key.
public class CustomerTypeTab extends DefaultTab {
    public CustomerTypeTab() {
        super(CustomerMetaData.CustomerType.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey())) {
            assetList = new AssetList(By.xpath("//div[@id='searchForm:customerTypePopup_container' or @id='customerTypePopup_container']"), metaDataClass).applyConfiguration("CustomerTypeTab");
            assetList.fill(td);
        }
        return this;
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.confirm();
        return this;
    }
}
