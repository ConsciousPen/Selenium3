/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.webdriver.controls.composite.table.Table;

public class RefundActionTab extends ActionTab {

    public static Table tblAllocations = new Table(By.id("paymentForm:policyAllocationResults"));

    public RefundActionTab() {
        super(BillingAccountMetaData.RefundActionTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }
}
