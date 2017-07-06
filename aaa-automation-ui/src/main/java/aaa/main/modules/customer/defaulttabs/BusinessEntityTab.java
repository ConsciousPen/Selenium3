/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;

// TODO 1) assetlist is protected field. It's not necessary to use super.getAssetList() & getAssetlist(), or is is used then is should be used everywhere.
public class BusinessEntityTab extends DefaultTab {

    public static final Button buttonAddBusinessEntity = new Button(By.id("crmForm:addBusinessEntity"));
    public static final Button buttonAddNaics = new Button(By.id("crmForm:addNAICS"));

    public BusinessEntityTab() {
        super(CustomerMetaData.BusinessEntityTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey()) && td.getTestData(getMetaKey()).containsKey(CustomerMetaData.BusinessEntityTab.NON_INDIVIDUAL_TYPE.getLabel())) {
            buttonAddBusinessEntity.click();
            if (td.getTestData(getMetaKey()).containsKey(CustomerMetaData.BusinessEntityTab.SECTOR.getLabel())) {
                buttonAddNaics.click();
            }

            ((AssetList) super.getAssetList()).setValue(td.getTestData(getMetaKey()));
        }
        return this;
    }
}
