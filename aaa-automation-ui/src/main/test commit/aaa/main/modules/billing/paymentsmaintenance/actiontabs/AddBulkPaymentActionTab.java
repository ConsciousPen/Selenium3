/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.paymentsmaintenance.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.PaymentsMaintenanceMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.RadioButton;

public class AddBulkPaymentActionTab extends ActionTab {

    public static RadioButton radioButtonAllacationAction = new RadioButton(By.id("bulkPaymentForm:allocation_Action_radio:0"));

    public AddBulkPaymentActionTab() {
        super(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.getTestData(getMetaKey()).containsKey(PaymentsMaintenanceMetaData.AddBulkPaymentActionTab.ALLOCATION.getLabel())) {
            radioButtonAllacationAction.setValue(true);
        }
        return super.fillTab(td);
    }
}
