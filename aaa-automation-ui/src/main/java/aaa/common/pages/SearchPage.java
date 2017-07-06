/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import org.openqa.selenium.By;

import aaa.common.pages.MainPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;

import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.metadata.SearchMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.AbstractEditableStringElement;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public class SearchPage extends MainPage {

	public static AbstractContainer<TestData, TestData> assetListSearch = new AssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), SearchMetaData.Search.class).applyConfiguration("Search");

	public static Button buttonClear = new Button(By.id("searchForm:clearBtn"));
	public static Button buttonCreateCustomer = new Button(By.id("searchForm:createAccountBtnAlway"));
	public static Button buttonSearch = new Button(By.id("searchForm:searchBtn"));

	public static StaticElement labelSearchError = new StaticElement(By.id("messages:0"));

	public static Table tableSearchResults = new Table(By.id("searchTable1Form:body_searchTable1"));
	public static Link linkSecondSearchedResult = new Link(By.xpath("(//table[@id='searchTable1Form:body_searchTable1']/tbody)/tr[2]/td[1]//span[text()]"));

	public static StaticElement labelNameParty = new StaticElement(By.xpath("//span[@id='partyPopup:name']"));
	public static Table tableRoleInfo = new Table(By.xpath("//table[@id='partyPopup:body_rolesTable']"));

	public static RadioGroup rbtnSearchFor = new RadioGroup(By.xpath("//table[@id='searchForm:entityTypeSelect']"), Waiters.AJAX);

	public static void search(SearchFor searchFor, SearchBy searchBy, String searchString) {
		if (!buttonSearch.isPresent()) {
			MainPage.QuickSearch.buttonSearchPlus.click();
		}

		rbtnSearchFor.setValue(searchFor.get());

		for (String key : assetListSearch.getAssetNames()) {
			if (key.contains(searchBy.get())) {
				((AbstractEditableStringElement) assetListSearch.getControl(key)).setValue(searchString);
				break;
			}
		}
		buttonSearch.click();
	}

	public static void search(SearchFor searchFor, TestData tdSearch) {
		if (!buttonSearch.isPresent()) {
			MainPage.QuickSearch.buttonSearchPlus.click();
		}

		rbtnSearchFor.setValue(searchFor.get());

		assetListSearch.fill(tdSearch);
		buttonSearch.click();
	}

	public static void openFirstResult() {
		SearchPage.tableSearchResults.getRow(1).getCell(1).controls.links.getFirst().click();
	}
}
