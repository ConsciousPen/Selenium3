/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.billing;

import org.openqa.selenium.By;

import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.main.pages.summary.BillingSummaryPage;

public class PaymentsAndBillingMaintenancePage extends BillingSummaryPage {

	public static Table tableSuspensePayments = new Table(By.xpath("//div[@id='suspenseSearch:suspenseSearchResults']//table"));
	public static Button buttonSearhSuspense = new Button(By.id("suspenseSearch:searchBtn"));
	public static Button buttonAddPaymentBatch = new Button(By.id("backOfficeGeneralForm:addBatchPaymentBtn"));
	public static Button buttonAddBulkPayment = new Button(By.id("backOfficeGeneralForm:addBulkPaymentBtn"));
	public static Button buttonAddSuspense = new Button(By.id("backOfficeGeneralForm:addSuspenseBtn"));
	public static Button buttonClearSuspense = new Button(By.id("backOfficeGeneralForm:clearSuspenseBtn"));

	public static void open() {
		NavigationPage.toMainTab(AppMainTabs.BILLING.get());
	}
}
