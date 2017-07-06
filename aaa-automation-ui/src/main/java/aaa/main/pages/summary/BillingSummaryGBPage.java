/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingSummaryGBPage extends BillingSummaryPage {

    public static Table tableBillingGeneralInformation = new Table(By.xpath("//div[@id='billingDetailedForm:general_info_table']//table"));
    public static Table tableBillsAndStatments = new Table(By.xpath("//div[@id='billingDetailedForm:benefits_invoices_table']//table"));
    public static Table tablePaymentsOtherTransactions = new Table(By.xpath("//div[@id='billingDetailedForm:billing_transactions_active']//table"));

    public static Button buttonAddPaymentBatch = new Button(By.id("backOfficeGeneralForm:addBatchPaymentBtn"));

    public static void open() {
        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
    }
}
