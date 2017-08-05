/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;

public class TransferPaymentActionTab extends ActionTab {
    public static TextBox textBoxAllocatedAmount = new TextBox(By.id("transferPaymentForm:policyAllocationResults:0:amountInput"));

    public TransferPaymentActionTab() {
        super(BillingAccountMetaData.TransferPaymentActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        String allocatedAmountLabel = BillingAccountMetaData.TransferPaymentActionTab.ALLOCATED_AMOUNT.getLabel();
        String allocatedAmount = td.getValue(getMetaKey(), allocatedAmountLabel);

        td.mask(TestData.makeKeyPath(getMetaKey(), allocatedAmountLabel));

        super.fillTab(td);

        textBoxAllocatedAmount.setValue(allocatedAmount);

        return this;
    }
}
