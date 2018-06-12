/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.pages.product;

import org.openqa.selenium.By;
import aaa.admin.metadata.product.MoratoriumMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class MoratoriumPage {
	public static AssetList assetListSearch = new AssetList(By.id("searchForm"), MoratoriumMetaData.SearchMetaData.class);

	public static Button buttonAddMoratorium = new Button(By.id("searchForm:addMoratoriumBtn"));
	public static Button buttonSearch = new Button(By.id("searchForm:searchBtn"));

	public static Table tableSearchResult = new Table(By.id("searchResultForm:resultInfoTable")).applyConfiguration("MoratoriumSearchResult");

	public static void search(TestData testData) {
		assetListSearch.fill(testData);
		buttonSearch.click();
	}
}
