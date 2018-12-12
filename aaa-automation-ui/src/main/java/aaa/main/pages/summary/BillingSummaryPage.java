/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary;

import java.time.LocalDateTime;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.components.Dialog;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.toolkit.webdriver.customcontrols.TableWithPages;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.ByT;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingSummaryPage extends SummaryPage {
	private static final ByT PAGINATION_LOCATOR = ByT.xpath("//table[@id='%s']/ancestor::tr[1]/following-sibling::tr[1]/descendant::span[1]");
	public static StaticElement labelBillingAccountNumber = new StaticElement(By.id("billingDetailedForm:general_info_table:0:accountNumber"));
	public static StaticElement labelBillingAccountName = new StaticElement(By.xpath("//*[@id='billingInfoForm']/div/table/tbody/tr[1]/td[1]//div[2]/span"));
	public static StaticElement labelBillingAccountStatus = new StaticElement(By.id("billingInfoForm:accountStatus"));
	public static Table tableBillingGeneralInformation = new Table(By.id("billingDetailedForm:general_info_table"));
	public static Table tableBillingAccountPolicies = new Table(By.id("billingDetailedForm:billing_policies_info_table"));
	public static TableWithPages tableInstallmentSchedule = new TableWithPages(By.id("billingDetailedForm:billing_installments_info_table"), PAGINATION_LOCATOR
		.format("billingDetailedForm:billing_installments_info_table"));
	public static TableWithPages tableBillsStatements = new TableWithPages(By.id("billingDetailedForm:billing_bills_statements"), PAGINATION_LOCATOR
		.format("billingDetailedForm:billing_bills_statements"));
	public static TableWithPages tablePaymentsOtherTransactions = new TableWithPages(By.id("billingDetailedForm:billing_transactions_active"), PAGINATION_LOCATOR
		.format("billingDetailedForm:billing_transactions_active"));
	public static Table tablePendingTransactions = new Table(By.id("billingDetailedForm:billing_transactions_pending"));
	public static Table tableBenefitAccounts = new Table(By.xpath("//div[@id='billingAccountListForm:group_billing_account_list_table']//table"));
	public static Table tableModalPremiums = new Table(By.xpath("//div[@id='modalPremiumForm:billing_modal_premium']//table"));
	public static Table tableBillingAccounts = new Table(By.xpath("//div[@id='billingAccountListForm:billing_account_list_table']//table"));
	public static Link linkUpdateBillingAccount = new Link(By.id("billingDetailedForm:updateBillingAccount"));
	//public static Link linkChangePaymentPlan = new Link(By.id("billingDetailedForm:billing_policies_info_table:0:changePaymentPlan"));
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
	public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
	protected static Logger log = LoggerFactory.getLogger(BillingSummaryPage.class);
	public static StaticElement labelEarnedPremiumWriteOff = new StaticElement(By.xpath("//*[@id='billingInfoForm:header_panel']/div/div[2]/label"));
	public static StaticElement labelAmountEarnedPremiumWriteOff = new StaticElement(By.id("billingInfoForm:epwo"));

	public static boolean isVisible() {
		return tableBillingGeneralInformation.isPresent() && tableBillingGeneralInformation.isVisible();
	}

	public static void open() {
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
	}

	public static void openPolicy(int rowNumber) {
		tableBillingAccountPolicies.getRow(rowNumber).getCell(1).controls.links.get(2).click();
	}

	public static void openPolicy(LocalDateTime effectiveDate) {
		tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.EFF_DATE, effectiveDate.format(DateTimeUtils.MM_DD_YYYY)).getCell(1).controls.links.get(2).click();
	}

	public static Dollar getInstallmentAmount(int intallmentIndex) {
		return new Dollar(tableInstallmentSchedule.getRow(intallmentIndex).getCell(2).getValue());
	}

	public static Dollar getTotalDue() {
		return new Dollar(tableBillingGeneralInformation.getRow(1).getCell(BillingConstants.BillingGeneralInformationTable.TOTAL_DUE).getValue());
	}

	public static Dollar getMinimumDue() {
		return new Dollar(tableBillingGeneralInformation.getRow(1).getCell(BillingConstants.BillingGeneralInformationTable.MINIMUM_DUE).getValue());
	}

	public static Dollar getPastDue() {
		return new Dollar(tableBillingGeneralInformation.getRow(1).getCell(BillingConstants.BillingGeneralInformationTable.PAST_DUE).getValue());
	}

	public static Dollar getTotalPaid() {
		return new Dollar(tableBillingGeneralInformation.getRow(1).getCell(BillingConstants.BillingGeneralInformationTable.TOTAL_PAID).getValue());
	}

	public static void showPriorTerms() {
		if (buttonShowPriorTerms.isPresent() && buttonShowPriorTerms.isVisible() && buttonShowPriorTerms.isEnabled()) {
			buttonShowPriorTerms.click();
		}
	}

	public static void hidePriorTerms() {
		if (buttonHidePriorTerms.isPresent() && buttonHidePriorTerms.isVisible() && buttonHidePriorTerms.isEnabled()) {
			buttonHidePriorTerms.click();
		}
	}

	public static Dollar getBillableAmount() {
		return new Dollar(tableBillingGeneralInformation.getRow(1).getCell(BillingConstants.BillingGeneralInformationTable.BILLABLE_AMOUNT).getValue());
	}

	public static LocalDateTime getInstallmentDueDate(int index) {
		return TimeSetterUtil.getInstance().parse(tableInstallmentSchedule.getRow(index).getCell(BillingConstants.BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getValue(),
			DateTimeUtils.MM_DD_YYYY);
	}
}
