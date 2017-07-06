/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.billing.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;

public class AcceptPaymentActionTab extends ActionTab {

    public AcceptPaymentActionTab() {
        super(BillingAccountMetaData.AcceptPaymentActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {

        String allocationsLabel = BillingAccountMetaData.AcceptPaymentActionTab.ALLOCATIONS.getLabel();
        TestData tabTestData = td.getTestData(getMetaKey());
        if (tabTestData.containsKey(allocationsLabel)) {

            td.mask(TestData.makeKeyPath(getMetaKey(), allocationsLabel));

            super.fillTab(td);

            int i = 0;
            for (String value : tabTestData.getList(allocationsLabel)) {
                new TextBox(By.id(String.format("paymentForm:invoicesDistributionsTable:%d:amount", i))).setValue(value);
                i++;
            }
            td.ksam(TestData.makeKeyPath(getMetaKey(), allocationsLabel));
        } else {
            super.fillTab(td);
        }

        return this;
    }
}
