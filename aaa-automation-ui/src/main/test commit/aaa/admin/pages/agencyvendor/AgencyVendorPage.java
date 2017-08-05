/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.agencyvendor;

import org.openqa.selenium.By;

import aaa.admin.metadata.agencyvendor.AgencyMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class AgencyVendorPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("brokerManagementForm"), AgencyMetaData.SearchByField.class);
    public static Button buttonAddNewAgency = new Button(By.id("brokerManagementForm:addButton"));
    public static Table tableAgencies = new Table(By.id("brokerManagementForm:body_brokerInfoTable"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }
    
    public static TestData getSearchTestData(String fieldLabel, String fieldValue) {
        return new SimpleDataProvider().adjust(AgencyMetaData.SearchByField.class.getSimpleName(), new SimpleDataProvider().adjust(fieldLabel, fieldValue));
    }
    
    public static TestData getSearchTestData(TestData testData) {
        return new SimpleDataProvider().adjust(AgencyMetaData.SearchByField.class.getSimpleName(), testData);
    }
    
    public static String searchMessage() {
        return new StaticElement(By.xpath("//span[@id='brokerManagementForm:brokerSearchMessage']//td")).getValue();
    }
}