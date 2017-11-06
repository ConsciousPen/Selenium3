/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.paymentsmaintenance.actiontabs;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.PaymentsMaintenanceMetaData;

public class SearchSuspenseActionTab extends ActionTab {

	public static Table tableSuspenseSearchResults = new Table(By.xpath("//div[@id='suspenseSearch:suspenseSearchResults']//table"));
	public static Button buttonSearch = new Button(By.id("suspenseSearch:searchBtn"));

	public SearchSuspenseActionTab() {
		super(PaymentsMaintenanceMetaData.SearchSuspenseActionTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonSearch.click();
		if (tableSuspenseSearchResults.getRow(1).getCell(BillingConstants.BillingSuspenseSearchResultsTable.ACTION).getValue().contains("Clear")) {
			tableSuspenseSearchResults.getRow(1).getCell(BillingConstants.BillingSuspenseSearchResultsTable.ACTION).controls.links.get(ActionConstants.CLEAR).click();
		}
		return this;
	}
}
