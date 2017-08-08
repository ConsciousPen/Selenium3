/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.openqa.selenium.By;
import aaa.main.enums.SearchEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.toolkit.webdriver.customcontrols.TableWithPages;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class SearchPage extends MainPage {
	public static AbstractContainer<TestData, TestData> assetListSearch = new AssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), SearchMetaData.Search.class).applyConfiguration("Search");

	public static Button buttonClear = new Button(By.id("searchForm:clearBtn"));
	public static Button buttonCreateCustomer = new Button(By.id("searchForm:createAccountBtnAlway"));
	public static Button buttonSearch = new Button(By.id("searchForm:searchBtn"));
	public static Table tableSearchResults = new TableWithPages(By.id("searchTable1Form:body_searchTable1"), By.id("searchTable1Form:body_searchTable1:dataScrollersearchTable1"));
	public static StaticElement labelNameParty = new StaticElement(By.xpath("//span[@id='partyPopup:name']"));
	public static Table tableRoleInfo = new Table(By.xpath("//table[@id='partyPopup:body_rolesTable']"));

	public static List<String> getBottomWarningsList() {
		StaticElement messages = new StaticElement(By.xpath("//ul[@id='messages']"));
		return Arrays.asList(messages.getValue().split("\n"));
	}

	public static boolean warningsExist(String... warningMessages) {
		return getBottomWarningsList().containsAll(Arrays.asList(warningMessages));
	}

	public static void search(SearchEnum.SearchFor searchFor, SearchEnum.SearchBy searchBy, String searchString) {
		TestData searchTd = DataProviderFactory.dataOf(SearchMetaData.Search.SEARCH_FOR.getLabel(), searchFor.get()).adjust(searchBy.get(), searchString);
		search(DataProviderFactory.dataOf(assetListSearch.getName(), searchTd));
	}

	public static void search(TestData tdSearch) {
		log.info("Searching with criteria: " + tdSearch);
		//TODO-dchubkov: call search dialog instead?
		if (!buttonSearch.isPresent()) {
			MainPage.QuickSearch.buttonSearchPlus.click();
		}
		assetListSearch.fill(tdSearch);
		buttonSearch.click();
	}

	public static void openFirstResult() {
		selectSearchedResult(1);
	}

	public static void selectSearchedResult(int index) {
		tableSearchResults.getRow(index).getCell(1).controls.links.getFirst().click();
	}

	public static void openPolicy(String policyNum) {
		search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
	}

	public static void openQuote(String quoteNum) {
		search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNum);
	}

	public static void clear() {
		buttonClear.click();
	}

	//---------- verify methods ----------

	public static void verifyWarningsExist(String... warningMessages) {
		verifyWarningsExist(true, warningMessages);
	}

	public static void verifyWarningsExist(boolean expectedValue, String... warningMessages) {
		if (expectedValue) {
			CustomAssert.assertTrue("Warning message(s) is(are) absent on search page: " + CollectionUtils.removeAll(Arrays.asList(warningMessages), getBottomWarningsList()),
					warningsExist(warningMessages));
		} else {
			Collection<String> existedWarnings = CollectionUtils.retainAll(getBottomWarningsList(), Arrays.asList(warningMessages));
			CustomAssert.assertTrue("Unexpeted warning message(s) is(are) present on search page: " + existedWarnings, existedWarnings.isEmpty());
		}
	}
}
