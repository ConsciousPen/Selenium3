/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiontemplate.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.commission.CommissionMetaData;
import aaa.admin.metadata.commission.CommissionMetaData.AddCommissionTemplate.PolicyYear;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class AddCommissionTemplateTab extends DefaultTab {
    public static final Button buttonAdd = new Button(By.xpath("//button[contains(@id, 'addBandBtn')]"));

    public AddCommissionTemplateTab() {
        super(CommissionMetaData.AddCommissionTemplate.class);
        assetList = new AssetList(By.xpath(Page.DEFAULT_DIALOG_LOCATOR), metaDataClass);
    }

    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_DIALOG_LOCATOR), PolicyYear.class) {
            @Override
            protected void addSection(int index, int size) {
                if (index > 0)
                    buttonAdd.click();
            }

            @Override
            protected void selectSection(int index) {}
        };
        assetList.fill(td.getTestData(metaDataClass.getSimpleName()));
        return this;
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
