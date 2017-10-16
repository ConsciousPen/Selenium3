/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.BillingAccountMetaData;
import org.openqa.selenium.By;
import toolkit.webdriver.controls.composite.table.Table;

public class ChangePaymentPlanActionTab extends ActionTab {

    public ChangePaymentPlanActionTab() {
        super(BillingAccountMetaData.ChangePaymentPlanActionTab.class);
    }

    public Table tableCurrentInstallmentSchedule = new Table(By.id("billingActionForm:billing_installments_info_table"));
    public Table tableNewInstallmentSchedule = new Table(By.id("billingActionForm:billing_installments_info_table2"));

    @Override
    public Tab submitTab() {
        Tab.buttonOk.click();
        if (Page.dialogConfirmation.isPresent()) {
            Page.dialogConfirmation.confirm();
        }
        return this;
    }
}
