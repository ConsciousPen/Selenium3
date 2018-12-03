/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import aaa.common.enums.NavigationEnum;
import aaa.common.metadata.SearchMetaData;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.toolkit.webdriver.customcontrols.TableWithPages;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class SearchPage extends MainPage {
	public static final String LABEL_SEARCH_LOCATOR = "//table[@id='searchForm:entityTypeSelect']//label[contains(.,'%s')]";
	public static final String LABEL_SEARCH = "Search For";
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
		TestData searchTd = DataProviderFactory.dataOf(LABEL_SEARCH, searchFor.get()).adjust(searchBy.get(), searchString);
		search(DataProviderFactory.dataOf(assetListSearch.getName(), searchTd));
	}

	public static void search(TestData tdSearch) {
		log.info("Searching with criteria: {}", tdSearch);
		//TODO-dchubkov: call search dialog instead?
		if (!buttonSearch.isPresent()) {
			MainPage.QuickSearch.buttonSearchPlus.click();
		}
		String searchFor = tdSearch.getTestData(SearchMetaData.Search.class.getSimpleName()).getValue(LABEL_SEARCH);
		if (StringUtils.isNotBlank(searchFor)) {
			Button buttonTab = new Button(By.xpath(String.format(LABEL_SEARCH_LOCATOR, searchFor)));
			buttonTab.setName(LABEL_SEARCH);
			assetListSearch.addAsset(buttonTab);
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
		openPolicy(policyNum, ProductConstants.PolicyStatus.POLICY_ACTIVE);
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.POLICY.get())) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		}
	}

	public static void openPolicy(String policyNum, String status) {
		search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
		if (tableSearchResults.isPresent()) {
			tableSearchResults.getRow("Status", status).getCell(1).controls.links.getFirst().click();
		}
	}

	public static void openQuote(String quoteNum) {
		search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNum);
	}

	public static void openBilling(String policyNum) {
		search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.BILLING.get())) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		}
	}

	public static void openCustomer(String customerNum) {
		search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, customerNum);
	}

	public static void clear() {
		buttonClear.click();
	}

	//---------- verify methods ----------

	public static void verifyWarningsExist(ETCSCoreSoftAssertions softly, String... warningMessages) {
		verifyWarningsExist(softly, true, warningMessages);
	}

	public static void verifyWarningsExist(ETCSCoreSoftAssertions softly, boolean expectedValue, String... warningMessages) {
		if (expectedValue) {
			softly.assertThat(warningsExist(warningMessages))
					.as("Warning message(s) is(are) absent on search page: " + CollectionUtils.removeAll(Arrays.asList(warningMessages), getBottomWarningsList())).isTrue();
		} else {
			Collection<String> existedWarnings = CollectionUtils.retainAll(getBottomWarningsList(), Arrays.asList(warningMessages));
			softly.assertThat(existedWarnings).as("Unexpeted warning message(s) is(are) present on search page: " + existedWarnings).isEmpty();
		}
	}
}
