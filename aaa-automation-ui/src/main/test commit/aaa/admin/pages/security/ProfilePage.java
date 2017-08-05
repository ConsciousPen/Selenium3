/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.pages.security;

import org.openqa.selenium.By;

import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.pages.AdminPage;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.AdminConstants;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class ProfilePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("profileSearchForm"), ProfileMetaData.SearchByField.class);

    public static Button buttonAddNewProfile = new Button(By.id("profileSearchForm:newProfileButton"));
    public static Table tableProfileSearchResults = new Table(By.id("profileSearchForm:usersSearchResult"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public static void change(TestData td) {
        search(td);
        tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.ACTION).controls.links.get(ActionConstants.CHANGE).click();
    }

    public static void inquiry(TestData td) {
        search(td);
        tableProfileSearchResults.getRow(1).getCell(AdminConstants.AdminProfileSearchResultsTable.FIRST_NAME).controls.links.getFirst().click();
    }

    public static TestData getSearchTestData(String fieldLabel, String fieldValue) {
        return new SimpleDataProvider().adjust(ProfileMetaData.SearchByField.class.getSimpleName(), new SimpleDataProvider().adjust(fieldLabel, fieldValue));
    }

    public static TestData getSearchTestData(TestData testData) {
        return new SimpleDataProvider().adjust(ProfileMetaData.SearchByField.class.getSimpleName(), testData);
    }

    public static Boolean isProfileFound() {
        return !new StaticElement(By.id("profileSearchForm:usersSearchResultPanel")).getValue().equals("Search Item not Found");
    }

}
