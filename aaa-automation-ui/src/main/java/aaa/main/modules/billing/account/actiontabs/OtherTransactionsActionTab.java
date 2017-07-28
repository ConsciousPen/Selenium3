/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;

public class OtherTransactionsActionTab extends ActionTab {
    public OtherTransactionsActionTab() {
        super(BillingAccountMetaData.OtherTransactionsActionTab.class);
    }

    public static Button btnContinue = new Button(By.id("otherTxForm:okBtn"));
    public static Link linkAdvancedAllocation = new Link(By.id("otherTxForm:openAdvAllocationLnk"));

    @Override
    public Tab submitTab() {
        buttonOk.click();
        return this;
    }

}
