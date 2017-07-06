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

public class NotePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("quickNotesForm"), GeneralMetaData.NoteSearchByField.class);

    public static Button buttonAddNoteCategory = new Button(By.id("quickNotesForm:addNote"));
    public static Button buttonDisableCategory = new Button(By.id("quickNotesForm:disableCategory"));

    public static Table tableSearchResults = new Table(By.xpath("//div[@id='quickNotesForm:quickNoteInfoTable']//table"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
}
