/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.account.actiontabs;

import org.openqa.selenium.By;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.AccountMetaData;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;

public class AddAffinityGroupActionTab extends ActionTab {

    public static Button buttonAddAffinityGroup = new Button(By.xpath("//input[@value='Add Affinity Group']"));

    public AddAffinityGroupActionTab() {
        super(AccountMetaData.AddAffinityGroupActionTab.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        assetList.fill(td);
        buttonAddAffinityGroup.click();
        return this;
    }
}
