/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.billing.account.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.DefaultTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.BillingAccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class BillingAccountTab extends DefaultTab {

    public static Link linkManageInvocingCalendars = new Link(By.linkText("Manage Invoicing Calendars"));
    public static Button buttonAddInvoicingCalendar = new Button(By.xpath("//input[@value='Add Invoicing Calendar']"));

    public BillingAccountTab() {
        super(BillingAccountMetaData.BillingAccountTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void selectSection(int index) {}

            @Override
            protected void setSectionValue(int index, TestData value) {
                if (index == 2) {
                    buttonSave.click();
                    buttonBack.click();
                }
                super.setSectionValue(index, value);
            }

            @Override
            protected void addSection(int index, int size) {
                if (index == 1) {
                    linkManageInvocingCalendars.click();
                    buttonAddInvoicingCalendar.click();
                }
            }
        };

        assetList.fill(td);
        return this;
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
