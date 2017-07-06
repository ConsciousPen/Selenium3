/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.AccountMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class MoveCustomerSearchActionTab extends ActionTab {

    public static Button buttonCreateNewAccount = new Button(By.xpath("//a[.='Create New Account']"));
    public static Table tableSearchResults = new Table(By.id("searchTable1Form:body_searchTable1"));
    public static Button buttonLastCustomerConfirmation = new Button(By.id("lastCustomerConfirmDialog_form:buttonYes"));

    public MoveCustomerSearchActionTab() {
        super(AccountMetaData.MoveCustomerSearchActionTab.class);
    }

    @Override
    public Tab submitTab() {
        SearchPage.buttonSearch.click();
        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow(1).getCell(1).controls.links.getFirst().click();
        } else if (buttonCreateNewAccount.isPresent()) {
            buttonCreateNewAccount.click();
            Page.dialogConfirmation.confirm();
            buttonLastCustomerConfirmation.click();
        }
        return this;
    }
}
