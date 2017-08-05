/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.product;

import org.openqa.selenium.By;

import aaa.admin.metadata.product.ProductMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class ProductProductFactoryPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("searchForm"), ProductMetaData.ProductProductFactorySearch.class);
    public static Button buttonAddNewProduct = new Button(By.id("searchForm:addbtn"));
    public static Table tableProducts = new Table(By.id("searchForm:body_foundProducts"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        new Button(assetListSearchForm, By.id("searchForm:search")).click();
    }
}
