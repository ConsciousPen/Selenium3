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

public class ChildrenPage extends AdminPage {
	public static Table tableAddedChildren = new Table(By.id("brokerChildrenForm:brokerChildAgenciesTable"));
	public static Button buttonAddChild = new Button(By.id("brokerChildrenForm:addUnderwriter"));
	
    public static AssetList assetListSearchForm = new AssetList(By.id("childAgencySearch"), AgencyMetaData.SearchByField.class);
    public static Table tableSearchFormResults = new Table(By.id("childAgencySearch:body_childAgencySearchResults"));
    public static Button buttonCancelSearchDialog = new Button(By.id("childAgencySearch:cancelBtn"));

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
        return new StaticElement(By.xpath("//span[@id='childAgencySearch:childAgencySearchMessage']//td")).getValue();
    }
}