/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import aaa.common.enums.SearchEnum;
import aaa.common.metadata.SearchMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

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

	//public static RadioGroup rbtnSearchFor = new RadioGroup(By.xpath("//table[@id='searchForm:entityTypeSelect']"), Waiters.AJAX);

	/*public static void search(SearchEnum.SearchFor searchFor, TestData tdSearch) {

		if (!buttonSearch.isPresent()) {
			MainPage.QuickSearch.buttonSearchPlus.click();
		}

		assetListSearch.getControl(SearchMetaData.Search.SEARCH_FOR.getLabel(), RadioGroup.class).setValue(searchFor.get());
		assetListSearch.fill(tdSearch);
		buttonSearch.click();
	}*/

	public static List<String> getBottomWarningsList() {
		StaticElement messages = new StaticElement(By.xpath("//ul[@id='messages']"));
		return Arrays.asList(messages.getValue().split("\n"));
	}

	public static boolean warningsExist(String... warningMessages) {
		return getBottomWarningsList().containsAll(Arrays.asList(warningMessages));
	}

	public static void search(SearchEnum.SearchFor searchFor, SearchEnum.SearchBy searchBy, String searchString) {
		TestData searchTd = DataProviderFactory.emptyData().adjust(SearchMetaData.Search.SEARCH_FOR.getLabel(), searchFor.get()).adjust(searchBy.get(), searchString);
		search(DataProviderFactory.emptyData().adjust(assetListSearch.getName(), searchTd));
	}

	public static void search(TestData tdSearch) {
		//TODO-dchubkov: call search dialog instead?
		if (!buttonSearch.isPresent()) {
			MainPage.QuickSearch.buttonSearchPlus.click();
		}
		assetListSearch.fill(tdSearch);
		buttonSearch.click();
	}

	public static void openFirstResult() {
		tableSearchResults.getRow(1).getCell(1).controls.links.getFirst().click();
	}

	public static void openPolicy(String policyNum) {
		search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
	}

	public static void openQuote(String quoteNum) {
		search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNum);
	}

	public static void verifyWarningsExist(String... warningMessages) {
		verifyWarningsExist(true, warningMessages);
	}

	public static void verifyWarningsExist(boolean expectedValue, String... warningMessages) {
		String assertMessage = String.format("Warning messages are %1$s on search page.\nExpected warnings list is: %2$s\nActual warnings list is: %3$s.",
				expectedValue ? "absent" : "present", Arrays.toString(warningMessages), getBottomWarningsList());
		if (expectedValue) {
			CustomAssert.assertTrue(assertMessage, warningsExist(warningMessages));
		} else {
			CustomAssert.assertFalse(assertMessage, warningsExist(warningMessages));
		}
	}

	//TODO-dchubkov: add clear all search results method
}
