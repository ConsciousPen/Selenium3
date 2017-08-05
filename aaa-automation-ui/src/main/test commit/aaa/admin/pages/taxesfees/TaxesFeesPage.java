/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.taxesfees;

import org.openqa.selenium.By;

import aaa.admin.metadata.taxesfees.TaxesFeesMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class TaxesFeesPage extends AdminPage {

    public static AssetList assetListSearchForm = new AssetList(By.id("searchForm"), TaxesFeesMetaData.SearchByField.class);

    public static Table tableFeeAndTaxResults = new Table(By.id("searchForm:body_results"));
    public static Table tableFeeGroupResults = new Table(By.xpath("//div[@id='searchForm:results']//table"));
    public static Table tableRules = new Table(By.id("taxStrategyForm:body_ruleResults"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
}
