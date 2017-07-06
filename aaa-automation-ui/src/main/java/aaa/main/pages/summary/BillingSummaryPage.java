/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.components.Dialog;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingSummaryPage extends SummaryPage {

    public static StaticElement labelBillingAccountNumber = new StaticElement(By.id("billingInfoForm:accountNumber"));
    public static StaticElement labelBillingAccountName = new StaticElement(By.xpath("//*[@id='billingInfoForm']/div/table/tbody/tr[1]/td[1]//div[2]/span"));
    public static StaticElement labelBillingAccountStatus = new StaticElement(By.id("billingInfoForm:accountStatus"));

    public static Table tableBillingGeneralInformation = new Table(By.id("billingDetailedForm:general_info_table"));
    public static Table tableBillingAccountPolicies = new Table(By.id("billingDetailedForm:billing_policies_info_table"));
    public static Table tableInstallmentSchedule = new Table(By.id("billingDetailedForm:billing_installments_info_table"));
    public static Table tableBillsStatements = new Table(By.id("billingDetailedForm:billing_bills_statements"));
    public static Table tablePaymentsOtherTransactions = new Table(By.id("billingDetailedForm:billing_transactions_active"));
    public static Table tablePendingTransactions = new Table(By.id("billingDetailedForm:billing_transactions_pending"));
    public static Table tableBenefitAccounts = new Table(By.xpath("//div[@id='billingAccountListForm:group_billing_account_list_table']//table"));
    public static Table tableModalPremiums = new Table(By.xpath("//div[@id='modalPremiumForm:billing_modal_premium']//table"));
    public static Table tableBillingAccounts = new Table(By.xpath("//div[@id='billingAccountListForm:billing_account_list_table']//table"));

    public static Link linkUpdateBillingAccount = new Link(By.id("billingDetailedForm:updateBillingAccount"));
    public static Link linkAcceptPayment = new Link(By.id("billingDetailedForm:acceptPayment"));
    public static Link linkOtherTransactions = new Link(By.id("billingDetailedForm:otherTransactions"));
    public static Link linkAdvancedAllocation = new Link(By.id("paymentForm:openAdvAllocationLnk"));
    public static Link linkBillingAccountStatus = new Link(By.id("billingInfoForm:accountStatusLink"));

    public static Dialog dialogDiscardBill = new Dialog("//div[@id='discardStConfirmDialog_container']");
    public static Dialog dialogWaiveFee = new Dialog("//div[@id='generalConfirmDialogForm:genericConfirmDialog']");
    public static Dialog dialogApprovePendingTransaction = new Dialog("//div[@id='generalConfirmDialogForm:genericConfirmDialog']");

    public static Button buttonShowPriorTerms = new Button(By.id("billingDetailedForm:showPriorTerms"));
    public static Button buttonHidePriorTerms = new Button(By.id("billingDetailedForm:hidePriorTerms"));
    public static Button buttonPaymentsBillingMaintenance = new Button(By.id("billingInfoForm:backOffice"));
    public static Button buttonAddBulkPayment = new Button(By.id("backOfficeGeneralForm:addBulkPaymentBtn"));
    public static Button buttonAddSuspense = new Button(By.id("backOfficeGeneralForm:addSuspenseBtn"));
    public static Button buttonClearSuspense = new Button(By.id("backOfficeGeneralForm:clearSuspenseBtn"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));

    public static void open() {
        NavigationPage.toMainTab(AppMainTabs.BILLING.get());
    }

    public static Dollar getInstallmentAmount(int intallmentIndex) {
        return new Dollar(tableInstallmentSchedule.getRow(intallmentIndex).getCell(2).getValue());
    }

    public static void openAccountPolicy(int rowNumber) {
        BillingSummaryPage.tableBillingAccountPolicies.getRow(rowNumber).getCell(1).controls.links.get(2).click();
    }
}
