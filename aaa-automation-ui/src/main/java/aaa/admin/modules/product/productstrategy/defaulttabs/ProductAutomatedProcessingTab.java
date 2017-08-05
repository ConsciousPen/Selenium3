/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.product.productstrategy.defaulttabs;

import org.openqa.selenium.By;

import aaa.admin.metadata.product.ProductMetaData;
import aaa.common.DefaultTab;
import aaa.common.Tab;
import toolkit.webdriver.controls.Button;

public class ProductAutomatedProcessingTab extends DefaultTab {

    public static Button submitButton = new Button(By.id("strategyInputForm:submit"));

    public ProductAutomatedProcessingTab() {
        super(ProductMetaData.ProductAutomatedProcessingTab.class);
    }

    @Override
    public Tab submitTab() {
        submitButton.click();
        return this;
    }
}
