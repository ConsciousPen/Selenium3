/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.product.productfactory.policy.defaulttabs;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import aaa.admin.metadata.product.ProductMetaData;
import aaa.common.Tab;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class NewProductTab extends PFDefaultTab {
    PFButton btnSave = new PFButton(By.id("product:save"));

    public NewProductTab() {
        super(ProductMetaData.NewProductTab.class);
        assetList = new AssetList(By.id("product"), metaDataClass);
        assetList.applyConfiguration("NewProduct");
    }

    @Override
    public Tab submitTab() {
        btnSave.click();
        return this;
    }
}
