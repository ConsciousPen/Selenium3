/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.paymentsmaintenance.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.PaymentsMaintenanceMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.table.Table;

public class ClearSuspenseActionTab extends ActionTab {
    public static Table tableSuspensePayments = new Table(By.xpath("//thead[@id='suspenseSearch:suspenseSearchResults_head']/.."));

    public ClearSuspenseActionTab() {
        super(PaymentsMaintenanceMetaData.ClearSuspenseActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);

        String paymentAmounts = td.getValue(ClearSuspenseActionTab.class.getSimpleName(), "Payment Amount");

        int i = 0;
        for (String value : paymentAmounts.split(",")) {
            new TextBox(By.id(String.format("suspenseForm:policyAllocationResults:%d:amountInput", i))).setValue(value);
            i++;
        }

        return this;
    }
}
