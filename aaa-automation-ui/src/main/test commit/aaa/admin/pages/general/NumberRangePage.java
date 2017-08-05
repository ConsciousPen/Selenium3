/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.general;

import org.openqa.selenium.By;

import aaa.admin.metadata.general.GeneralMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class NumberRangePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("numberRangeForm"), GeneralMetaData.NumberRangeSearchByField.class);

    public static Button buttonAddRange = new Button(By.id("numberRangeForm:addButton"));
    public static Button buttonSubmitRange = new Button(By.id("addNumberRangeForm:saveButton_footer"));

    public static Table tableSearchResults = new Table(By.xpath("//div[@id='numberRangeForm:dt']//table"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
}
