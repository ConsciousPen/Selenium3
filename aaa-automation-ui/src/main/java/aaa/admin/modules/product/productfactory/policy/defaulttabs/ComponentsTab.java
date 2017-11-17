/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import org.openqa.selenium.By;


import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.pages.product.CommonProductFactoryPage;
import aaa.common.Tab;
import aaa.common.pages.Page;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.waiters.LocatorWaiter;

public class ComponentsTab extends PFDefaultTab {
    AssetList assetListProperties;

    public static Button buttonDone = new Button(By.xpath("//form[@id='component-attributes']//button[.='Done']"));

    public ComponentsTab() {
        super(ProductMetaData.ComponentsTab.class);
        assetListProperties = new AssetList(By.xpath("//*"), metaDataClass);
        assetListProperties.setName("Properties");

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void setSectionValue(int index, TestData value) {
                assetListProperties.fill(value);
            }

            @Override
            protected void selectSection(int index) {}

            @Override
            protected void addSection(int index, int size) {}
        };
    }

    public AssetList getPropertiesAssetList() {
        return assetListProperties;
    }

    @Override
    public Tab submitTab() {
        if (btnSave.isPresent()) {
            btnSave.click();
        }
        CommonProductFactoryPage.activateNavigation();
        if (linkNextTab.isPresent()) {
            linkNextTab.click(new LocatorWaiter(By.id("tree-bar-right:addTab")));
        }
        return this;
    }

}
