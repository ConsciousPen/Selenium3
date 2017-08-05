/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.enums.AccountConstants;
import aaa.main.metadata.AccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class SelectContactTab extends ActionTab {

    public static Button buttonSelectContact = new Button(By.xpath("//input[@value= 'Select Contact']"));
    public static Button buttonContactSearch = new Button(By.id("contactSearchPopupForm:search"));
    public static Table tableContactSeachResults = new Table(By.id("contactSearchPopupForm:resultsTable"));

    public SelectContactTab() {
        super(AccountMetaData.SelectContactTab.class);
        assetList = new AssetList(By.id("contactSearchPopup_container"), metaDataClass);
    }

    @Override
    public Tab fillTab(TestData td) {
        buttonSelectContact.click();
        ((AssetList) assetList).fill(td);
        buttonContactSearch.click();
        tableContactSeachResults.getColumn(AccountConstants.AccountContactSeachResultsTable.NAME).getCell(1).controls.links.getFirst().click();
        return this;
    }
}
