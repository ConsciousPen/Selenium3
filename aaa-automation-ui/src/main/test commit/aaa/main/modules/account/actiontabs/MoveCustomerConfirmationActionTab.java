/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.AccountMetaData;
import toolkit.webdriver.controls.Button;

public class MoveCustomerConfirmationActionTab extends ActionTab {

    public static Button buttonMove = new Button(By.id("searchForm:moveBtn1"));

    public MoveCustomerConfirmationActionTab() {
        super(AccountMetaData.MoveCustomerConfirmationActionTab.class);
    }

    @Override
    public Tab submitTab() {
        buttonMove.click();
        Page.dialogConfirmation.confirm();
        return this;
    }
}
