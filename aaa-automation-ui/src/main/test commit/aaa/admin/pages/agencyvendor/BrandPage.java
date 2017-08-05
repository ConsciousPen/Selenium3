/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.agencyvendor;

import org.openqa.selenium.By;

import aaa.admin.metadata.agencyvendor.BrandMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class BrandPage extends AdminPage {

    public static Button buttonAddBrandType = new Button(By.id("brandTypeForm:addBrandType"));
    public static AssetList assetListAddBrandType = new AssetList(By.id("brandTypeEditForm:brandTypeEditDialog"), BrandMetaData.BrandTab.AddBrandTypeDialog.class);
    public static Table tableBrandType = new Table(By.xpath("//div[@id='brandTypeForm:brandsTypeTable']//table"));

    public static Button buttonAddBrand = new Button(By.id("brandInfoForm:addBrand"));
    public static AssetList assetListAddBrand = new AssetList(By.id("brandEditForm:brandEditDialog"), BrandMetaData.BrandTab.AddBrandDialog.class);
    public static Table tableBrands = new Table(By.xpath("//div[@id='brandInfoForm:brandsTable']//table"));

    public static void expandBrandType() {
        Link collapsedBrandType = new Link(By.xpath("//div[text()='Brand Types' and contains(@class, 'colps')]"));
        if (collapsedBrandType.isPresent()) {
            collapsedBrandType.click();
        }
    }
}
