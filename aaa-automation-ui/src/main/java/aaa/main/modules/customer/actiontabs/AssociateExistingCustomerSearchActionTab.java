/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.table.Table;

// TODO multiasset list has an empty locator
public class AssociateExistingCustomerSearchActionTab extends ActionTab {

    public static Table tableSearchForGroups = new Table(By.id("customerSearchForGroupsFrom:searchResultTable"));

    public AssociateExistingCustomerSearchActionTab() {
        super(CustomerMetaData.AssociateExistingCustomerSearchActionTab.class);
        assetList = new MultiAssetList(By.xpath("//*"), metaDataClass) {

            @Override
            protected void addSection(int index, int size) {
                // TODO Auto-generated method stub

            }

            @Override
            protected void selectSection(int index) {
                // TODO Auto-generated method stub

            }

        };
    }

    @Override
    public Tab fillTab(TestData td) {
        super.getAssetList().fill(td);
        Page.dialogSearch.buttonSearch.click();
        tableSearchForGroups.getColumn(5).getCell(1).controls.links.getFirst().click();
        return this;
    }
}
