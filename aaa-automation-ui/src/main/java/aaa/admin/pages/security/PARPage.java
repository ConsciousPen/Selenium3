/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.security;

import org.openqa.selenium.By;

import aaa.admin.metadata.security.RoleMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class PARPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.xpath("//form[contains(@id, 'SearchForm')]"), RoleMetaData.SearchByField.class);

    public static Button buttonAddPAR = new Button(By.id("roleSearchForm:add-role"));
    public static Table tableSearchResults = new Table(By.id("roleSearchForm:body_searchResult"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
}
