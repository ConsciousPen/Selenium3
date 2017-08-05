/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.customer.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;
import toolkit.webdriver.controls.composite.table.Table;

// TODO 1) Multiasset list has an empty locator; 2) Is it possible to avoid using indexes in getColumn(1).getCell(1) 3) assetlist is protected field. Why is used
// super.getAssetList() or getAssetlist()
public class AddAgencyActionTab extends ActionTab {

    public static Button buttonAddAsigment = new Button(By.xpath("//a[.= 'Add Assignment']"));
    public static Button buttonBrokerSearch = new Button(By.id("brokerSearchFromcrmCustomerBrokerCd:searchBtn"));
    public static Table tableBrokerSearchResults = new Table(By.id("brokerSearchFromcrmCustomerBrokerCd:body_brokerSearchResultscrmCustomerBrokerCd"));

    public AddAgencyActionTab() {
        super(CustomerMetaData.AddAgencyActionTab.class);
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
        buttonAddAsigment.click();
        super.getAssetList().fill(td);
        buttonBrokerSearch.click();
        tableBrokerSearchResults.getColumn(1).getCell(1).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
}
