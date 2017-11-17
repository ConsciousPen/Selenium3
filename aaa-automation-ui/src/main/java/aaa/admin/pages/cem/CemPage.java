/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.cem;

import org.openqa.selenium.By;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class CemPage extends AdminPage {

    public static AssetList assetListSearchForm = new AssetList(By.xpath("//thead[contains(@id, 'searchTable')]"), CemMetaData.SearchByField.class);

    public static Button buttonCreateNewMajorLargeAccount = new Button(By.id("createNewAccount"));
    public static Button buttonCreateNewGroup = new Button(By.id("createNewGroup"));
    public static Button buttonSave = new Button(By.id("configForm:saveButton_footer"));
    public static Button buttonConfirm = new Button(By.id("cancelConfirmDialogDialog_form:buttonYes"));

    public static Table tableMajorLargeAccount = new Table(By.id("searchForm:searchTable"));
    public static Table tableGroupsInformation = new Table(By.id("groupInfoSearchForm:searchTable"));

    public static CheckBox customerUiConfiguration = new CheckBox(By.id("configForm:customerCoreUIManagement"));

    public static void search(TestData td) {
        assetListSearchForm.applyConfiguration("CemSearch");
        assetListSearchForm.fill(td);
    }
}
