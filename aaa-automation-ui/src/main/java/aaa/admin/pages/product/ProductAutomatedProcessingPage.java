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

public class ProductAutomatedProcessingPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.xpath("//form[contains(@id, 'SearchForm')]"), ProductMetaData.SearchByField.class);
    public static Table tableProductStrategy = new Table(By.xpath("//table[@id='strategySearchForm:body_strategyInfoTable']"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        new Button(By.xpath("//input[contains(@id, 'SearchForm:searchButton')]")).click();
    }
}
